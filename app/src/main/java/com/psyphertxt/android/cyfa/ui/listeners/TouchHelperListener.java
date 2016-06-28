package com.psyphertxt.android.cyfa.ui.listeners;

import com.psyphertxt.android.cyfa.ui.adapters.TouchHelperAdapter;
import com.psyphertxt.android.cyfa.ui.viewholder.TouchHelperViewHolder;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to listen for {@link
 * TouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link TouchHelperViewHolder}.
 *
 * @author Pixelbendr
 */
public class TouchHelperListener extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;
    public static final int SWIPE_RIGHT = 32;
    public static final int SWIPE_LEFT = 16;
    private final TouchHelperAdapter adapter;

    public TouchHelperListener(TouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 0;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            if (direction == SWIPE_RIGHT) {
                adapter.onItemEdit(viewHolder.getAdapterPosition());
            } else if (direction == SWIPE_LEFT) {
                adapter.onItemDelete(viewHolder.getAdapterPosition());
            }
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            // Let the view holder know that this item is being moved or dragged
            TouchHelperViewHolder helperViewHolder = (TouchHelperViewHolder) viewHolder;
            helperViewHolder.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        // Tell the view holder it's time to restore the idle state
        TouchHelperViewHolder helperViewHolder = (TouchHelperViewHolder) viewHolder;
        helperViewHolder.onItemClear();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float width = (float) viewHolder.itemView.getWidth();
            float alpha = 1.0f - Math.abs(dX) / width;
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
