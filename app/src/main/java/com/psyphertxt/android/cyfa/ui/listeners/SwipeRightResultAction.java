package com.psyphertxt.android.cyfa.ui.listeners;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.psyphertxt.android.cyfa.ui.adapters.ChatAdapter;

/**
 * Created by Codebendr on 06/10/2015.
 */
public class SwipeRightResultAction extends SwipeResultActionRemoveItem {
    private ChatAdapter chatAdapter;
    private final int position;

    public SwipeRightResultAction(ChatAdapter adapter, int position) {
        chatAdapter = adapter;
        this.position = position;
    }

    @Override
    protected void onPerformAction() {
        super.onPerformAction();

        chatAdapter.listManager.removeItem(position);
        chatAdapter.notifyItemRemoved(position);
    }

    @Override
    protected void onSlideAnimationEnd() {
        super.onSlideAnimationEnd();

        if (chatAdapter.getSwipeEventListener() != null) {
            chatAdapter.getSwipeEventListener().onItemRemoved(position);
        }
    }

    @Override
    protected void onCleanUp() {
        super.onCleanUp();
        // clear the references
        chatAdapter = null;
    }
}
