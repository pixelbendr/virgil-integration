package com.psyphertxt.android.cyfa.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.Fab;
import com.psyphertxt.android.cyfa.ui.manager.ListManager;
import com.psyphertxt.android.cyfa.ui.viewholder.ContextUserViewHolder;
import com.psyphertxt.android.cyfa.ui.viewholder.FabViewHolder;
import com.psyphertxt.android.cyfa.util.Testable;

import java.util.ArrayList;
import java.util.List;

public class ContextUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> contextUserList;
    private static final int ADD_FAB_VIEW = 0;
    private static final int CONTEXT_USER_VIEW = 1;
    public ListManager listManager;

    public ContextUserAdapter() {
        super();
        contextUserList = new ArrayList<>();
        listManager = new ListManager(this,contextUserList);
        setHasStableIds(true);
    }


    @Override
    public int getItemCount() {
        return contextUserList.size();
    }

    @Override
    public long getItemId(int position) {
        return listManager.getId(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (contextUserList.get(position) instanceof ContextUser) {
            return CONTEXT_USER_VIEW;
        } else if (contextUserList.get(position) instanceof Fab) {
            return ADD_FAB_VIEW;
        }

        throw new RuntimeException("Unknown view type in status adapter");
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case CONTEXT_USER_VIEW:
                return new ContextUserViewHolder(LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.context_user_view, viewGroup, false));
            default:
                return new FabViewHolder(LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.add_fab_view, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case CONTEXT_USER_VIEW:
                ContextUserViewHolder contextUserViewHolder = (ContextUserViewHolder) viewHolder;
                contextUserViewHolder.setContextUser((ContextUser) contextUserList.get(i));
                break;
            default:
                FabViewHolder fabViewHolder = (FabViewHolder) viewHolder;
                fabViewHolder.setAdapterView(this);
                fabViewHolder.setFab((Fab) contextUserList.get(i));
        }
    }

}
