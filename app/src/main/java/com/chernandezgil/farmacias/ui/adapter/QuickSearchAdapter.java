package com.chernandezgil.farmacias.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
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
public class QuickSearchAdapter extends RecyclerView.Adapter<QuickSearchAdapter.ViewHolder> implements View.OnClickListener {
    private List<SuggestionsBean> mList;
    private  Context mContext;
    private Drawable lupa;
    private Drawable history;
    public static final int HISTORY_ROW = 0;
    public static final int DATABASE_ROW = 1;
    public final OnClickHandler mCallback;

    public QuickSearchAdapter(Context context, OnClickHandler callback){
        mContext = context;
        lupa = AppCompatResources.getDrawable(mContext,R.drawable.ic_lupa_suggestions);
        history = AppCompatResources.getDrawable(mContext,R.drawable.ic_history);
        mCallback = callback;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_suggestions_bean,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvText.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.ivIcon.setImageDrawable(mList.get(position).getImageId()==0?history:lupa);
        holder.tvText.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(mList == null) return 0;
        return mList.size();
    }

    public void swapData(List<SuggestionsBean> newList) {
        mList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        TextView name= (TextView)view;
        mCallback.onClickSuggestions(name.getText().toString());
    }

    public static interface OnClickHandler {
        public  void onClickSuggestions(String a);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView ivIcon;
        @BindView(R.id.textView)
        TextView tvText;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
