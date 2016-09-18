package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.expandable.ExpandableLayoutListener;
import com.chernandezgil.farmacias.expandable.ExpandableLinearLayout;
import com.chernandezgil.farmacias.model.Pharmacy;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListTabAdapter extends RecyclerView.Adapter<ListTabAdapter.MyViewHolder> {

    private List<Pharmacy> mPharmacyList;
    private Context mContext;
    private ListTabAdapterOnClickHandler mClickHandler;
    private static final String LOG_TAG=ListTabAdapter.class.getSimpleName();
    private boolean mRotation;
    private TimeMeasure mTm;
    private boolean[] mRotationArray;
    private boolean[] expandState;
    //to handle Loader loading again
    private boolean[] oldexpandState;

    public ListTabAdapter(Context context,ListTabAdapterOnClickHandler clickHandler){
        mContext=context;
        mClickHandler=clickHandler;
        mTm=new TimeMeasure("start ListTabAdapter");

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      //  Util.logD(LOG_TAG,"onCreateViewHolder");
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tab_list,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
      //  Util.logD(LOG_TAG,"onBindViewHolder, position"+position);
        Pharmacy pharmacy=mPharmacyList.get(position);
        if(expandState[position]) {
            if(mRotationArray[position]) {
                holder.viewOptionsRow.expand();
                mRotationArray[position] = false;
                holder.ivArrow.setRotation(180);

            }
        }
        holder.viewOptionsRow.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onPreOpen() {
                Util.logD(LOG_TAG,"**********************************on Preopen");
                expandState[position]=true;

            }

            @Override
            public void onPreClose() {
                Util.logD(LOG_TAG,"**********************************on PreClose");
                expandState[position]= false;
            }

            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {

            }
        });




        holder.tvName.setText(pharmacy.getName());
        holder.tvStreet.setText(pharmacy.getAddressFormatted());
        holder.tvDistance.setText(mContext.getString(R.string.format_distance,pharmacy.getDistance()/1000));
        boolean isOpen=pharmacy.isOpen();
        holder.tvOpen.setText(isOpen? "Abierta":"Cerrada");
        int color;
        GradientDrawable gradientDrawable;
        if(isOpen) {
            color=getColor(R.color.pharmacy_open_list);
            holder.tvOpen.setTextColor(ContextCompat.getColor(mContext,R.color.green_800));
            gradientDrawable= (GradientDrawable) ContextCompat.getDrawable(mContext,R.drawable.distance_box_open);
        } else {
            color=getColor(R.color.pharmacy_close);
            holder.tvOpen.setTextColor(color);
            gradientDrawable= (GradientDrawable) ContextCompat.getDrawable(mContext,R.drawable.distance_box_close);
        }
        holder.tvDistance.setBackground(gradientDrawable);

        int favDraResid;
        if(pharmacy.isFavorite()) {
            favDraResid=R.drawable.heart;
        } else {
            favDraResid=R.drawable.heart_outline;
        }
        setBitmapFromVectorDrawable(holder.ivClock,R.drawable.clock,color);
        setBitmapFromVectorDrawable(holder.ivFavorite,favDraResid,color);
        setBitmapFromVectorDrawable(holder.ivGo,R.drawable.directions,color);
        setBitmapFromVectorDrawable(holder.ivShare,R.drawable.share,color);
        setBitmapFromVectorDrawable(holder.ivPhone,R.drawable.phone,color);


      //  AnimatedVectorDrawableCompat drawableCompat = AnimatedVectorDrawableCompat.create(mContext, R.drawable.arrow_avd);
      //  holder.ivArrow.setImageDrawable(drawableCompat);



    }
    public void setBitmapFromVectorDrawable(ImageView imageView, @DrawableRes int drawableResId, int color) {

        //create vector drawable and tint it
        VectorDrawableCompat drawable=VectorDrawableCompat.create(mContext.getResources(),drawableResId,null);
        if(drawable==null) return;
        drawable.setTint(color);
        //convert tinted vector drawable to bitmap
        Bitmap bitmap= Util.createScaledBitMapFromVectorDrawable(mContext,drawable,40f);
        imageView.setImageBitmap(bitmap);



    }
    public void setExpandStateArray(boolean[] stateList,boolean rotation){
        oldexpandState=stateList;
        mRotation=rotation;
        if(mRotation) {
          copyStateToRotationArray(stateList);
          mRotation=false;
        }

    }
    private void copyStateToRotationArray(boolean[] stateList){
        mRotationArray = new boolean[stateList.length];
        for (int i = 0;i<stateList.length;i++) {
            mRotationArray[i] = stateList[i];
        }
    }
    public boolean[] getExpandStateArray(){
        return oldexpandState;
    }
    private int getColor(@ColorRes int resId){
        int color=ContextCompat.getColor(mContext,resId);
        return color;
    }
    @Override
    public int getItemCount() {

        if (mPharmacyList == null) return 0;
        return mPharmacyList.size();
    }

    public void swapData(List<Pharmacy> pharmacyList) {
        mPharmacyList=pharmacyList;
        if(oldexpandState == null ) {
            expandState=new boolean[pharmacyList.size()];
            for (int i = 0; i < mPharmacyList.size(); i++) {
                expandState[i]=false;

            }

            oldexpandState = expandState;
        } else {
          expandState = oldexpandState;

        }


        notifyDataSetChanged();

    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        @BindView(R.id.tvName)
        public TextView tvName;
        @BindView(R.id.tvStreet)
        public TextView tvStreet;
        @BindView(R.id.tvDistance)
        public TextView tvDistance;
        @BindView(R.id.tvOpen)
        public TextView tvOpen;
        @BindView(R.id.optionsRow)
        public ExpandableLinearLayout viewOptionsRow;
        @BindView(R.id.ivArrow)
        public ImageView ivArrow;
        @BindView(R.id.ivScheduleb)
        public ImageView ivClock;
        @BindView(R.id.ivPhoneb)
        public ImageView ivPhone;
        @BindView(R.id.ivShareb)
        public ImageView ivShare;
        @BindView(R.id.ivFavoriteb)
        public ImageView ivFavorite;
        @BindView(R.id.ivGob)
        public ImageView ivGo;

        @BindView(R.id.holderContainer)
        public CardView cardView;

        public MyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            viewOptionsRow.setOnClickListener(this);
            ivArrow.setOnClickListener(this);
            ivPhone.setOnClickListener(this);
            ivShare.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
            ivGo.setOnClickListener(this);
            viewOptionsRow.collapse();
            int position = getAdapterPosition();

        }


        @Override
        public void onClick(View view) {
            int id= view.getId();
            int position = getAdapterPosition();
            switch (id) {
                case R.id.tvOpen:
                case R.id.ivArrow:

                    mClickHandler.onClickOptions(this);


                    break;
                case R.id.ivPhoneb:
                    mClickHandler.onClickPhone(mPharmacyList.get(position).getPhone());
                    break;
                case R.id.ivGob:
                    mClickHandler.onClickGo(mPharmacyList.get(position));
                    break;
                case R.id.ivShareb:
                    mClickHandler.onClickShare(mPharmacyList.get(position));
                    break;
                case R.id.ivFavoriteb:
                    mClickHandler.onClickFavorite(mPharmacyList.get(position));
                break;

            }
        }
    }

    public static interface ListTabAdapterOnClickHandler {
        void onClickGo(Pharmacy pharmacy);
        void onClickFavorite(Pharmacy pharmacy);
        void onClickOptions(MyViewHolder viewHolder);
        void onClickPhone(String phone);
        void onClickShare(Pharmacy pharmacy);
    }
}
