package com.psyphertxt.android.cyfa.model;

import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Codebendr on 06/11/2015.
 */
public class FileManager {

    private Stack<String> parentPaths = new Stack<>();
    private File rootFile = null;
    List<Object> files;

  public void getPath(Context context,String targetDirectory){
        if (isExternalStorageReadable()) {
            rootFile = new File(Environment.getExternalStoragePublicDirectory(targetDirectory).getPath());
            traverseNextPath(rootFile.getAbsolutePath());
        } else {
            String errorMessage = "SDCard not mounted!";
            Alerts.show(context, "Error", errorMessage);
        }
    }

    public List<Object> getFiles(){
        return files;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void traverseNextPath(String path) {
        files = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            parentPaths.push(file.getParent());
            File[] children = file.listFiles();
            for (File f : children) {
                files.add(FileSystemEntry.fromFile(f));
            }
        }
    }

    public  void traversePrevPath(CallbackListener.completion completion){
        if (parentPaths.pop() != null) {
            String parentPath = parentPaths.pop();
            File parentFile = new File(parentPath);
            if (parentFile.getAbsolutePath().equalsIgnoreCase(rootFile.getParentFile().getAbsolutePath())) {
              //  super.onBackPressed();
                completion.done();
            } else {
                File file = new File(parentPath);
                if (file.isDirectory()) {
                    traverseNextPath(parentPath);
                }
            }
        } else {
           // super.onBackPressed();
            completion.done();
        }
    }
}
