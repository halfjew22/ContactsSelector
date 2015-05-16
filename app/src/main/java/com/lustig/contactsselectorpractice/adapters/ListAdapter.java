package com.lustig.contactsselectorpractice.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.lustig.contactsselectorpractice.MainActivity;
import com.lustig.contactsselectorpractice.R;
import com.lustig.contactsselectorpractice.model.Contact;
import com.lustig.contactsselectorpractice.viewholders.ItemHolder;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by lustig on 5/14/15.
 */
public class ListAdapter extends RecyclerView.Adapter<ItemHolder> {

    private Set<String> mKeySet;

    // This will hold all of the contacts
    ArrayList<Contact> mContacts;

    // This will hold all of the /selected/ contacts
    ArrayList<Contact> mSelectedContacts;

    private LayoutInflater mInflater;

    Context mContext;

    public ListAdapter(ArrayList<Contact> contacts, Context context) {

        mContacts = contacts;
        mSelectedContacts = new ArrayList<Contact>();

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = mInflater.inflate(R.layout.list_item, viewGroup, false);
        ItemHolder holder = new ItemHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemHolder viewHolder, final int position) {

        // Gets the Contact from the ArrayList for the current position
        final Contact contact = mContacts.get(position);

        final CheckBox checkBox = viewHolder.checkBox;

        String currentName = contact.getName();
        String currentPhoneNumber = contact.getNumber();

        boolean isChecked = contact.isSelected();

        viewHolder.textViewName.setText(currentName);
        viewHolder.textViewNumber.setText(currentPhoneNumber);

        viewHolder.textViewNumber.setTypeface(viewHolder.textViewNumber.getTypeface(), Typeface.ITALIC);

        checkBox.setChecked(isChecked);

        checkBox.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {



                        if (checkBox.isChecked()) {
                            addToSelectedContacts(contact);
                        } else {
                            removeFromSelectedContacts(contact);
                        }

                    }
                });

        viewHolder.root.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Log.d("Lustig", "onClick root of list_item");

                        if (!checkBox.isChecked()) {

                            checkBox.setChecked(true);

                            addToSelectedContacts(contact);

                        } else {

                            checkBox.setChecked(false);

                            removeFromSelectedContacts(contact);
                        }

                        ((MainActivity) mContext).clearEditText();
                    }
                });

    }

    public void addToSelectedContacts(Contact contactToAdd) {

        // First, set that this contact is now selected
        contactToAdd.setSelected(true);

        // Then, add contact to selected contacts list
        mSelectedContacts.add(contactToAdd);

    }

    public void removeFromSelectedContacts(Contact contactToRemove) {

        // First, set that this Contact is no longer selected
        contactToRemove.setSelected(false);

        // Then, remove the contact from the selected contacts list
        mSelectedContacts.remove(contactToRemove);
    }

    @Override
    public int getItemCount() {

        return mContacts.size();
    }

    public ArrayList<Contact> getSelectedContacts() {
        return mSelectedContacts;
    }

    public ArrayList<Contact> getContacts() {
        return mContacts;
    }
}
