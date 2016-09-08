package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.database.Cursor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 05/09/2016.
 */

public class FindSuggestionsAdapter extends CursorAdapter {
    public static final int COL_NAME = 1;
    public static final int COL_ADDRESS = 2;
    public static final int COL_LOCALITY = 3;
    public static final int COL_PROVINCE = 4;
    public static final int COL_PHONE = 5; //used when the user clicks on suggestion

    public FindSuggestionsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_list_suggestions, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MyViewHolder vh = (MyViewHolder) view.getTag();
        vh.tvName.setText(cursor.getString(COL_NAME));
        String address = cursor.getString(COL_ADDRESS)
                + Constants.COMMA
                + Constants.SPACE
                + cursor.getString(COL_LOCALITY)
                + Constants.COMMA
                + Constants.SPACE
                + cursor.getString(COL_PROVINCE);

        vh.tvAddress.setText(address);
//        Pharmacy pharmacy = mList.get(position);
//        holder.tvName.setText(pharmacy.getName());
//        String address = pharmacy.getAddress() + Constants.COMMA
//                + Constants.SPACE
//                + pharmacy.getProvince();
//        holder.tvAddress.setText(address);
    }

//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//        case R.id.plus:
//            MyViewHolder holder= (MyViewHolder) view.getTag();
//
//            holder.optionsRow.toggle();
//
//
//        break;
//        }
//
//    }


    public static class MyViewHolder  {
        @BindView(R.id.nameSuggestions)
        TextView tvName;
        @BindView(R.id.addressSuggestions)
        TextView tvAddress;


        public MyViewHolder(View itemView) {

            ButterKnife.bind(this, itemView);

        }
    }
}
