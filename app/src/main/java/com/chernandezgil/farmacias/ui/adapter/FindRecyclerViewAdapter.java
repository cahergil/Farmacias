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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chernandezgil.farmacias.BuildConfig;
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
    private static final int ANIMATED_ITEMS_COUNT = 10;
    private int mColorSpan;

    public FindRecyclerViewAdapter(Context context, RecyclerView recyclerview) {
        mRecyclerView = recyclerview;
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            expandCollapse = new AutoTransition();
            expandCollapse.setDuration(200);
            expandCollapse.setInterpolator(AnimationUtils.loadInterpolator(mContext,
                    android.R.interpolator.fast_out_slow_in));
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
        mColorSpan = ContextCompat.getColor(mContext, R.color.black);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //   Util.logD(LOG_TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_find_list1, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    setDelayedTransition();
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
        holder.tvPlus.setOnClickListener(this);
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setDelayedTransition() {
        TransitionManager.beginDelayedTransition(mRecyclerView, expandCollapse);
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

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Util.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
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

        holder.tvLocality.setText(createSpannable(locality, sLocality, span));
        holder.tvPlus.setTag(holder);

        holder.tvAdress.setText(createSpannable(sDireccion + pharmacy.getAddress(), sDireccion, span));
        holder.tvDistance.setText(createSpannable(mContext.getString(R.string.format_distancia, pharmacy.getDistance()),
                mContext.getString(R.string.fca_distancia) + Constants.SPACE, span));
        holder.tvTxtPhone.setText(createSpannable(sPhone + pharmacy.getPhoneFormatted(), sPhone, span));
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT) {
            final boolean isExpanded = position == expandedPosition;
            setExpanded(holder, isExpanded);
        } else {
            setExpanded(holder,true);
        }
    }

    private SpannableString createSpannable(String string, String stringToSpan, ForegroundColorSpan span) {
        SpannableString spannable = new SpannableString(string);
        spannable.setSpan(span, 0, stringToSpan.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;

    }

    private void setExpanded(MyViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        //   holder.reply.setVisibility((isExpanded && allowComment) ? View.VISIBLE : View.GONE);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.plus:


                break;
        }

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
        @BindView(R.id.plus)
        TextView tvPlus;
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
}
