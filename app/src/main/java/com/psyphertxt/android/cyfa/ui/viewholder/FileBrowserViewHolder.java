package com.psyphertxt.android.cyfa.ui.viewholder;

import com.psyphertxt.android.cyfa.FileConfig;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.FileManager;
import com.psyphertxt.android.cyfa.model.FileSystemEntry;
import com.psyphertxt.android.cyfa.ui.activity.MediaPreviewActivity;
import com.psyphertxt.android.cyfa.ui.adapters.ContextUserAdapter;
import com.psyphertxt.android.cyfa.ui.adapters.FileBrowserAdapter;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FileBrowserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @InjectView(R.id.image_file_type)
    ImageView imgFileType;
    @InjectView(R.id.text_file_name)
    TextView txtFileName;
    @InjectView(R.id.text_file_description)
    TextView txtFileDesc;

    private Context context;
    private FileSystemEntry fileSystemEntry;

    private FileBrowserAdapter getAdapterView(View view) {
        // search for recycler view from root and remove view
        RecyclerView recyclerview = (RecyclerView) view.getRootView().findViewById(R.id.recyclerview);
        return ((FileBrowserAdapter) recyclerview.getAdapter());
    }

    private RelativeLayout getLoaderView(View view) {
        // search for recycler view from root and remove view
        return (RelativeLayout) view.getRootView().findViewById(R.id.loader_relative_layout);
    }


    public FileBrowserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
        this.context = itemView.getContext();

    }

    public FileSystemEntry getFile() {
        return this.fileSystemEntry;
    }

    public void setFile(FileSystemEntry fileSystemEntry) {

        this.fileSystemEntry = fileSystemEntry;

        if (fileSystemEntry.exists()) {
            if (fileSystemEntry.isImageFile()) {
                Bitmap photo = BitmapFactory.decodeFile(fileSystemEntry.getPath());
                imgFileType.setImageBitmap(photo);
            } else if (fileSystemEntry.isVideoFile()) {
                imgFileType.setImageResource(R.drawable.ic_video_file);
            } else if (fileSystemEntry.isDirectory()) {
                imgFileType.setImageResource(R.drawable.ic_directory);
            }
        }
        txtFileName.setText(fileSystemEntry.getName());
        txtFileDesc.setText(fileSystemEntry.getDescription());

    }

    @Override
    public void onClick(final View view) {

        FileManager fileManager = new FileManager();
        FileSystemEntry fileSystemEntry = getFile();

        if (fileSystemEntry.isDirectory()) {

            fileManager.traverseNextPath(fileSystemEntry.getPath());
            getAdapterView(view).listManager.refill(fileManager.getFiles());

        } else {

            if (fileSystemEntry.exists()) {

                if (fileSystemEntry.isImageFile()) {

                    Intent intent = new Intent(context, MediaPreviewActivity.class);
                    Bitmap photo = BitmapFactory.decodeFile(fileSystemEntry.getPath());
                    intent.putExtra(FileConfig.SELECTED_IMAGE_DATA, photo);
                    context.startActivity(intent);

                } else if (fileSystemEntry.isVideoFile()) {

                    Intent intent = new Intent(context, MediaPreviewActivity.class);
                    Uri videoUri = Uri.fromFile(new File(fileSystemEntry.getPath()));
                    intent.putExtra(FileConfig.SELECTED_VIDEO_URL, videoUri);
                    context.startActivity(intent);

                }

            } else {

                String message = "Selected file does not exist!";
                Alerts.show(context, "File Error", message);

            }
        }

    }

}