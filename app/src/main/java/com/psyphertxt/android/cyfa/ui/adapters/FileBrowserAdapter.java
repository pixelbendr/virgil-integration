package com.psyphertxt.android.cyfa.ui.adapters;

import com.hannesdorfmann.adapterdelegates.AdapterDelegatesManager;
import com.psyphertxt.android.cyfa.model.FileSystemEntry;
import com.psyphertxt.android.cyfa.ui.delegates.FileBrowserAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.delegates.WalkthroughAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.manager.ListManager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pie on 9/23/15.
 */
public class FileBrowserAdapter extends RecyclerView.Adapter {

    private AdapterDelegatesManager<List<? extends Object>> delegatesManager;
    private List<Object> list;
    public ListManager listManager;

    public FileBrowserAdapter(Activity activity) {
        this.list = new ArrayList<>();
        listManager = new ListManager(this, list);
        delegatesManager = new AdapterDelegatesManager<>();
        delegatesManager.addDelegate(new FileBrowserAdapterDelegate(activity, 0));
        delegatesManager.addDelegate(new WalkthroughAdapterDelegate(activity, 1));
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    /*@Override
    public FileSytemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_browser_view, parent, false);
        return new FileSytemViewholder(view);
    }

    @Override
    public void onBindViewHolder(FileSytemViewholder holder, int position) {
        FileSystemEntry fileSystemEntry = this.fileSystemEntries.get(position);

        if(fileSystemEntry.exists()){
            if(fileSystemEntry.isImageFile()){
                Bitmap photo = BitmapFactory.decodeFile(fileSystemEntry.getPath());
                holder.entryTypeView.setImageBitmap(photo);
            }else if(fileSystemEntry.isVideoFile()){
                holder.entryTypeView.setImageResource(R.drawable.ic_video_file);
            }else if(fileSystemEntry.isDirectory()){
                holder.entryTypeView.setImageResource(R.drawable.ic_directory);
            }
        }
        holder.entryNameLabel.setText(fileSystemEntry.getName());
        holder.descriptionNameLabel.setText(fileSystemEntry.getDescription());
    }*/
}
