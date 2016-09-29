package com.chernandezgil.farmacias.ui.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.chernandezgil.farmacias.Utilities.Util;
import com.chernandezgil.farmacias.model.Pharmacy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 05/09/2016.
 */

public class FindRecyclerViewAdapter extends RecyclerView.Adapter<FindRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener {
    private static final String LOG_TAG = FindRecyclerViewAdapter.class.getSimpleName();
    private List<Pharmacy> mList;
    private Context mContext;
    private int expandedPosition = RecyclerView.NO_POSITION;
    private static int COLLAPSE = 2;
    private static int EXPAND = 3;
    private RecyclerView mRecyclerView;
    private Transition expandCollapse = null;
    private int lastAnimatedPosition = -1;
    private static final int ANIMATED_ITEMS_COUNT = 50;
    private int mColorSpan;
    private int mColorSpanData;
    private CustomItemAnimator mCustomItemAnimator;
    private OnClickHandler mCallback;
    private float offset;
    private static int firstVisibleInRecyclerViw;



    @Constants.ScrollDirection
    int scrollDirection;

    public FindRecyclerViewAdapter(Context context, RecyclerView recyclerview, CustomItemAnimator customItemAnimator,
                                   OnClickHandler callback) {
        mRecyclerView = recyclerview;
        mContext = context;
        mCustomItemAnimator = customItemAnimator;
        mCallback = callback;
        offset = mContext.getResources().getDimensionPixelSize(R.dimen.offset_y);

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
        mColorSpan = Util.modifyAlpha(ContextCompat.getColor(mContext, R.color.black), 0.87f);
        mColorSpanData = Util.modifyAlpha(ContextCompat.getColor(mContext, R.color.black), 0.54f);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //   Util.logD(LOG_TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_find_list, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        holder.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Pharmacy pharmacy = mList.get(position);
                mCallback.onClickPhone(pharmacy.getPhone());
            }
        });
        holder.ivPhone.setOnClickListener(this);
        holder.ivGo.setOnClickListener(this);
        holder.ivShare.setOnClickListener(this);
        holder.ivSchedule.setOnClickListener(this);
        holder.ivFavorite.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        }

        return holder;
    }

    @Override
    public void onClick(View v) {

        final int id = v.getId();
        MyViewHolder holder = (MyViewHolder) v.getTag();
        final int position = holder.getAdapterPosition();
        final Pharmacy pharmacy = mList.get(position);
        switch (id) {
            case R.id.ivPhone:
                mCallback.onClickPhone(pharmacy.getPhone());
                break;
            case R.id.ivGo:
                mCallback.onClickGo(pharmacy);
                break;
            case R.id.ivShare:
                mCallback.onClickShare(pharmacy);
                break;
            case R.id.ivSchedule:

                break;
            case R.id.ivFavorite:
                mCallback.onClickFavorite(pharmacy);
                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setDelayedTransition() {
        TransitionManager.beginDelayedTransition(mRecyclerView, expandCollapse);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

//        Observable.from(new String[]{"1","2"})
//
//                .delay(5000, TimeUnit.MILLISECONDS)
//                .map(s -> {
//                    return "1";
//                })

//        Observable.timer(50, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> {
//                    runEnterAnimation(holder.itemView, position);
//                    bindHolder(holder, position);
//                });
        //  runEnterAnimation(holder.itemView, position);

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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {

        if ((payloads.contains(EXPAND) || payloads.contains(COLLAPSE))) {
            setExpanded(holder, position == expandedPosition);
        } else {
            onBindViewHolder(holder, position);
        }
    }

    private void bindHolder(MyViewHolder holder, int position) {

        ForegroundColorSpan span = new ForegroundColorSpan(mColorSpan);
        ForegroundColorSpan spanData = new ForegroundColorSpan(mColorSpanData);
        String sLocality = mContext.getString(R.string.fca_localidad) + Constants.SPACE;
        String sDireccion = mContext.getString(R.string.fca_direccion) + Constants.SPACE;
        String sPhone = mContext.getString(R.string.fca_telefono) + Constants.SPACE;
        Pharmacy pharmacy = mList.get(position);
        holder.tvName.setText(pharmacy.getName());
        String locality = sLocality + pharmacy.getLocality() + Constants.COMMA
                + Constants.SPACE
                + pharmacy.getPostal_code()
                + Constants.SPACE
                + pharmacy.getProvince();

        holder.tvLocality.setText(createSpannable(locality, sLocality, span, spanData));


        holder.tvAdress.setText(createSpannable(sDireccion + pharmacy.getAddress(), sDireccion, span, spanData));
        holder.tvDistance.setText(createSpannable(mContext.getString(R.string.format_distancia, pharmacy.getDistance()),
                mContext.getString(R.string.fca_distancia) + Constants.SPACE, span, spanData));
        holder.tvTxtPhone.setText(createSpannable(sPhone + pharmacy.getPhoneFormatted(), sPhone, span, spanData));
        holder.ivFavorite.setImageResource(pharmacy.isFavorite() ? R.drawable.ic_heart : R.drawable.ic_heart_outline);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            final boolean isExpanded = position == expandedPosition;
            setExpanded(holder, isExpanded);
        } else {
            setExpanded(holder, true);
        }
        //i have chosen to do this because the code is very repetitive en the click handler
        holder.ivPhone.setTag(holder);
        holder.ivGo.setTag(holder);
        holder.ivShare.setTag(holder);
        holder.ivSchedule.setTag(holder);
        holder.ivFavorite.setTag(holder);
    }

    private SpannableString createSpannable(String string, String stringToSpan,
                                            ForegroundColorSpan span, ForegroundColorSpan span2) {
        SpannableString spannable = new SpannableString(string);
        spannable.setSpan(span, 0, stringToSpan.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(span2, stringToSpan.length(), string.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;

    }

    private void setExpanded(MyViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        holder.llOptionsRow.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivGo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivPhone.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivSchedule.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivShare.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivFavorite.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        //     Util.logD(LOG_TAG,"getItemCount:");
        if (mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<Pharmacy> incommingList) {

        mList = incommingList;
        if (mList == null) {
            mList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView tvName;
        @BindView(R.id.locality)
        TextView tvLocality;
        @BindView(R.id.address)
        TextView tvAdress;
        @BindView(R.id.distance)
        TextView tvDistance;
        @BindView(R.id.txtPhone)
        TextView tvTxtPhone;
        @BindView(R.id.ivGo)
        ImageView ivGo;
        @BindView(R.id.ivPhone)
        ImageView ivPhone;
        @BindView(R.id.ivShare)
        ImageView ivShare;
        @BindView(R.id.ivSchedule)
        ImageView ivSchedule;
        @BindView(R.id.ivFavorite)
        ImageView ivFavorite;
        @BindView(R.id.optionsRow)
        LinearLayout llOptionsRow;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public interface OnClickHandler {
        void onClickGo(Pharmacy pharmacy);

        void onClickFavorite(Pharmacy pharmacy);

        void onClickPhone(String phone);

        void onClickShare(Pharmacy pharmacy);
    }
}
