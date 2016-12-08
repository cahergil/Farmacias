package com.chernandezgil.farmacias.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
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
import com.chernandezgil.farmacias.Utilities.ColorUtils;
import com.chernandezgil.farmacias.Utilities.Constants;
import com.chernandezgil.farmacias.Utilities.TimeMeasure;
import com.chernandezgil.farmacias.Utilities.Utils;
import com.chernandezgil.farmacias.model.ColorMap;
import com.chernandezgil.farmacias.model.Pharmacy;
import com.chernandezgil.farmacias.ui.adapter.item_animator.CustomItemAnimator;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.ItemTouchHelperAdapter;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.ItemTouchHelperViewHolder;
import com.chernandezgil.farmacias.ui.adapter.touch_helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 28/09/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Pharmacy> mList;
    private Context mContext;
    private FavoriteAdapterOnClickHandler mClickHandler;
    private static final String LOG_TAG=FavoriteAdapter.class.getSimpleName();
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
    //swipe dismiss
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private List<Pharmacy> itemsPendingRemoval;
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private HashMap<Pharmacy, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private PreferencesManager mSharedPreferences;
    private boolean mDismissCanceled;
    ColorMap mColorMap;


    public FavoriteAdapter(Context context, FavoriteAdapterOnClickHandler clickHandler,
                          RecyclerView recyclerView, CustomItemAnimator customItemAnimator,
                           OnStartDragListener dragStartListener, PreferencesManager preferencesManager){
        mContext=context;
        mClickHandler=clickHandler;
        mRecyclerView = recyclerView;
        mTm=new TimeMeasure("start FavoriteAdapter");
        mCustomItemAnimator = customItemAnimator;
        mDragStartListener = dragStartListener;
        mSharedPreferences = preferencesManager;
        itemsPendingRemoval = new ArrayList<>();
        initColorMap();


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

    private void initColorMap() {
        mColorMap = new ColorMap();
        HashMap<String,Integer> storedHashMap = mSharedPreferences.getColorMap();
        if( storedHashMap != null) {
            mColorMap.setColorHashMap(storedHashMap);
            return;

        }
        mColorMap.generate();
    }
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Utils.logD("onItemMove","onItemMove");
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


    public void onItemDismiss(int position) {
        Utils.changeFavoriteInDb(mList.get(position).getPhone());
        mList.remove(position);
        notifyItemRemoved(position);
        //in order to show the animation call this also
        notifyItemRangeChanged(position, getItemCount());
        if(mList.isEmpty()) {
            mClickHandler.onListEmpty();
        }
        
    }



    @Override
    public boolean isPendingRemoval(int position) {
        Pharmacy item = mList.get(position);
        return itemsPendingRemoval.contains(item);
    }
    @Override
    public void pendingRemoval(int position) {
        final Pharmacy pharmacy=mList.get(position);
        if(!itemsPendingRemoval.contains(pharmacy)) {
            Utils.logD(LOG_TAG,"pendingRemoval position:"+position);
            itemsPendingRemoval.add(pharmacy);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    onItemDismiss(mList.indexOf(pharmacy));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(pharmacy, pendingRemovalRunnable);
        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  Utils.logD(LOG_TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favorite_list, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    setDelayedTransition();
                }
                mCustomItemAnimator.setAnimateMoves(false);
                if (position == RecyclerView.NO_POSITION) return;
                // collapse any currently expanded items
                if (expandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(expandedPosition, COLLAPSE);
                }
                // expand item
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
    @RequiresApi(Build.VERSION_CODES.KITKAT)
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
  //      holder.ivFavorite.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
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
                view.setTranslationY(Utils.getScreenHeight(mContext));
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
            }
        }
    }
    private void bindHolder(MyViewHolder holder, int position) {
        final Pharmacy pharmacy= mList.get(position);
        if (itemsPendingRemoval.contains(pharmacy)) {
            int color= pharmacy.getCircleColor();
          //  holder.itemView.setBackgroundColor(Utils.getColor(R.color.colorAccent));
            holder.itemView.setBackgroundColor(color);
            Utils.logD(LOG_TAG,"bindViewHolderSwiped,position;"+position);
            //there seems to be a bounce back effect once onSwaped.
            //after calling notifyItemRangeChanged, e.g threre has been previous dismiss
            //this effect shows incorrect views, one has to change visibility of them.
            holder.tvCircle.setVisibility(View.INVISIBLE);
            holder.ivReorder.setVisibility(View.INVISIBLE);
            holder.tvName.setVisibility(View.INVISIBLE);
            holder.tvStreet.setVisibility(View.INVISIBLE);
            holder.tvOpen.setVisibility(View.INVISIBLE);
            holder.tvDistance.setVisibility(View.INVISIBLE);
            holder.ivUndo.setVisibility(View.VISIBLE);
            holder.ivUndo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(pharmacy);
                    pendingRunnables.remove(pharmacy);
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(pharmacy);
                    notifyItemChanged(mList.indexOf(pharmacy));
                    mDismissCanceled =true;

                }
            });

        } else {

            GradientDrawable gradientDrawable;
            Utils.logD(LOG_TAG,"bindViewHolder");
            if(mDismissCanceled) {
                mDismissCanceled =false;
                ObjectAnimator translateToLeft = ObjectAnimator.ofFloat(holder.itemView,
                        "translationX",holder.itemView.getRight(),holder.itemView.getLeft());
                translateToLeft.setDuration(200);
                translateToLeft.start();

            }

            holder.itemView.setBackgroundColor(Utils.getColor(R.color.white));

            holder.ivUndo.setVisibility(View.INVISIBLE);
            holder.ivUndo.setOnClickListener(null);
            holder.tvCircle.setVisibility(View.VISIBLE);
            holder.ivReorder.setVisibility(View.VISIBLE);
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvStreet.setVisibility(View.VISIBLE);
            holder.tvOpen.setVisibility(View.VISIBLE);
            holder.tvDistance.setVisibility(View.VISIBLE);
            String firsChar =pharmacy.getName().substring(0,1).toUpperCase();
            holder.tvCircle.setText(pharmacy.getName().substring(0,1));
            gradientDrawable= (GradientDrawable) ContextCompat.getDrawable(mContext,R.drawable.shape_circle);
            int circleColor=mColorMap.getColorForString(firsChar);
            gradientDrawable.setColor(circleColor);
            holder.tvCircle.setBackground(gradientDrawable);
            holder.tvCircle.setTag(circleColor);
            pharmacy.setCircleColor(circleColor);
            holder.tvName.setText(pharmacy.getName());
            holder.tvStreet.setText(pharmacy.getAddressFormatted());
            holder.tvDistance.setText(mContext.getString(R.string.format_distance, pharmacy.getDistance()));
            boolean isOpen = pharmacy.isOpen();
            holder.tvOpen.setText(isOpen ? "Abierta" : "Cerrada");
            int favDraResid;
            if (pharmacy.isFavorite()) {
                favDraResid = R.drawable.ic_heart;
            } else {
                favDraResid = R.drawable.ic_heart_outline;
            }
//            holder.ivFavorite.setImageResource(favDraResid);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                final boolean isExpanded = position == expandedPosition;
                setExpanded(holder, isExpanded);
            } else {
                setExpanded(holder, true);
            }
            holder.ivReorder.setVisibility(View.VISIBLE);
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

    }

    @Override
    public int getItemCount() {

        if (mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<Pharmacy> pharmacyList) {
        mList = reorderListIfNecessary(pharmacyList);
        notifyDataSetChanged();

    }

    private List<Pharmacy> reorderListIfNecessary(List<Pharmacy> pharmacyList) {
        List<Pharmacy> storedFavorites= mSharedPreferences.getFavorites();
        //delete from original list those elements removed in other screens
        for (int i = 0;i <storedFavorites.size();i++) {
            if(!pharmacyList.contains(storedFavorites.get(i))) {
                storedFavorites.remove(i);
            }
        }
        //add to original list those elements added in other screens
        //the new elements are added according the sortorder of the query in the content provider,
        //currently name of pharmacy order.
        for (int i = 0; i<pharmacyList.size();i++) {
            Pharmacy element = pharmacyList.get(i);
            if(!storedFavorites.contains(element)) {
                storedFavorites.add(element);
            }
        }
        return storedFavorites;
    }

    public List<Pharmacy> getmList(){
        return mList;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,
            ItemTouchHelperViewHolder {

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
        @BindView(R.id.ivGo)
        public ImageView ivGo;
        @BindView(R.id.optionsRow)
        public LinearLayout llOptionsRow;
        @BindView(R.id.ivReorder)
        public ImageView ivReorder;
        @BindView(R.id.ivUndo)
        public ImageView ivUndo;
        @BindView(R.id.ivCircle)
        public TextView tvCircle;


        public MyViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            ivPhone.setOnClickListener(this);
            ivShare.setOnClickListener(this);
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
                case R.id.ivSchedule:
                    mClickHandler.onClickClock(mList.get(position).getHorario());
            }
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ColorUtils.modifyAlpha(ContextCompat.getColor(mContext, R.color.black),0.10f));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    public static interface FavoriteAdapterOnClickHandler {
        void onClickGo(Pharmacy pharmacy);
        void onClickPhone(String phone);
        void onClickShare(Pharmacy pharmacy);
        void onClickClock(String hour);
        void onListEmpty();
    }
}