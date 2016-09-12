package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_find_list1,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        holder.tvPlus.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

            Pharmacy pharmacy = mList.get(position);
            holder.tvName.setText(pharmacy.getName());
            String locality = mContext.getString(R.string.fca_format_localidad)+pharmacy.getLocality() + Constants.COMMA
                + Constants.SPACE
                + pharmacy.getPostal_code()
                + Constants.SPACE
                + pharmacy.getProvince();
            holder.tvLocality.setText(locality);
            holder.tvPlus.setTag(holder);

            holder.tvAdress.setText(mContext.getString(R.string.fca_format_direccion,pharmacy.getAddress()));
            holder.tvDistance.setText(mContext.getString(R.string.format_distancia,pharmacy.getDistance()));
            holder.tvTxtPhone.setText(mContext.getString(R.string.fca_format_telefono,pharmacy.getAddress()));
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


        break;
        }

    }


    public  class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView tvName;
        @BindView(R.id.locality)
        TextView tvLocality;
        @BindView(R.id.address)
        TextView tvAdress;
        @BindView(R.id.distance)
        TextView tvDistance;
        @BindView(R.id.plus)
        TextView tvPlus;
        @BindView(R.id.txtPhone)
        TextView tvTxtPhone;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}
