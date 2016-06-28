package com.psyphertxt.android.cyfa.ui.delegates;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.StatusFeed;
import com.psyphertxt.android.cyfa.ui.viewholder.StatusViewHolder;

import java.util.List;

public class StatusAdapterDelegate extends AbsAdapterDelegate<List<? extends Object>> {

    private LayoutInflater inflater;

    public StatusAdapterDelegate(Activity activity, int viewType) {
        super(viewType);
        inflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<? extends Object> objects, int i) {
        return objects.get(i) instanceof StatusFeed;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new StatusViewHolder(inflater.inflate(R.layout.status_message_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<? extends Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        StatusViewHolder statusViewHolder = (StatusViewHolder) viewHolder;
        statusViewHolder.setStatus((StatusFeed) objects.get(i));
    }
}
