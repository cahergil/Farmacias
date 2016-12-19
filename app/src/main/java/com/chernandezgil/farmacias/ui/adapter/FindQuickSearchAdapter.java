package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chernandezgil.farmacias.R;
import com.chernandezgil.farmacias.model.SuggestionsBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carlos on 08/09/2016.
 */
public class FindQuickSearchAdapter extends RecyclerView.Adapter<FindQuickSearchAdapter.ViewHolder> implements View.OnClickListener {
    private static final String LOG_TAG = FindQuickSearchAdapter.class.getSimpleName();
    private List<SuggestionsBean> mList;
    private  Context mContext;
    private Drawable mLupa;
    private Drawable mHistory;
    private int mColorSpan;
    public static final int HISTORY_ROW = 0;
    public static final int DATABASE_ROW = 1;
    public final OnClickHandler mCallback;
    private String mSearchString;


    public String getmSearchString() {
        return mSearchString;
    }

    public void setmSearchString(String mSearchString) {
        this.mSearchString = mSearchString;
    }

    public FindQuickSearchAdapter(Context context, OnClickHandler callback){
        mContext = context;
        mLupa = ContextCompat.getDrawable(mContext,R.drawable.ic_lupa_suggestions);
        mHistory = ContextCompat.getDrawable(mContext,R.drawable.ic_history);
        mColorSpan = ContextCompat.getColor(mContext,R.color.blue_mobile_web);
        mCallback = callback;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //   Utils.logD(LOG_TAG,"onCreateViewHolderQuickSearch");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_suggestions_bean,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvText.setOnClickListener(this);
        holder.ivClearItem.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //     Utils.logD(LOG_TAG,"onBindViewHolderQuickSearch:position"+position);
        SuggestionsBean suggestionsBean = mList.get(position);
        holder.ivIcon.setImageDrawable(suggestionsBean.getImageId()==0? mHistory : mLupa);
        holder.ivClearItem.setVisibility(suggestionsBean.getImageId()==0? View.VISIBLE:View.INVISIBLE);
        holder.ivClearItem.setTag(position);
        String name = suggestionsBean.getName();
        int startIndex = name.toLowerCase().indexOf(mSearchString.toLowerCase());

        if(startIndex !=-1) {
            SpannableString spannableName = new SpannableString(name);
            ForegroundColorSpan span = new ForegroundColorSpan(mColorSpan);
            spannableName.setSpan(span, startIndex,startIndex + mSearchString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            holder.tvText.setText(spannableName);
        } else {
            holder.tvText.setText(name);
        }
    }

    @Override
    public int getItemCount() {
       // Utils.logD(LOG_TAG,"getItemCountQuickSearch:"+count++);
        if(mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<SuggestionsBean> newList) {
        mList = newList;
        notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        int id= view.getId();
        switch (id) {
            case R.id.textView:
                TextView name= (TextView)view;
                mCallback.onClickSuggestions(name.getText().toString());
                break;
            case R.id.ivClearItem:
                int position = (int) view.getTag();
                String text = mList.get(position).getName();
                mCallback.onClickClearHistoryItem(text);
                break;
        }

    }

    public static interface OnClickHandler {
        public  void onClickSuggestions(String a);
        public  void onClickClearHistoryItem(String text);
    }
     static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView ivIcon;
        @BindView(R.id.textView)
        TextView tvText;
        @BindView(R.id.ivClearItem)
        ImageView ivClearItem;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
