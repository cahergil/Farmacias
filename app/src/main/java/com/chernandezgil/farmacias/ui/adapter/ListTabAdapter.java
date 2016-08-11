package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bettervectordrawable.utils.BitmapUtil;
import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 08/08/2016.
 */
public class ListTabAdapter extends RecyclerView.Adapter<ListTabAdapter.ViewHolder> implements View.OnClickListener{

    private List<Pharmacy> mPharmacyList;
    private Context mContext;
    private View mEmptyView;
    private ListTabAdapterOnClickHandler mClickHandler;
    private View mLastClicked;
    private static final String LOG_TAG=ListTabAdapter.class.getSimpleName();

    public ListTabAdapter(Context context,View emptyView,ListTabAdapterOnClickHandler clickHandler){
        mContext=context;
        mEmptyView=emptyView;
        mClickHandler=clickHandler;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tab_list,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.ivArrow.setOnClickListener(this);
        viewHolder.tvOpen.setOnClickListener(this);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pharmacy pharmacy=mPharmacyList.get(position);
        holder.tvName.setText(pharmacy.getName());
        holder.tvStreet.setText(pharmacy.getAddressFormatted());
        holder.tvDistance.setText(mContext.getString(R.string.format_distance,pharmacy.getDistance()/1000));
        boolean isOpen=pharmacy.isOpen();
        holder.tvOpen.setText(isOpen? "Open":"Close");
        int color;
        if(isOpen) {
            color=getColor(R.color.pharmacy_open);
        } else {
            color=getColor(R.color.pharmacy_close);
        }

        holder.tvOpen.setTextColor(color);
        setBitmapFromVectorDrawable(holder.ivClock,R.drawable.clock,color);
        setBitmapFromVectorDrawable(holder.ivFavorite,R.drawable.heart,color);
        setBitmapFromVectorDrawable(holder.ivGo,R.drawable.directions,color);
        setBitmapFromVectorDrawable(holder.ivShare,R.drawable.share,color);
        setBitmapFromVectorDrawable(holder.ivPhone,R.drawable.phone,color);
        if(pharmacy.isOptionsRow()) {
            holder.viewOptionsRow.setVisibility(View.VISIBLE);
        } else {
            holder.viewOptionsRow.setVisibility(View.GONE);
        }
        if(pharmacy.isArrow_down()) {
            setBitmapFromVectorDrawable(holder.ivArrow,R.drawable.arrow_down, Color.BLACK);
        } else {
            setBitmapFromVectorDrawable(holder.ivArrow,R.drawable.arrow_up,Color.BLACK);
        }
      //  AnimatedVectorDrawableCompat drawableCompat = AnimatedVectorDrawableCompat.create(mContext, R.drawable.arrow_avd);
      //  holder.ivArrow.setImageDrawable(drawableCompat);

        holder.ivArrow.setTag(holder);
        holder.tvOpen.setTag(holder);
    }
    public void setBitmapFromVectorDrawable(ImageView imageView, @DrawableRes int drawableResId, int color) {

        //create vector drawable and tint it
        VectorDrawableCompat drawable=VectorDrawableCompat.create(mContext.getResources(),drawableResId,null);
        if(drawable==null) return;
        drawable.setTint(color);
        //convert tinted vector drawable to bitmap
        Bitmap bitmap= createScaledBitMapFromVectorDrawable(drawable,40f);
        imageView.setImageBitmap(bitmap);



    }

