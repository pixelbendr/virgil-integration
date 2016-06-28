package com.psyphertxt.android.cyfa.ui.listeners;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.ui.adapters.ChatAdapter;

/**
 * Created by Codebendr on 06/10/2015.
 */
public class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
    private ChatAdapter adapter;
    private final int position;
    private boolean setPinned;

    public SwipeLeftResultAction(ChatAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected void onPerformAction() {
        super.onPerformAction();

        Chat chat = adapter.getChat();

        if (!chat.isPinned()) {
            chat.isPinned(true);
            adapter.notifyItemChanged(position);
            setPinned = true;
        }
    }

    @Override
    protected void onSlideAnimationEnd() {
        super.onSlideAnimationEnd();

        if (setPinned && adapter.getSwipeEventListener() != null) {
            adapter.getSwipeEventListener().onItemPinned(position);
        }
    }

    @Override
    protected void onCleanUp() {
        super.onCleanUp();
        // clear the references
        adapter = null;
    }
}
