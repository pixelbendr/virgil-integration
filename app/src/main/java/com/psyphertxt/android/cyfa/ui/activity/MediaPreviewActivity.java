package com.psyphertxt.android.cyfa.ui.activity;

import com.psyphertxt.android.cyfa.FileConfig;
import com.psyphertxt.android.cyfa.R;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by pie on 9/22/15.
 * UIMediaPreviewActivity is used to playback/preview both photo and video content passed from UIMediaActivity
 * This activities layout features a Framelayout with both ImageView(R.id.iv_image_preview) and VideoView
 * (R.id.vv_video_preview) as root subviews of the parent FrameLayout
 * <p>
 * Which of the 2 subviews (ImageView/VideoView) gets shown depends on the value of MEDIA_TYPE_ID
 * passed via the explicit intent used for starting UIMediaPreviewActivity. This can be either
 * MEDIA_TYPE_PHOTO for photos or MEDIA_TYPE_VIDEO for videos
 */
public class MediaPreviewActivity extends AppCompatActivity {
    @InjectView(R.id.iv_image_preview)
    protected ImageView previewImageView;
    @InjectView(R.id.vv_video_preview)
    protected VideoView previewVideoView;

    private Bitmap photo = null;
    private Uri videoUri = null;
    private boolean isPreviewingPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ui_media_preview_activity_layout);
        ButterKnife.inject(this);
        this.initViews();
    }

    /**
     * Toggles visibility of the root imageview and video view based on the
     * value of MEDIA_TYPE_ID passed.
     */
    private void initViews() {
        this.previewImageView.setVisibility(View.INVISIBLE);
        this.previewVideoView.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(FileConfig.MEDIA_TYPE_ID)) {
            int mediaType = extras.getInt(FileConfig.MEDIA_TYPE_ID);
            switch (mediaType) {
                /**
                 * Called when UIMediaPreviewActivity is invoked to preview a photo taken with native camera app
                 */
                case FileConfig.MEDIA_TYPE_PHOTO:
                    if (extras.containsKey(FileConfig.CAPTURED_PHOTO_DATA)) {
                        Bitmap photo = (Bitmap) extras.getParcelable(FileConfig.CAPTURED_PHOTO_DATA);
                        if (photo != null) {
                            this.photo = photo;
                            this.isPreviewingPhoto = true;
                            previewImageView.setVisibility(View.VISIBLE);
                            previewImageView.setImageBitmap(photo);
                        }
                    }
                    break;
                /**
                 * Called when UIMediaPreviewActivity is invoked to preview a video file taken with native camera app
                 */
                case FileConfig.MEDIA_TYPE_VIDEO:
                    if (extras.containsKey(FileConfig.CAPTURED_VIDEO_URI)) {
                        Uri videoUri = (Uri) extras.getParcelable(FileConfig.CAPTURED_VIDEO_URI);
                        if (videoUri != null) {
                            this.videoUri = videoUri;
                            this.isPreviewingPhoto = false;
                            previewVideoView.setVisibility(View.VISIBLE);
                            previewVideoView.setVideoURI(videoUri);
                            previewVideoView.start();
                        }
                    }
                    break;
            }
        } else if (extras != null && extras.containsKey(FileConfig.SELECTED_IMAGE_DATA)) {
            /**
             * Called when UIMediaPreviewActivity is started after user selects image file with the ImageFileBrowser
             */
            Bitmap photo = extras.getParcelable(FileConfig.SELECTED_IMAGE_DATA);
            if (photo != null) {
                this.photo = photo;
                this.isPreviewingPhoto = true;
                previewImageView.setVisibility(View.VISIBLE);
                previewImageView.setImageBitmap(photo);
            }
        } else if (extras != null && extras.containsKey(FileConfig.SELECTED_VIDEO_URL)) {
            /**
             * Called when UIMediaPreviewActivity is started after user selects image file with VideoFileBrowser
             */
            Uri videoUri =  extras.getParcelable(FileConfig.SELECTED_VIDEO_URL);
            if (videoUri != null) {
                this.videoUri = videoUri;
                this.isPreviewingPhoto = false;
                previewVideoView.setVisibility(View.VISIBLE);
                previewVideoView.setVideoURI(videoUri);
                previewVideoView.start();
            }
        }

    }

    /**
     * Called when accept button is clicked
     */
    @OnClick(R.id.btn_accept)
    public void acceptMedia() {
        if (isPreviewingPhoto) {
            /**
             * Call StatusActivity here and pass photo to it via intent
             * Photo can be passed as :
             * 1 : A base64 encoded string by calling ImageUitls.encodeToBase64(photo)
             * 2 : A byte array by calling ImageUtils.toByteArray(photo)
             */
        } else {
            /**
             * Call StatusActivity here and pass videoUri to it via intent
             */
        }
    }

    /**
     * Controls playback of video
     */
    @OnClick(R.id.vv_video_preview)
    public void toggleVideoPlayback() {
        if (previewVideoView.isPlaying()) {
            previewVideoView.pause();
        } else {
            previewVideoView.start();
        }
    }

    /**
     * Closes preview activity when reject button is clicked
     */
    @OnClick(R.id.btn_reject)
    public void rejectMedia() {
        this.finish();
    }
}
