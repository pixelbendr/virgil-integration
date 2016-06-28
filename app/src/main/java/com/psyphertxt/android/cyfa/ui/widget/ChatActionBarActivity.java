package com.psyphertxt.android.cyfa.ui.widget;

import com.cocosw.bottomsheet.BottomSheet;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.FileConfig;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.channel.UserChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.model.ContentType;
import com.psyphertxt.android.cyfa.ui.activity.FileBrowserActivity;
import com.psyphertxt.android.cyfa.ui.activity.MediaPreviewActivity;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.TypingIndicatorListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

/**
 * this activity is responsible for initializing similar view
 * elements for views that have chat functionality, also manages
 * validation etc.
 */
public class ChatActionBarActivity extends MainActionBarActivity {

    @InjectView(R.id.text_message)
    EditText txtMessage;

    @InjectView(R.id.fab_send_message)
    FloatingActionButton fabSendMessage;

    @InjectView(R.id.fab_compose)
    FloatingActionButton fabCompose;

    @InjectView(R.id.fab_attachment)
    FloatingActionButton fabAttachment;

    @InjectView(R.id.fab_more)
    FloatingActionButton fabMore;

    @InjectView(R.id.fab_attachment_toggle)
    FloatingActionButton fabAttachmentToggle;

    @InjectView(R.id.fab_emoji)
    FloatingActionButton fabEmoji;

    @InjectView(R.id.fab_active_emoji)
    FloatingActionButton fabEmojiActive;

    @InjectView(R.id.send_message_layout)
    LinearLayout layoutSendMessage;

    @InjectView(R.id.easy_access_layout)
    LinearLayout layoutSendOption;

    private MessageListener.onSendMessage onSendMessageListener;
    private MessageListener.onMediaTypeSelection onMediaTypeSelectionListener;
    private Boolean isOpened = false;
    private final Handler handler = new Handler();
    private Context context;
    private EmojiconsPopup popup;

    private static final int TEXT_LENGTH = 3;
    private static int IDLE_SEC = 4000; // idle seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.inject(this);
        context = this;


        showKeyboard(txtMessage, new CallbackListener.completion() {
            @Override
            public void done() {

            }
        });

        fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = txtMessage.getText().toString();

