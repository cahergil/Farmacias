package com.chernandezgil.farmacias.ui.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
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
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.ItemTouchHelperAdapter;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.ItemTouchHelperViewHolder;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.OnStartDragListener;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 28/09/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> implements
        ItemTouchHelperAdapter{

    private List<Pharmacy> mList;
    private Context mContext;
    private FavoriteAdapterOnClickHandler mClickHandler;
    private static final String LOG_TAG=ListTabAdapter.class.getSimpleName();
    private CustomItemAnimator mCustomItemAnimator;
    private int expandedPosition = RecyclerView.NO_POSITION;
    private Transition expandCollapse = null;
    private TimeMeasure mTm;
    private static int COLLAPSE = 2;
    private static int EXPAND = 3;
    private RecyclerView mRecyclerView;
    private int lastAnimatedPosition = -1;
    private static final int ANIMATED_ITEMS_COUNT = 50;
    private final OnStartDragListener mDragStartListener;
    @Constants.ScrollDirection
    int scrollDirection;



    public FavoriteAdapter(Context context, FavoriteAdapterOnClickHandler clickHandler,
                          RecyclerView recyclerView, CustomItemAnimator customItemAnimator,
                           OnStartDragListener dragStartListener
    ){
        mContext=context;
        mClickHandler=clickHandler;
        mRecyclerView = recyclerView;
        mTm=new TimeMeasure("start FavoriteAdapter");
        mCustomItemAnimator = customItemAnimator;
        mDragStartListener = dragStartListener;

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
                    //    Log.i("RecyclerView scrolled: ", "currentVisible,firstVisible"+currentFirstVisible +","+ firstVisibleInRecyclerViw);
                } else {
                    scrollDirection = Constants.SCROLL_DOWN;
                    Log.i("RecyclerView scrolled: ", "scroll down!");
                    Log.i("RecyclerView scrolled: ", "dy:" + dy);
                    //   Log.i("RecyclerView scrolled: ", "currentVisible,firstVisible"+currentFirstVisible +","+ firstVisibleInRecyclerViw);
                }
                //     firstVisibleInRecyclerViw = currentFirstVisible;

            }
        });

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Util.logD("onItemMove","onItemMove");
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }






    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  Util.logD(LOG_TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favorite_list, parent, false);

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
    private void setExpanded(FavoriteAdapter.MyViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        holder.llOptionsRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivGo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivPhone.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivClock.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivShare.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivFavorite.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onBindViewHolder(FavoriteAdapter.MyViewHolder holder, int position, List<Object> payloads) {

        if ((payloads.contains(EXPAND) || payloads.contains(COLLAPSE))) {
            setExpanded(holder, position == expandedPosition);
        } else {
            onBindViewHolder(holder, position);
        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //  Util.logD(LOG_TAG,"onBindViewHolder, position"+position);

        runEnterAnimation(holder.itemView, position);
        bindHolder(holder, position);

    }
    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }


        if (scrollDirection == Constants.SCROLL_UP) {
            Log.d(LOG_TAG, "runEnterAnimation_up");
            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                Log.d(LOG_TAG, "lasAnimated,position" + lastAnimatedPosition + "," + position);
                view.setTranslationY(Util.getScreenHeight(mContext));
                view.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(500)
                        .start();

            }
        } else {
            Log.d(LOG_TAG, "runEnterAnimation_down");
            if (position < lastAnimatedPosition) {
                Log.d(LOG_TAG, "lasAnimated,position" + lastAnimatedPosition + "," + position);
                lastAnimatedPosition = position;
//                view.setTranslationY(-Util.getScreenHeight(mContext));
//                view.animate()
//                        .translationY(0)
//                        .setInterpolator(new DecelerateInterpolator(3.f))
//                        .setDuration(500)
//                        .start();
            }
        }
    }
    private void bindHolder(MyViewHolder holder, int position) {
        Pharmacy pharmacy= mList.get(position);

        holder.tvName.setText(pharmacy.getName());
        holder.tvStreet.setText(pharmacy.getAddressFormatted());
        holder.tvDistance.setText(mContext.getString(R.string.format_distance,pharmacy.getDistance()));
        boolean isOpen=pharmacy.isOpen();
        holder.tvOpen.setText(isOpen? "Abierta":"Cerrada");
        int favDraResid;
        if(pharmacy.isFavorite()) {
            favDraResid=R.drawable.ic_heart;
        } else {
            favDraResid=R.drawable.ic_heart_outline;
        }
        holder.ivFavorite.setImageResource(favDraResid);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            final boolean isExpanded = position == expandedPosition;
            setExpanded(holder, isExpanded);
        } else {
            setExpanded(holder, true);
        }

        holder.ivReorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);


                }

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {

        if (mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<Pharmacy> pharmacyList) {
        mList =pharmacyList;
        notifyDataSetChanged();

    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,
            ItemTouchHelperViewHolder{

        @BindView(R.id.tvName)
        public TextView tvName;
        @BindView(R.id.tvStreet)
        public TextView tvStreet;
        @BindView(R.id.tvDistance)
        public TextView tvDistance;
        @BindView(R.id.tvOpen)
        public TextView tvOpen;
        @BindView(R.id.ivSchedule)
        public ImageView ivClock;
        @BindView(R.id.ivPhone)
        public ImageView ivPhone;
        @BindView(R.id.ivShare)
        public ImageView ivShare;
        @BindView(R.id.ivFavorite)
        public ImageView ivFavorite;
        @BindView(R.id.ivGo)
        public ImageView ivGo;
        //        @BindView(R.id.holderContainer)
//        public CardView cardView;
        @BindView(R.id.optionsRow)
        public LinearLayout llOptionsRow;

        @BindView(R.id.ivReorder)
        ImageView ivReorder;


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
            switch (id) {

                case R.id.ivPhone:
                    mClickHandler.onClickPhone(mList.get(position).getPhone());
                    break;
                case R.id.ivGo:
                    mClickHandler.onClickGo(mList.get(position));
                    break;
                case R.id.ivShare:
                    mClickHandler.onClickShare(mList.get(position));
                    break;
                case R.id.ivFavorite:
                    mClickHandler.onClickFavorite(mList.get(position));
                    break;

            }
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Util.modifyAlpha(ContextCompat.getColor(mContext, R.color.black),0.10f));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    public static interface FavoriteAdapterOnClickHandler {
        void onClickGo(Pharmacy pharmacy);
        void onClickFavorite(Pharmacy pharmacy);
        void onClickPhone(String phone);
        void onClickShare(Pharmacy pharmacy);
    }
}