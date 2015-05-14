package com.lustig.contactsselectorpractice;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.HashMap;

/**
 * Here's the URL where this code is from:
 * http://saigeethamn.blogspot.in/2011/05/contacts-api-20-and-above-android.html
 */

public class MainActivity extends ActionBarActivity {

    HashMap<String, String> mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContacts = new HashMap<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    pCur.moveToFirst();

                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phoneNo = phoneNo.replaceAll("[\\D]", "");

                    Log.d("Lustig", "Phone Number:" + phoneNo  + ",\tName:" + name);
                    mContacts.put(name, phoneNo);
                    pCur.close();
                }
            }

            Log.d("Lustig", "Size of contacts list: " + mContacts.size());
        }
    }
}
