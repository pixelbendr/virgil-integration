package com.psyphertxt.android.cyfa.ui.listeners;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.ui.adapters.ChatAdapter;

/**
 * Created by Codebendr on 06/10/2015.
 */
public class UnpinResultAction extends SwipeResultActionDefault {
    private ChatAdapter adapter;
    private final int position;

    public UnpinResultAction(ChatAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected void onPerformAction() {
        super.onPerformAction();

        Chat chat = adapter.getChat();
        if (chat.isPinned()) {
            chat.isPinned(false);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    protected void onCleanUp() {
        super.onCleanUp();
        // clear the references
        adapter = null;
    }
}