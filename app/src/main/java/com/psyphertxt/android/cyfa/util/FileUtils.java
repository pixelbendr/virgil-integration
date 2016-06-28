package com.psyphertxt.android.cyfa.util;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This Convenient class is responsible for checking and validation network connectivity and
 * connectivity problems.
 */
public class FileUtils {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIATYPE_IMAGE = 4;
    public static final int MEDIATYPE_VIDEO = 5;
    //getting megabytes
    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;

    public Uri mMediaUri;

    public static boolean isExternalStorageAvailable() {

        return Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED);

    }

    public static Uri getOutputMediaFileUri(int mediatype) {

        if (isExternalStorageAvailable()) {

            //get external storage picture directory
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Cyfa"
            );

            //create storage directory
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdir()) {
                    return null;
                }
            }
            //create a file name
            //create file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
            String path = mediaStorageDir.getPath() + File.separator;
            if (mediatype == MEDIATYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            } else if (mediatype == MEDIATYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
            } else {
                return null;
            }
            //return file's URI
            return Uri.fromFile(mediaFile);

        } else {
            return null;
        }
    }
}
