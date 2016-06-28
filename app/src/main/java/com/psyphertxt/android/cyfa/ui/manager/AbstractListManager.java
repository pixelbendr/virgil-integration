package com.psyphertxt.android.cyfa.ui.manager;

import com.psyphertxt.android.cyfa.Config;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Codebendr on 06/10/2015.
 */
public abstract class AbstractListManager {

    RecyclerView.Adapter adapter;
    private List<Object> list;

    public AbstractListManager(RecyclerView.Adapter adapter, List<Object> list) {
        this.list = list;
        this.adapter = adapter;
    }

    public long getId(int position) {
        return list.get(position).hashCode();

    }

    public void add(Object item) {
        list.add(item);
    }

    public void addFirst(Object item) {
        list.add(0, item);
        adapter.notifyDataSetChanged();
    }

    public void addItem(Object item) {
        add(item);
        adapter.notifyDataSetChanged();
    }

    public void updateItem(Object item) {
        int position = list.indexOf(item);
        if (position >= 0) {
            adapter.notifyItemChanged(position);
        }
    }

    public void moveItem(Object item) {
        int position = list.indexOf(item);
        if (position >= 0) {
            list.remove(position);
            add(item);
            adapter.notifyItemMoved(position, 0);
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void refill(List<Object> list) {
        if (this.list.size() > Config.NUMBER_ZERO) {
            this.list.clear();
        }
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public void refillManually(List<Object> list) {
        for (Object object : list) {
            add(object);
        }
        adapter.notifyDataSetChanged();
    }

    public List<Object> getData(){
        return list;
    }

    public void clearItems() {
        list.clear();
    }


}
