package com.lustig.contactsselectorpractice;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.lustig.contactsselectorpractice.interfaces.OnContactsLoadCompleteListener;
import com.lustig.contactsselectorpractice.model.Contact;

import java.util.ArrayList;
import java.util.Collections;

public class ThisApplication extends Application{

    public static boolean haveContactsLoaded = false;

    public static Context mContext;

//    public static final String PREFS_TAG_CONTACTS = "Contacts";

    private static OnContactsLoadCompleteListener mContactsLoadedListener;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        Log.d("Lustig", "in onCreate application");

        loadContacts();
    }

    public static Context getThisApplicationContext() {
        return mContext;
    }

    private void loadContacts() {

        final ArrayList<Contact> contacts = new ArrayList<Contact>();

        // Try getting from SharedPreferences first

        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
//
//        Gson gson = new Gson();

        String jsonContacts = appSharedPrefs.getString(Contact.Helper.PREFS_TAG, "NONE");

        if (jsonContacts.equals("NONE")) {
            Log.d("Lustig", "No contacts found, loading from scratch");
        } else {
            Log.d("Lustig", "Contacts are already loaded - ThisApplication");
            haveContactsLoaded = true;
            return;
        }

        // Load the contacts from scratch in its own thread since it takes some time

        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        ContentResolver cr = getContentResolver();
                        Cursor cur = cr.query(
                                ContactsContract.Contacts.CONTENT_URI,
                                null, null, null, null);

                        if (cur.getCount() > 0) {

                            while (cur.moveToNext()) {

                                String id = cur.getString(
                                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                                String name = cur.getString(
                                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                                if (Integer.parseInt(
                                        cur.getString(
                                                cur.getColumnIndex(
                                                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                                    Cursor pCur = cr.query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{id}, null);

                                    pCur.moveToFirst();

                                    String phoneNo = pCur
                                            .getString(
                                                    pCur.getColumnIndex(
                                                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    phoneNo = phoneNo.replaceAll("[\\D]", "");
                                    name = name.replaceAll("[^a-zA-Z_0-9\\s]", "");

                                    Contact c = new Contact(name, phoneNo, false);

                                    contacts.add(c);
                                    pCur.close();
                                }
                            }
                        }

                        Collections.sort(contacts);

                        Gson gson = new Gson();
                        String jsonContacts = gson.toJson(contacts);

                        Log.d("Lustig", "jsonContacts = " + jsonContacts);

                        // Put the Json String into SharedPreferences
                        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(ThisApplication.this);
                        SharedPreferences.Editor editor = appSharedPrefs.edit();

                        editor.putString(Contact.Helper.PREFS_TAG, jsonContacts);
                        editor.commit();

                        mContactsLoadedListener.onContactsLoadComplete();

                        // Contacts are now stored in SharedPreferences

                    }
                }).start();
    }

    public static void setOnContactsLoadedListener(OnContactsLoadCompleteListener listener) {
        mContactsLoadedListener = listener;
    }
}
