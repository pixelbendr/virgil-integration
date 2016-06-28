package com.psyphertxt.android.cyfa.ui.delegates;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.FileSystemEntry;
import com.psyphertxt.android.cyfa.ui.viewholder.FileBrowserViewHolder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public class FileBrowserAdapterDelegate extends AbsAdapterDelegate<List<? extends Object>> {

    private LayoutInflater inflater;

    public FileBrowserAdapterDelegate(Activity activity, int viewType) {
        super(viewType);
        inflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<? extends Object> objects, int i) {
        return objects.get(i) instanceof FileSystemEntry;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new FileBrowserViewHolder(inflater.inflate(R.layout.file_browser_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<? extends Object> objects, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        FileBrowserViewHolder fileBrowserViewHolder = (FileBrowserViewHolder) viewHolder;
        fileBrowserViewHolder.setFile((FileSystemEntry) objects.get(i));
    }
}
