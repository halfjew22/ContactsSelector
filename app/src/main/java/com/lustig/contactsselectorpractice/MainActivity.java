package com.lustig.contactsselectorpractice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                        //super.onScrolled(recyclerView, dx, dy);
                        hideKeyboard(MainActivity.this);

                    }
                });

        mEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("Lustig", "onTextChanged");

                searchTextLength = mEditText.getText().length();
                mSearchResultsArray.clear();

                for (Contact contact : mContacts) {

                    String name = contact.getName();

                    String[] names = name.split(" ");

                    String firstName = names[0];
                    String lastName = names[names.length - 1];

                    for (String singleName : names) {

                        if (searchTextLength <= singleName.length()) {

//                        if (name.toLowerCase().contains(s.toString().toLowerCase())) {
//                            mSearchResultsArray.add(contact);
//                        }

                            Log.d("Lustig", "First name: " + firstName);
                            Log.d("Lustig", "Last name: " + lastName);

                            // ToDo implement a Trie to improve performance beyond O(N)

                            // This block of code searches for strings /starting with/ the search string. The above searches for contacts that contain the search string.
                            if (mEditText.getText().toString().equalsIgnoreCase((String) singleName.subSequence(0, searchTextLength))) {
                                mSearchResultsArray.add(contact);

                                // If a Contact is added, I don't want to add duplicates
                                break;
                            }
                        }
                    }
                }

                mAdapter = new ListAdapter(mSearchResultsArray, MainActivity.this);
                mRecyclerView.swapAdapter(mAdapter, true);
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

//        ArrayList<Contact> contacts = mAdapter.getContacts();
        Contact.Helper.saveContacts(mContacts);
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

            // ToDo Add loading spinner if contacts are being loaded
            mContacts = new ArrayList<Contact>();

            setUpRecyclerView();
        }
    }

    private void setUpRecyclerView() {

        mAdapter = new ListAdapter(mContacts, this);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void hideKeyboard(Context activity) {

        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(((Activity) activity).getCurrentFocus().getWindowToken(), 0);


//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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