    private Bitmap createScaledBitMapFromVectorDrawable(VectorDrawableCompat vd,float dimension) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return BitmapUtil.toBitmap(vd, metrics, dimension, 0);


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
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        int position;
        int id= view.getId();
        switch (id) {
            case R.id.tvOpen:
            case R.id.ivArrow:
                ViewHolder vh= (ViewHolder) view.getTag();
                position=vh.getAdapterPosition();
                for(int i = 0;i<mPharmacyList.size();i++) {
                    if(i==position) {
                        Pharmacy pharmacy=mPharmacyList.get(position);
                        mPharmacyList.get(position).setOptionsRow(!pharmacy.isOptionsRow());
                        mPharmacyList.get(position).setArrow_down(!pharmacy.isArrow_down());
                        continue;
                    }
                    mPharmacyList.get(i).setOptionsRow(false);
                    mPharmacyList.get(i).setArrow_down(true);
                }

                  //  Animation animation= AnimationUtils.loadAnimation(mContext,R.anim.rotate_animation);
                    RotateAnimation rotate = new RotateAnimation(0, 180,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);

                    rotate.setDuration(100);
                   // rotate.setRepeatCount(1);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Util.LOGD(LOG_TAG,"onAnimationStart");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Util.LOGD(LOG_TAG,"onAnimationEnd");
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            Util.LOGD(LOG_TAG,"onAnimationRepeat");
                        }
                    });
                    vh.ivArrow.startAnimation(rotate);




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
        View viewOptionsRow;

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


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }


//        @Override
//        public void onClick(View view) {
//            int position;
//            int id= view.getId();
//            switch (id) {
//                case R.id.tvOpen:
//                case R.id.ivArrow:
//
//                    position=getAdapterPosition();
//                    for(int i = 0;i<mPharmacyList.size();i++) {
//                        if(i==position) {
//                            mPharmacyList.get(position).setOptionsRow(!mPharmacyList.get(position).isOptionsRow());
//                            continue;
//                        }
//                        mPharmacyList.get(i).setOptionsRow(false);
//                    }
//                    notifyDataSetChanged();
//                    mClickHandler.onClickToogle(this,position);
//        //            AnimatedVectorDrawableCompat drawableCompat = AnimatedVectorDrawableCompat.create(mContext, R.drawable.arrow_avd);
//        //            ivArrow.setImageDrawable(drawableCompat);
//                    ViewHolder holder= (ViewHolder) view.getTag();
//                    AnimatedVectorDrawableCompat drawableCompat= (AnimatedVectorDrawableCompat) holder.ivArrow.getDrawable();
//                    if (drawableCompat instanceof Animatable) {
//                        ((Animatable) drawableCompat).start();
//                    }
//
//                  //  Animation animation=AnimationUtils.loadAnimation(mContext,R.anim.rotate_animation);
////                    RotateAnimation rotate = new RotateAnimation(0, 180,
////                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
////                            0.5f);
////
////                    rotate.setDuration(4000);
////                    rotate.setRepeatCount(1);
////                    rotate.setAnimationListener(new Animation.AnimationListener() {
////                        @Override
////                        public void onAnimationStart(Animation animation) {
////                            Util.LOGD(LOG_TAG,"onAnimationStart");
////                        }
////
////                        @Override
////                        public void onAnimationEnd(Animation animation) {
////                            Util.LOGD(LOG_TAG,"onAnimationEnd");
////                        }
////
////                        @Override
////                        public void onAnimationRepeat(Animation animation) {
////                            Util.LOGD(LOG_TAG,"onAnimationRepeat");
////                        }
////                    });
////                    ivArrow.startAnimation(rotate);
//
//                    break;
//
//                case R.id.ivGob:
//                    position=getAdapterPosition();
//                    mClickHandler.onClickGo(this,position);
//                    int color=ContextCompat.getColor(view.getContext(),R.color.blue_mobile_web);
//                    setBitmapFromVectorDrawable(ivClock,R.drawable.clock,color);
//                    break;
//                case R.id.ivFavoriteb:break;
//                case R.id.ivShareb:break;
//                case R.id.ivPhoneb:break;
//                case R.id.ivScheduleb:break;
//
//            }
//        }
//
//
//    }
    }
    public static interface ListTabAdapterOnClickHandler {
        void onClickGo(ViewHolder vh,int position);
        void onClickToogle(ViewHolder vh,int position);
        void onClick(ViewHolder vh);
    }
}
