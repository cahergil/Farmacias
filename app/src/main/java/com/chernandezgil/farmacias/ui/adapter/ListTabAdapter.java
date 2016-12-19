package com.chernandezgil.farmacias.ui.adapter;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.ui.adapter.item_animator.CustomItemAnimator;
import com.chernandezgil.farmacias.ui.fragment.ListTabFragment;


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
    private CustomItemAnimator mCustomItemAnimator;
    private int expandedPosition = RecyclerView.NO_POSITION;
    private Transition expandCollapse = null;
    private static int COLLAPSE = 2;
    private static int EXPAND = 3;
    private RecyclerView mRecyclerView;
    private int lastAnimatedPosition = -1;



    @Constants.ScrollDirection
    int scrollDirection;


    public ListTabAdapter(ListTabFragment fragment,
                          RecyclerView recyclerView, CustomItemAnimator customItemAnimator
                        ){
        mContext=fragment.getActivity();
        mClickHandler=fragment;
        mRecyclerView = recyclerView;
        mCustomItemAnimator = customItemAnimator;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            expandCollapse = new AutoTransition();
            expandCollapse.setDuration(200);
            expandCollapse.setInterpolator(AnimationUtils.loadInterpolator(mContext,
                    android.R.interpolator.linear));
            expandCollapse.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    mCustomItemAnimator.setAnimateMoves(true);
                    mRecyclerView.setOnTouchListener(null);

                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
        scrollDirection = Constants.SCROLL_UP;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    scrollDirection = Constants.SCROLL_UP;
                    Log.i("RecyclerView scrolled: ", "scroll up!");
                    Log.i("RecyclerView scrolled: ", "dy:" + dy);
                } else {
                    scrollDirection = Constants.SCROLL_DOWN;
                    Log.i("RecyclerView scrolled: ", "scroll down!");
                    Log.i("RecyclerView scrolled: ", "dy:" + dy);
                }
            }
        });

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  Utils.logD(LOG_TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tab_list, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                setDelayedTransition();
                mCustomItemAnimator.setAnimateMoves(false);
                if (position == RecyclerView.NO_POSITION) return;
                // collapse any currently expanded items
                if (expandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(expandedPosition, COLLAPSE);
                }
                if (expandedPosition != position) {
                    expandedPosition = position;
                    notifyItemChanged(position, EXPAND);
                } else {
                    expandedPosition = RecyclerView.NO_POSITION;
                }


            }
        });
        return holder;

    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setDelayedTransition() {
        TransitionManager.beginDelayedTransition(mRecyclerView, expandCollapse);
    }
    private void setExpanded(ListTabAdapter.MyViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        holder.llOptionsRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivGo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivPhone.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivClock.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivShare.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivFavorite.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onBindViewHolder(ListTabAdapter.MyViewHolder holder, int position, List<Object> payloads) {

        if ((payloads.contains(EXPAND) || payloads.contains(COLLAPSE))) {
            setExpanded(holder, position == expandedPosition);
        } else {
            onBindViewHolder(holder, position);
        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Utils.logD(LOG_TAG,"onBindViewHolder, position"+position);

        runEnterAnimation(holder.itemView, position);
        bindHolder(holder, position);

    }
    private void runEnterAnimation(View view, int position) {
        if (position >= mPharmacyList.size() - 1) {
            return;
        }

        if (scrollDirection == Constants.SCROLL_UP) {
            Utils.logD(LOG_TAG, "runEnterAnimation_up");
            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                Utils.logD(LOG_TAG, "lasAnimated,position:" + lastAnimatedPosition + "," + position);
                view.setTranslationY(Utils.getScreenHeight(mContext));
                ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(view,"translationY",0);
                //more than this causes the animation to stutter
                objectAnimator.setDuration(300);
                objectAnimator.setInterpolator(new DecelerateInterpolator(3.f));
                objectAnimator.start();
            }
        } else {
            Utils.logD(LOG_TAG, "runEnterAnimation_down");
            if (position < lastAnimatedPosition) {
                Utils.logD(LOG_TAG, "lasAnimated,position" + lastAnimatedPosition + "," + position);
                lastAnimatedPosition = position;
            }
        }
    }
    private void bindHolder(MyViewHolder holder, int position) {
        Pharmacy pharmacy=mPharmacyList.get(position);

        holder.tvName.setText(pharmacy.getName());
        holder.tvStreet.setText(pharmacy.getAddressFormatted());
        holder.tvDistance.setText(mContext.getString(R.string.format_distance,pharmacy.getDistance()/1000));
        boolean isOpen=pharmacy.isOpen();
        holder.tvOpen.setText(isOpen? mContext.getString(R.string.rtl_farmacia_abierta)
                :mContext.getString(R.string.rtl_farmacia_cerrada));
        int color;
        if(isOpen) {
            color=getColor(R.color.pharmacy_open);
        } else {
            color=getColor(R.color.pharmacy_close);
        }
        holder.tvOpen.setTextColor(color);


        int favDraResid;
        if(pharmacy.isFavorite()) {
            favDraResid=R.drawable.ic_heart;
        } else {
            favDraResid=R.drawable.ic_heart_outline;
        }
        setFavoriteIcon(holder.ivFavorite,favDraResid,color);
        setTintedVectorDrawable(holder.ivClock,color);
        setTintedVectorDrawable(holder.ivGo,color);
        setTintedVectorDrawable(holder.ivShare,color);
        setTintedVectorDrawable(holder.ivPhone,color);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            final boolean isExpanded = position == expandedPosition;
            setExpanded(holder, isExpanded);
        } else {
            setExpanded(holder, true);
        }

        holder.tvOrder.setBackgroundResource(isOpen ? R.drawable.shape_circle_order_open:
            R.drawable.shape_circle_order_close);
        holder.tvOrder.setText(pharmacy.getOrder());

    }


    private void setFavoriteIcon(ImageView imageView,int favDraResid,int color){

        imageView.setImageResource(favDraResid);
        setTintedVectorDrawable(imageView,color);
    }

    private void setTintedVectorDrawable(ImageView imageView, int color) {
      //option1. create vector drawable and tint it(this method resize the image
      //  VectorDrawableCompat drawable=VectorDrawableCompat.create(mContext.getResources(),drawableResId,null);
      //  if(drawable==null) return;
      //  drawable.setTint(color);
      //  convert tinted vector drawable to bitmap
      //  Bitmap bitmap= Utils.createScaledBitMapFromVectorDrawable(mContext,drawable,40f);
      //  imageView.setImageBitmap(bitmap);

      // option2: much better, reduce stuttering when scrolling

        DrawableCompat.setTint(imageView.getDrawable(),color);



    }



    @Override
    public int getItemCount() {

        if (mPharmacyList == null) return 0;
        return mPharmacyList.size();
    }

    public void swapData(List<Pharmacy> pharmacyList) {
        mPharmacyList=pharmacyList;
        notifyDataSetChanged();

    }

    private int getColor(@ColorRes int resId){
        int color=ContextCompat.getColor(mContext,resId);
        return color;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        @BindView(R.id.tvOrder)
        public  TextView tvOrder;
        @BindView(R.id.tvName)
        public TextView tvName;
        @BindView(R.id.tvStreet)
        public TextView tvStreet;
        @BindView(R.id.tvDistance)
        public TextView tvDistance;
        @BindView(R.id.tvOpen)
        public TextView tvOpen;
        @BindView(R.id.ivOpeningHours)
        public ImageView ivClock;
        @BindView(R.id.ivPhone)
        public ImageView ivPhone;
        @BindView(R.id.ivShare)
        public ImageView ivShare;
        @BindView(R.id.ivFavorite)
        public ImageView ivFavorite;
        @BindView(R.id.ivGo)
        public ImageView ivGo;
        @BindView(R.id.optionsRow)
        public LinearLayout llOptionsRow;


        public MyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            ivPhone.setOnClickListener(this);
            ivShare.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
            ivGo.setOnClickListener(this);
            ivClock.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int id= view.getId();
            int position = getAdapterPosition();
            Pharmacy pharmacy=mPharmacyList.get(position);
            switch (id) {

                case R.id.ivPhone:
                    mClickHandler.onClickPhone(pharmacy.getPhone());
                    break;
                case R.id.ivGo:
                    mClickHandler.onClickGo(pharmacy);
                    break;
                case R.id.ivShare:
                    mClickHandler.onClickShare(pharmacy);
                    break;
                case R.id.ivFavorite:
                    mClickHandler.onClickFavorite(pharmacy);
                    break;
                case R.id.ivOpeningHours:
                    mClickHandler.onClickOpeningHours(pharmacy);
                    break;

            }
        }



    }

    public interface ListTabAdapterOnClickHandler {
        void onClickGo(Pharmacy pharmacy);
        void onClickFavorite(Pharmacy pharmacy);
        void onClickPhone(String phone);
        void onClickShare(Pharmacy pharmacy);
        void onClickOpeningHours(Pharmacy pharmacy);
    }
}
