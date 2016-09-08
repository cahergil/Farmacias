package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.expandable.ExpandableLayoutListener;
import com.chernandezgil.farmacias.expandable.ExpandableLinearLayout;
import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 05/09/2016.
 */

public class FindRecyclerViewAdapter extends RecyclerView.Adapter<FindRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener{

    private List<Pharmacy> mList;
    private Context mContext;

    public FindRecyclerViewAdapter(Context context) {

        mContext = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_find_list,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        holder.tvPlus.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.optionsRow.setListener(new ExpandableLayoutListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {

                }

                @Override
                public void onPreOpen() {

                }

                @Override
                public void onPreClose() {

                }

                @Override
                public void onOpened() {

                }

                @Override
                public void onClosed() {

                }
            });
            Pharmacy pharmacy = mList.get(position);
            holder.tvName.setText(pharmacy.getName());
            String locality = pharmacy.getLocality() + Constants.COMMA
                + Constants.SPACE
                + pharmacy.getPostal_code()
                + Constants.SPACE
                + pharmacy.getProvince();
            holder.tvLocality.setText(locality);
            holder.tvPlus.setTag(holder);

            holder.tvAdress.setText(pharmacy.getAddress());
            holder.tvTxtPhone.setText(pharmacy.getPhoneFormatted());
    }


    @Override
    public int getItemCount() {
        if(mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<Pharmacy> incommingList) {
        mList = incommingList;
        if(mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.plus:
            MyViewHolder holder= (MyViewHolder) view.getTag();

            holder.optionsRow.toggle();


        break;
        }

    }


    public  class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView tvName;
        @BindView(R.id.locality)
        TextView tvLocality;
        @BindView(R.id.plus)
        TextView tvPlus;
        @BindView(R.id.optionsRow)
        ExpandableLinearLayout optionsRow;

        @BindView(R.id.txtPhone)
        TextView tvTxtPhone;
        @BindView(R.id.address)
        TextView tvAdress;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            optionsRow.collapse();
        }
    }
}
