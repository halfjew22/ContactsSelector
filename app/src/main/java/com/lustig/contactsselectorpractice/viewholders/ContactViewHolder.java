package com.lustig.contactsselectorpractice.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lustig.contactsselectorpractice.R;


public class ContactViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout root;

    public CheckBox checkBox;

    public TextView textViewName;
    public TextView textViewNumber;

    public ContactViewHolder(View itemView) {
        super(itemView);

        root = (RelativeLayout) itemView.findViewById(R.id.rootLayout);

        checkBox = (CheckBox) itemView.findViewById(R.id.contactSelectedCheckBox);

        textViewName = (TextView) itemView.findViewById(R.id.tvName);
        textViewNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
    }
}
