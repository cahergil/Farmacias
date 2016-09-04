package com.chernandezgil.farmacias.ui.adapter;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.expandable.ExpandableLayoutListener;
import com.chernandezgil.farmacias.expandable.ExpandableLinearLayout;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.google.common.collect.ObjectArrays;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListTabAdapter extends RecyclerView.Adapter<ListTabAdapter.ViewHolder> implements View.OnClickListener{

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Util.LOGD(LOG_TAG,"onCreateViewHolder");
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tab_list,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);

        viewHolder.ivArrow.setOnClickListener(this);
        viewHolder.tvOpen.setOnClickListener(this);
        viewHolder.ivPhone.setOnClickListener(this);
        viewHolder.ivGo.setOnClickListener(this);
        viewHolder.ivShare.setOnClickListener(this);
        viewHolder.ivFavorite.setOnClickListener(this);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Util.LOGD(LOG_TAG,"onBindViewHolder, position"+position);
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
                Util.LOGD(LOG_TAG,"**********************************on Preopen");
                expandState[position]=true;

            }

            @Override
            public void onPreClose() {
                Util.LOGD(LOG_TAG,"**********************************on PreClose");
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

        holder.ivArrow.setTag(holder);
        holder.tvOpen.setTag(holder);
        holder.ivPhone.setTag(holder);
        holder.ivGo.setTag(position);
        holder.ivShare.setTag(position);
        holder.ivFavorite.setTag(position);


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

    public List<Pharmacy>  getPharmaList() {
        return mPharmacyList;
    }
    @Override
    public void onClick(View view) {

        int position;
        ViewHolder vh;
        int id= view.getId();
        switch (id) {
            case R.id.tvOpen:
            case R.id.ivArrow:
                vh= (ViewHolder) view.getTag();
//                ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(vh.ivArrow,"rotation",vh.ivArrow.getRotation()==180?0:180);
//
//                rotateAnim.setDuration(200);
//                rotateAnim.start();
//
//                http://stackoverflow.com/questions/30209415/rotate-an-imagewith-animation
//                float pivotX=(vh.ivArrow.getWidth()/2);
//                float pivotY=(vh.ivArrow.getWidth()/2);
//                vh.ivArrow.setPivotX(pivotX);
//                vh.ivArrow.setPivotY(pivotY);
                vh.ivArrow.animate().rotation(vh.ivArrow.getRotation()==180?0:180);

                vh.viewOptionsRow.toggle();


                break;
            case R.id.ivPhoneb:
                vh= (ViewHolder) view.getTag();
                position=vh.getAdapterPosition();
                Util.startPhoneIntent(mContext,mPharmacyList.get(position).getPhone());
                break;
            case R.id.ivGob:
                position= (int) view.getTag();
                mClickHandler.onClickGo(position);
                break;
            case R.id.ivShareb:
                position=(int) view.getTag();
                Pharmacy pharmacy=mPharmacyList.get(position);
                String name=pharmacy.getName();
                double dist=pharmacy.getDistance();
                String dir=pharmacy.getAddressFormatted();
                String tel=pharmacy.getPhone();
                Util.startShare(mContext,name,dist,dir,tel);
                break;
            case R.id.ivFavoriteb:
                position = (int) view.getTag();
                mClickHandler.onClickFavorite(position);


        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvStreet)
        TextView tvStreet;
        @BindView(R.id.tvDistance)
        TextView tvDistance;
        @BindView(R.id.tvOpen)
        TextView tvOpen;

        @BindView(R.id.optionsRow)
        ExpandableLinearLayout viewOptionsRow;

        @BindView(R.id.ivArrow)
        ImageView ivArrow;
        @BindView(R.id.ivScheduleb)
        ImageView ivClock;
        @BindView(R.id.ivPhoneb)
        ImageView ivPhone;
        @BindView(R.id.ivShareb)
        ImageView ivShare;
        @BindView(R.id.ivFavoriteb)
        ImageView ivFavorite;
        @BindView(R.id.ivGob)
        ImageView ivGo;

        @BindView(R.id.holderContainer)
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);


            viewOptionsRow.collapse();


        }



    }
    public static interface ListTabAdapterOnClickHandler {
        void onClickGo(int position);
        void onClickFavorite(int position);
        void onClick(ViewHolder vh);
    }
}
