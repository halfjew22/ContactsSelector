package com.lustig.contactsselectorpractice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lustig.contactsselectorpractice.adapters.ListAdapter;
import com.lustig.contactsselectorpractice.interfaces.OnContactsLoadCompleteListener;
import com.lustig.contactsselectorpractice.model.Contact;

import java.util.ArrayList;

/**
 * Here's the URL where this code is from:
 * http://saigeethamn.blogspot.in/2011/05/contacts-api-20-and-above-android.html
 */

public class MainActivity extends ActionBarActivity implements OnContactsLoadCompleteListener {

    ArrayList<Contact> mContacts;

    ListAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private EditText mEditText;

    private ArrayList<Contact> mSearchResultsArray = new ArrayList<Contact>();

    int searchTextLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Waits for Application to load contacts
        ThisApplication.setOnContactsLoadedListener(this);

        mEditText = (EditText) findViewById(R.id.editText);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mEditText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (!hasFocus) {
                            hideKeyboard(v);
                        }
                    }
                });


        // By using setAdpater method in listview we an add string array in list.
//        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListViewArray));

        mEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("Lustig", "onTextChanged");

                searchTextLength = mEditText.getText().length();
                mSearchResultsArray.clear();

                for (Contact contact : mContacts) {

                    String name = contact.getName();

                    if (searchTextLength <= name.length()) {
                        if (mEditText.getText().toString().equalsIgnoreCase((String) name.subSequence(0, searchTextLength))) {
                            mSearchResultsArray.add(contact);
                        }
                    }

                    // TODO IMPORTANT. COME BACK HERE AND FIGURE OUT WHY THIS ISN'T WORKING PROPERLY
                    // You aren't setting the adapter to the recycler view, and even if you were, it's not in the right place. Find that article

                    mAdapter = new ListAdapter(mSearchResultsArray, MainActivity.this);
                }
            }
        });
    }

    public void clearEditText() {

        mEditText.setText("");
    }

    @Override
    protected void onPause() {

        super.onPause();

        Log.d("Lustig", "MainActivity: onPause");
        Log.d("Lustig", "Should I save the selectedContacts here?");

        ArrayList<Contact> contacts = mAdapter.getContacts();
        Contact.Helper.saveContacts(contacts);
    }

    @Override
    protected void onResume() {

        super.onResume();

        // Contacts have loaded before this point and it is safe to set up RecyclerView
        if (ThisApplication.haveContactsLoaded) {
            mContacts = Contact.Helper.getAllContacts();

            setUpRecyclerView();

            /**
             * If the contacts haven't loaded, I need to initialize an empty RecyclerView
             */
        } else {

            mContacts = new ArrayList<Contact>();

            setUpRecyclerView();
        }
    }

    private void setUpRecyclerView() {

        mAdapter = new ListAdapter(mContacts, this);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onContactsLoadComplete() {

        Log.d("Lustig", "It's OK to load contacts into view now");
        mContacts = Contact.Helper.getAllContacts();

        Log.d("Lustig", "contacts size: " + mContacts.size() + " from MainActivity");

        runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {

                        setUpRecyclerView();
                    }
                });
    }
}



