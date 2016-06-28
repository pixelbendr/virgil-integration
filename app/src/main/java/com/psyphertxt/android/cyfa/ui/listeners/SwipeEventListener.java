package com.psyphertxt.android.cyfa.ui.listeners;

import android.view.View;

/**
 * Created by Codebendr on 06/10/2015.
 */
public interface SwipeEventListener {
    void onItemRemoved(int position);

    void onItemPinned(int position);

    void onItemViewClicked(View v, boolean pinned);
}