                if (!text.isEmpty()) {

               /*     TestUtils.messageTest(999, new TestUtils.TestListener() {
                        @Override
                        public void run(Object result) {
                            getMessageListener().sendMessage((String) result);
                        }
                    });*/

                    if (getSendMessageListener() != null) {
                        getSendMessageListener().sendMessage(text);
                        txtMessage.setText(Config.EMPTY_STRING);
                    }

                }
            }
        });

        mediaSelection(fabAttachment);
        mediaSelection(fabAttachmentToggle);

        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BottomSheet.Builder(ChatActionBarActivity.this, R.style.BottomSheet_StyleDialog)
                        .title(R.string.more_options_text)
                        .sheet(R.menu.profile_more_menu)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).show();

            }
        });
    }

    private void mediaSelection(FloatingActionButton floatingActionButton) {

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new BottomSheet.Builder(ChatActionBarActivity.this, R.style.BottomSheet_StyleDialog)
                        .title(R.string.chat_media_subtitle)
                        .sheet(R.menu.add_media_menu)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case R.id.gallery:
                                       /* Intent galleryIntent = new Intent(ChatActionBarActivity.this, FileBrowserActivity.class);
                                        galleryIntent.putExtra(FileConfig.FILE_BROWSER_ROOT_DIRECTORY, FileConfig.PHOTOS_ROOT_DIRECTORY);
                                        startActivity(galleryIntent);*/
                                        getMediaTypeSelectionListener().mediaType(ContentType.IMAGE);
                                        break;
                                    case R.id.camera:
                                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                            startActivityForResult(cameraIntent, FileConfig.IMAGE_CAPTURE_ACTIVITY_REQUEST_CODE);
                                        }
                                        // getMediaTypeSelectionListener().mediaType(MediaType.IMAGE);
                                        break;
                                    case R.id.video:
                                        Intent galleryIntent = new Intent(ChatActionBarActivity.this, FileBrowserActivity.class);
                                        galleryIntent.putExtra(FileConfig.FILE_BROWSER_ROOT_DIRECTORY, FileConfig.PHOTOS_ROOT_DIRECTORY);
                                        startActivity(galleryIntent);
                                       /* Intent intent = new Intent(ChatActionBarActivity.this, FileBrowserActivity.class);
                                        intent.putExtra(FileConfig.FILE_BROWSER_ROOT_DIRECTORY, FileConfig.VIDEOS_ROOT_DIRECTORY);
                                        startActivity(intent);*/
                                        //  getMediaTypeSelectionListener().mediaType(MediaType.VIDEO);
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    /**
     * Called when the user is done selecting either an image or photo using the systems
     * photo/video
     * browser activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent previewIntent = new Intent(ChatActionBarActivity.this, MediaPreviewActivity.class);
            switch (requestCode) {
                /**
                 * Called when user successfully takes a photo with the native camera app
                 * Passes both MEDIA_TYPE_ID and CAPTURED_PHOTO_DATA to UIMediaPreviewActivity
                 */
                case FileConfig.IMAGE_CAPTURE_ACTIVITY_REQUEST_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap photo = (Bitmap) extras.get("data");

                    previewIntent.putExtra(FileConfig.MEDIA_TYPE_ID, FileConfig.MEDIA_TYPE_PHOTO);
                    previewIntent.putExtra(FileConfig.CAPTURED_PHOTO_DATA, photo);
                    break;
                /**
                 * Called when user successfully takes a video with the native camera app
                 * Passes both MEDIA_TYPE_ID and CAPTURED_VIDEO_URI to UIMediaPreviewActivity
                 */
                case FileConfig.VIDEO_CAPTURE_ACTIVITY_REQUEST_CODE:
                    Uri videoUri = data.getData();

                    previewIntent.putExtra(FileConfig.MEDIA_TYPE_ID, FileConfig.MEDIA_TYPE_VIDEO);
                    previewIntent.putExtra(FileConfig.CAPTURED_VIDEO_URI, videoUri);
                    break;
            }

            /**
             * Starts UIMediaPreviewActivity
             */
            startActivity(previewIntent);
        }
    }

    public void showKeyboard(View textView, final CallbackListener.completion completion) {

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMessageView();

                completion.done();

            }
        });

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final View rootView = findViewById(R.id.bottomsheet);
        popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //Set on emojicon click listener
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                txtMessage.append(emojicon.getEmoji());
            }
        });

        //Set on backspace click listener
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                txtMessage.dispatchKeyEvent(event);
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                fabEmojiActive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_emoji));
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                txtMessage.append(emojicon.getEmoji());
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                txtMessage.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        fabEmojiActive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EmojiKeyboard();
            }
        });

        fabEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EmojiKeyboard();

                layoutSendOption.setVisibility(View.GONE);
                layoutSendMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showMessageView() {

        txtMessage.requestFocus();

        InputMethodManager keyboard = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(txtMessage, 0);

        //hide and show views
        layoutSendOption.setVisibility(View.GONE);
        layoutSendMessage.setVisibility(View.VISIBLE);
    }

    private void EmojiKeyboard() {

        //If popup is not showing => emoji keyboard is not visible, we need to show it
        if (!popup.isShowing()) {

            //If keyboard is visible, simply show the emoji popup
            if (popup.isKeyBoardOpen()) {
                popup.showAtBottom();

                fabEmojiActive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard));

            } else {

                // open the text keyboard first and immediately after that show the emoji popup
                txtMessage.setFocusableInTouchMode(true);
                txtMessage.requestFocus();
                popup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(txtMessage, InputMethodManager.SHOW_IMPLICIT);
                fabEmojiActive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard));

            }
        }

        //If popup is showing, simply dismiss it to show the undelying text keyboard
        else {
            fabEmojiActive.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_emoji));
            popup.dismiss();
        }
    }

    public void setSendMessageListener(MessageListener.onSendMessage messageListener) {
        onSendMessageListener = messageListener;
    }

    public MessageListener.onSendMessage getSendMessageListener() {
        return onSendMessageListener;
    }

    public void setMediaTypeSelectionListener(MessageListener.onMediaTypeSelection mediaTypeSelectionListener) {
        onMediaTypeSelectionListener = mediaTypeSelectionListener;
    }

    public MessageListener.onMediaTypeSelection getMediaTypeSelectionListener() {
        return onMediaTypeSelectionListener;
    }

    //TODO expand this to allow group type indicators
    public void enableTypingIndicator(final UserChannel userChannel) {

        if (userChannel.isLiveTyping()) {
            IDLE_SEC = 10000;
        } else {
            IDLE_SEC = 4000;
        }

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if (s.length() >= TEXT_LENGTH) {

                    //TODO use timers for efficiency
                    if (userChannel.isLiveTyping() && !Message.IS_PRIVATE) {
                        userChannel.setTypingIndicator(s.toString());
                    } else if (!Message.IS_PRIVATE) {
                        userChannel.setTypingIndicator(TypingIndicatorListener.STARTED);
                    }

                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            userChannel.setTypingIndicator(TypingIndicatorListener.FINISHED);
                        }
                    }, IDLE_SEC);

                } else {

                    userChannel.setTypingIndicator(TypingIndicatorListener.FINISHED);
                    handler.removeCallbacksAndMessages(null);

                }
            }
        });
    }

    public FloatingActionButton getComposeMessageFabButton() {
        return fabCompose;
    }

    public void setListenerToRootView(final CallbackListener.completion completion) {

        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > Config.ONE_HUNDRED) {

                 /*   if (!isOpened) {

                    }*/
                    isOpened = true;
                } else if (isOpened) {

                    if (txtMessage.getText().toString().isEmpty()) {

                        layoutSendOption.setVisibility(View.VISIBLE);
                        layoutSendMessage.setVisibility(View.GONE);
                        completion.done();

                    }

                    isOpened = false;
                }
            }
        });
    }
}
