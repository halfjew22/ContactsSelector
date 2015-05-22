package com.lustig.contactsselectorpractice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lustig.contactsselectorpractice.adapters.ListAdapter;
import com.lustig.contactsselectorpractice.interfaces.OnContactsLoadCompleteListener;
import com.lustig.contactsselectorpractice.model.Contact;

import java.util.ArrayList;



public class ContactSelectorActivity extends ActionBarActivity implements OnContactsLoadCompleteListener, SearchView.OnQueryTextListener {

    ArrayList<Contact> mContacts;

    ListAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private EditText mEditText;

    private ArrayList<Contact> mSearchResultsArray = new ArrayList<Contact>();

    int searchTextLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_selector);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        // Waits for Application to load contacts
        ThisApplication.setOnContactsLoadedListener(this);

//        mEditText = (EditText) findViewById(R.id.editText);
//
//        mEditText.setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//
//                        if (mEditText.getText().length() != 0) {
//                            mEditText.setSelection(mEditText.getText().length());
//                        }
//                    }
//                });
//
//        mEditText.setOnFocusChangeListener(
//                new View.OnFocusChangeListener() {
//
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//
//                        if (hasFocus) {
//                            Log.d("Lustig", "hasFocus!");
//
//                            mEditText.postDelayed(
//                                    new Runnable() {
//
//                                        @Override
//                                        public void run() {
//                                            InputMethodManager keyboard = (InputMethodManager)
//                                                    getSystemService(Context.INPUT_METHOD_SERVICE);
//                                            keyboard.showSoftInput(mEditText, 0);
//                                        }
//                                    }, 200);
//
//                        } else {
//                            Log.d("Lustig", "does NOT have focus!");
//                        }
//                    }
//                });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                        super.onScrollStateChanged(recyclerView, newState);

                        Log.d("Lustig", "onScrollStateChanged");
                        hideKeyboard(ContactSelectorActivity.this);
                    }
                });

        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        Log.d("Lustig", "onTouch root layout");

                        hideKeyboard(ContactSelectorActivity.this);

                        return false;
                    }
                });

//        mEditText.addTextChangedListener(
//                new TextWatcher() {
//
//                    public void afterTextChanged(Editable s) {
//
//                    }
//
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        Log.d("Lustig", "onTextChanged");
//
//                        mSearchResultsArray.clear();
//
//                        searchTextLength = mEditText.getText().toString().trim().length();
//
//                        for (Contact contact : mContacts) {
//
//                            String name = contact.getName();
//
//                            String[] names = name.split(" ");
//
//                            for (String singleName : names) {
//
//                                if (searchTextLength <= singleName.length()) {
//
//                                    // ToDo implement a Trie to improve performance beyond O(N)
//
//                                    // This block of code searches for strings /starting with/ the search string. The above searches for contacts that contain the search string.
//                                    if (mEditText.getText().toString().trim().equalsIgnoreCase((String) singleName.subSequence(0, searchTextLength))) {
//                                        mSearchResultsArray.add(contact);
//
//                                        // If a Contact is added, I don't want to add duplicates
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//
//                        mAdapter = new ListAdapter(mSearchResultsArray, ContactSelectorActivity.this);
//                        mRecyclerView.swapAdapter(mAdapter, true);
//                    }
//                });
    }

    public void highlightText() {

        mEditText.requestFocus();
        mEditText.selectAll();

        // If the user has searched for a Contact, highlight AND open keyboard
        // Otherwise, just highlight the text and prepare for the event the user
        // might want to search for a different Contact
        if (mEditText.getText().length() != 0) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
        }
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

//        mEditText.requestFocus();
    }

    private void setUpRecyclerView() {

        mAdapter = new ListAdapter(mContacts, this);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void hideKeyboard(Context activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("Main", "Text Changed " + newText);
        return false;
    }
}



