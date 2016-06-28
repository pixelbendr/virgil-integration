package com.psyphertxt.android.cyfa;

import com.psyphertxt.android.cyfa.model.FileSystemEntry;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Codebendr on 05/11/2015.
 */
public class FileConfig {


    public static final String SELECTED_IMAGE_DATA = "SELECTED_IMAGE_DATA";
    public static final String SELECTED_VIDEO_URL = "SELECTED_VIDEO_URL";

    /*
Request code for processing response from native image selection activity (eg. gallery, photos etc)
Used in onActivityResult to get a handle to the image file that was selected by the user
*/
    public static final int IMAGE_CAPTURE_ACTIVITY_REQUEST_CODE = 100;
    /*
    Request code for processing response from video selection activity
    Used in onActivityResult to get a handle to the video file that was selected by the user
     */
    public static final int VIDEO_CAPTURE_ACTIVITY_REQUEST_CODE = 200;
    /*
    Passed as an extras to @UIMediaPreviewActivity to determine the type of content (image/video that was selected)
    This is important because UIMediaPreveiwActivity is used to preview both pictures and videos
     */
    public static final String MEDIA_TYPE_ID = "MEDIA_TYPE_ID";
    /*
    MEDIA_TYPE_PHOTO is the value for the MEDIA_TYPE_ID key passed to UIMediaPreviewActivity when an image is selected
     */
    public static final int MEDIA_TYPE_PHOTO = 1;
    /*
    MEDIA_TYPE_VIDEO is the value for the MEDIA_TYPE_ID key passed to UIMediaPreviewActivity when a video is selected
     */
    public static final int MEDIA_TYPE_VIDEO = 2;
    /*
    CAPTURED_PHOTO_DATA is the name of the key used to pass the photo (as a Bitmap) taken with the device's camera to UIMediaPreviewActivity
    for previewing.
     */
    public static final String CAPTURED_PHOTO_DATA = "CAPTURED_PHOTO_DATA";
    /*
    CAPTURED_VIDEO_URI is the key for passing the url to the video captured to UIMediaPreviewActivity for previewing
     */
    public static final String CAPTURED_VIDEO_URI = "CAPTURED_VIDEO_URI";

    /*
    FILE_BROWSER_ROOT_DIRECTORY is the key used to pass the root directory from which UIFileBrowserActivity should begin traversing
    Values are Environment.DIRECTORY_DCIM or Environment.DIRECTORY_MOVIES
     */
    public static final String FILE_BROWSER_ROOT_DIRECTORY = "FILE_BROWSER_ROOT_DIRECTORY";
    /*
    PHOTOS_ROOT_DIRECTORY is the name of the system's designated directory for storing photos
     */
    public static final String PHOTOS_ROOT_DIRECTORY = Environment.DIRECTORY_DCIM;
    /*
    VIDEOS_ROOT_DIRECTORY is the name of the system's designated directory for storing videos
     */
    public static final String VIDEOS_ROOT_DIRECTORY = Environment.DIRECTORY_MOVIES;


}
