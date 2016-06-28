package com.psyphertxt.android.cyfa.ui.adapters;

import com.hannesdorfmann.adapterdelegates.AdapterDelegatesManager;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.model.StatusFeed;
import com.psyphertxt.android.cyfa.ui.delegates.StatusAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.delegates.WalkthroughAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.manager.ListManager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusAdapter extends RecyclerView.Adapter {

    private AdapterDelegatesManager<List<? extends Object>> delegatesManager;
    private List<Object> list;
    public ListManager listManager;

    public StatusAdapter(Activity activity) {
        super();
        this.list = new ArrayList<>();
        listManager = new ListManager(this, list);
        delegatesManager = new AdapterDelegatesManager<>();
        delegatesManager.addDelegate(new StatusAdapterDelegate(activity, 0));
        delegatesManager.addDelegate(new WalkthroughAdapterDelegate(activity, 1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void resetCount(Object item) {
        //find the item in the list which is available in memory
        Map statusMap = findByUserId(((StatusFeed) item).getUserId());

        //if the item is found, get the position and the item model
        if (statusMap != null) {
            StatusFeed sf = (StatusFeed) statusMap.get(Config.VALUE);
            if (sf.getCount() != 0) {
                sf.setCount(0);
                listManager.updateItem(sf);
            }
        }
    }

    //TODO fix bug
    public void updateStatus(Map object, Object item) {

        //find the item in the list which is available in memory
        Map statusMap = findByUserId(((StatusFeed) item).getUserId());

        //convert the object from the server/locally into a hash map
        HashMap status = Status.setStatus(object);

        //if the item is found, get the position and the item model
        if (statusMap != null) {

            int position = (int) statusMap.get(Config.KEY);
            StatusFeed sf = (StatusFeed) statusMap.get(Config.VALUE);

            //update value in the found list item
            sf.updateFeed(status);

            if (position == 0) {
                listManager.updateItem(sf);
            } else {
                listManager.moveItem(sf);
            }

        } else {
            listManager.addFirst(item);
        }

    }

    public void notifyChange(Map object, Object item) {

        //find the item in the list which is available in memory
        Map statusMap = findByUserId(((StatusFeed) item).getUserId());

        //convert the object from the server/locally into a hashmap
        HashMap status = Status.setStatus(object);

        //if the item is found, get the position and the item model
        if (statusMap != null) {

            int position = (int) statusMap.get(Config.KEY);
            StatusFeed sf = (StatusFeed) statusMap.get(Config.VALUE);

            //update value in the found list item
            sf.updateFeed(status);

            //check to see if the item is already at the first position
            if (position == 0) {
                listManager.updateItem(sf);
            } else {
                listManager.moveItem(sf);
            }

        } else {
            listManager.addItem(item);
        }
    }

    public Map findByUserId(String userId) {
        HashMap<String, Object> hashMap = null;
        if (list != null && !list.isEmpty()) {
            for (int i = 0, len = getItemCount(); i < len; i++) {
                if (list.get(i) instanceof StatusFeed) {
                    StatusFeed sf = (StatusFeed) list.get(i);
                    if (sf.getUserId().equals(userId)) {
                        hashMap = new HashMap<>();
                        hashMap.put(Config.KEY, i);
                        hashMap.put(Config.VALUE, sf);
                    }
                }
            }
        }
        return hashMap;
    }

    @Override
    public int getItemViewType(int position) {
        return delegatesManager.getItemViewType(list, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return delegatesManager.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        delegatesManager.onBindViewHolder(list, i, viewHolder);
    }
}
