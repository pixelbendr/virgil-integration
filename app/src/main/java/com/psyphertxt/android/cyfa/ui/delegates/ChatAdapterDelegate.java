package com.psyphertxt.android.cyfa.ui.delegates;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.ui.viewholder.ChatViewHolder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public class ChatAdapterDelegate extends AbsAdapterDelegate<List<? extends Object>> {

    private LayoutInflater inflater;

    public ChatAdapterDelegate(Activity activity, int viewType) {
        super(viewType);
        inflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<? extends Object> objects, int i) {
        return objects.get(i) instanceof Chat;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ChatViewHolder(inflater.inflate(R.layout.chat_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<? extends Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        ChatViewHolder chatTextViewHolder = (ChatViewHolder) viewHolder;
        chatTextViewHolder.setChat((Chat) objects.get(i));
    }
}

