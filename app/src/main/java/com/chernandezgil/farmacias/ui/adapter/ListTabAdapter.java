package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListTabAdapter extends RecyclerView.Adapter<ListTabAdapter.ViewHolder> {

    private List<Pharmacy> mPharmacyList;
    private Context mContext;
    private View mEmptyView;
    private ListTabAdapterOnClickHandler mClickHandler;
    public ListTabAdapter(Context context,View emptyView,ListTabAdapterOnClickHandler clickHandler){
        mContext=context;
        mEmptyView=emptyView;
        mClickHandler=clickHandler;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tab_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(mPharmacyList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mPharmacyList == null) return 0;
        return mPharmacyList.size();
    }

    public void swapData(List<Pharmacy> pharmacyList) {
        mPharmacyList=pharmacyList;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvStreet)
        TextView tvStreet;
        @BindView(R.id.tvDistance)
        TextView tvDistance;
        @BindView(R.id.tvOpen)
        TextView tvOpen;

        @BindView(R.id.ivClock)
        ImageView ivClock;
        @BindView(R.id.ivPhone)
        ImageView ivPhone;
        @BindView(R.id.ivShare)
        ImageView ivShare;
        @BindView(R.id.ivFavorite)
        ImageView ivFavorite;
        @BindView(R.id.ivGo)
        ImageView ivGo;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
            v.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            int id= view.getId();
            switch (id) {

                case R.id.ivGo:
                    int position=getAdapterPosition();
                    break;
                case R.id.ivFavorite:break;
                case R.id.ivShare:break;
                case R.id.ivPhone:break;
                case R.id.ivClock:break;

            }
        }
    }

    public static interface ListTabAdapterOnClickHandler {
        void onClickGo(ViewHolder vh,int position);
        void onClick(ViewHolder vh);
    }
}
