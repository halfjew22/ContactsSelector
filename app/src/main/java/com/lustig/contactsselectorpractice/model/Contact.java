package com.lustig.contactsselectorpractice.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lustig.contactsselectorpractice.ThisApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by lustig on 5/15/15.
 */
public class Contact implements Comparable<Contact> {

    private String mName;
    private String mNumber;

    private boolean mIsSelected;

    public Contact(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public Contact(String name, String number, boolean isSelected) {
        mName = name;
        mNumber = number;
        mIsSelected = isSelected;
    }

    public String getName() {
        return mName;
    }

    public String getNumber() {
        return mNumber;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public int compareTo(Contact otherStudent) {

        String otherStudentName = otherStudent.getName();

        return this.getName().compareToIgnoreCase(otherStudentName);
    }


    public static class Helper {

        public static String PREFS_TAG = "Contacts";

        public static ArrayList<Contact> getAllContacts() {

            ArrayList<Contact> contacts;

            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(ThisApplication.getThisApplicationContext());
            String jsonContacts = appSharedPrefs.getString(Helper.PREFS_TAG, "NONE");

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Contact>>() {

            }.getType();
            contacts = gson.fromJson(jsonContacts, type);

            return contacts;
        }

        public static void saveContacts(ArrayList<Contact> contactsToSave) {

            Gson gson = new Gson();
            String jsonContacts = gson.toJson(contactsToSave);

            Log.d("Lustig", "jsonContacts = " + jsonContacts);

            // Put the Json String into SharedPreferences
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(ThisApplication.getThisApplicationContext());
            SharedPreferences.Editor editor = appSharedPrefs.edit();

            editor.putString(Helper.PREFS_TAG, jsonContacts);
            editor.commit();

        }

    }

}
