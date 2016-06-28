package com.psyphertxt.android.cyfa.ui.activity;

import com.psyphertxt.android.cyfa.FileConfig;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.FileManager;
import com.psyphertxt.android.cyfa.model.FileSystemEntry;
import com.psyphertxt.android.cyfa.ui.adapters.FileBrowserAdapter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Created by pie on 9/23/15.
 */
public class FileBrowserActivity extends AppCompatActivity {

    @InjectView(R.id.recyclerview)
    protected RecyclerView recyclerview;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.fab_back_navi)
    FloatingActionButton fabBackNavi;

    @InjectView(R.id.loader_relative_layout)
    RelativeLayout loaderRelativeLayout;

    FileManager fileManager;
    FileBrowserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_file_browser);

        ButterKnife.inject(this);

        setupToolbar();
        setupAdapter();

        initFolder();

        //   this.registerEventHanders();
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        fabBackNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileBrowserActivity.this.onBackPressed();
            }
        });


    }

    private void setupAdapter() {

        adapter = new FileBrowserAdapter(this);
        recyclerview.setItemAnimator(new SlideInDownAnimator());
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);

    }

    private void initFolder() {
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(FileConfig.FILE_BROWSER_ROOT_DIRECTORY)) {
            fileManager = new FileManager();
            fileManager.getPath(FileBrowserActivity.this, extras.getString(FileConfig.FILE_BROWSER_ROOT_DIRECTORY));
            //  adapter.listManager.refill(fileManager.getFiles());
            for (Object object : fileManager.getFiles()) {
                FileSystemEntry fileSystemEntry = (FileSystemEntry) object;
                adapter.listManager.addItem(fileSystemEntry);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // FileBrowserActivity.this.onBackPressed();
        /*fileManager.traversePrevPath(new CallbackListener.completion() {
            @Override
            public void done() {
                //FileBrowserActivity.this.onBackPressed();
            }
        });*/
    }
}
