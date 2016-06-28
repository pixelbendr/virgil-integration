package com.psyphertxt.android.cyfa.ui.widget;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.Config.NotificationType;
import com.psyphertxt.android.cyfa.MainApplication;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.Themes;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.activity.StatusActivity;
import com.psyphertxt.android.cyfa.ui.activity.startup.PhoneNumberActivity;
import com.psyphertxt.android.cyfa.ui.activity.startup.StartUpActivity;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.util.Testable;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * This is a convenient class which handles Alert Dialogs, Modal Popups, Toast Notifications,
 * Progress bars
 * and anything that needs a user's action temporarily or needs to freeze the UI temporarily to
 * connect to
 * a network. Also good for testing UI functionality
 */

public class Alerts {

    public static MaterialDialog progressDialog;

    public static void show(Context context, String title, String message, String buttonText) {
        new MaterialDialog.Builder(context)
                .titleColor(Themes.getStoredColor(context))
                .title(title)
                .content(message)
                .positiveText(buttonText)
                .show();
    }

    public static void show(Context context, String title, String message, final CallbackListener.completion completion) {
        new MaterialDialog.Builder(context)
                .titleColor(Themes.getStoredColor(context))
                .title(title)
                .content(message)
                .positiveText(context.getString(android.R.string.ok))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        completion.done();
                    }
                })
                .show();
    }

    @NonNull
    public static BottomSheetLayout getBottomSheetLayout(View v, Context context, ContextUser contextUser) {

        Rect rect = new Rect();
        Activity activity = (Activity) context;
        Window win = activity.getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusHeight = rect.top;
        //  int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //  int titleHeight = contentViewTop - statusHeight;

        final BottomSheetLayout bottomSheet = ButterKnife.findById(v, R.id.bottomsheet);
        bottomSheet.setPeekSheetTranslation(v.getHeight() - statusHeight);
        View view = LayoutInflater.from(context).inflate(R.layout.profile_view, bottomSheet, false);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bottomSheet.showWithSheetView(view);

        if (contextUser != null) {

            ((TextView) ButterKnife.findById(bottomSheet, R.id.text_username)).setText(contextUser.getProfileName());
            ButterKnife.findById(bottomSheet, R.id.profile_close_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheet.dismissSheet();
                        }
                    });


            Shape.background(ButterKnife.findById(bottomSheet, R.id.block_frame_layout), Shape.oval(Shape.getPrimaryColor(context)));
            Shape.background(ButterKnife.findById(bottomSheet, R.id.groups_frame_layout), Shape.oval(Shape.getPrimaryColor(context)));
            Shape.background(ButterKnife.findById(bottomSheet, R.id.call_frame_layout), Shape.oval(Shape.getPrimaryColor(context)));
            Shape.background(ButterKnife.findById(bottomSheet, R.id.block_user_frame_layout), Shape.oval(Shape.getPrimaryColor(context)));
            Shape.background(ButterKnife.findById(bottomSheet, R.id.groups_user_frame_layout), Shape.oval(Shape.getPrimaryColor(context)));
            Shape.background(ButterKnife.findById(bottomSheet, R.id.private_frame_layout), Shape.oval(Color.BLACK));

            if (contextUser.getProfile() != null) {
                if (contextUser.getProfile().getStatus() != null) {
                    ((TextView) ButterKnife.findById(bottomSheet, R.id.text_status_message)).setText(contextUser.getProfile().getStatus());
                }
            }
        }
        return bottomSheet;
    }

    public static void show(Context context, String title, String message) {
        show(context, title, message, context.getString(android.R.string.ok));
    }

    public static void passcode(final Context context, final CallbackListener.completion completion) {
        new MaterialDialog.Builder(context)
                // .titleColor(Themes.getStoredColor(context))
                .title(R.string.passcode_message_title)
                .customView(R.layout.add_passcode_message_view, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(final MaterialDialog dialog) {

                        final TextView txtPasscode = ButterKnife.findById(dialog, R.id.text_pass_code);
                        final TextInputLayout txtPasscodeInputLayout = ButterKnife.findById(dialog, R.id.text_pass_code_layout);
                        txtPasscodeInputLayout.setHint(context.getString(R.string.passcode_text));

                        Message.IS_PRIVATE = false;

                        Settings settings = new Settings(context);

                        if (txtPasscode.length() != Config.ZERO_LENGTH &&
                                txtPasscode.length() == Config.DISPLAY_NAME_MIN_LENGTH) {

                            if (txtPasscode.getText().toString().equals(settings.getPasscode())) {

                                Config.IS_PASSCODE = true;
                                completion.done();
                                dialog.dismiss();

                            } else {

                                txtPasscodeInputLayout.setError("Incorrect Passcode Please Try Again");

                            }

                        } else {

                            txtPasscodeInputLayout.setError("Passcode must be 4 characters");

                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                })
                .autoDismiss(false)
                .build()
                .show();
    }

    public static void choosePasscode(final Context context, final CallbackListener.completion completion) {

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                // .titleColor(Themes.getStoredColor(context))
                .title(R.string.choose_passcode_message_title)
                .customView(R.layout.choose_passcode_message_view, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(final MaterialDialog dialog) {

                        final TextView txtPasscode = ButterKnife.findById(dialog, R.id.text_passcode);
                        final TextInputLayout txtPasscodeInputLayout = ButterKnife.findById(dialog, R.id.text_passcode_layout);
                        txtPasscodeInputLayout.setHint(context.getString(R.string.passcode_text));

                        final TextView txtPasscodeRetype = ButterKnife.findById(dialog, R.id.text_retype_passcode);
                        final TextInputLayout txtPasscodeRetypeInputLayout = ButterKnife.findById(dialog, R.id.text_retype_passcode_layout);
                        txtPasscodeRetypeInputLayout.setHint(context.getString(R.string.retype_passcode_text));

                        Message.IS_PRIVATE = false;

                        //check the length of the first field
                        if (txtPasscode.length() != Config.ZERO_LENGTH &&
                                txtPasscode.length() == Config.DISPLAY_NAME_MIN_LENGTH) {

                            //check the length of the second field
                            if (txtPasscodeRetype.length() != Config.ZERO_LENGTH &&
                                    txtPasscodeRetype.length() == Config.DISPLAY_NAME_MIN_LENGTH) {

                                //check if characters are the same
                                if (txtPasscodeRetype.getText().toString().equals(txtPasscode.getText().toString())) {

                                    Config.IS_SAVED_PASSCODE = true;
                                    Config.IS_PASSCODE = true;
                                    Settings settings = new Settings(context);
                                    settings.setPasscode(txtPasscode.getText().toString());
                                    completion.done();
                                    dialog.dismiss();

                                } else {

                                    txtPasscodeRetypeInputLayout.setError("Passcode Doesn't Match, Please Check and Try Again");

                                }

                            } else {

                                txtPasscodeRetypeInputLayout.setError("Passcode must be 4 characters");

                            }

                        } else {

                            txtPasscodeInputLayout.setError("Passcode must be 4 characters");

                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                })
                .autoDismiss(false)
                .build();

        dialog.show();
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public static void progress(Context context, String title, String message) {
        Settings settings = new Settings(context);
        int theme = Themes.getThemeByColor(settings.getTheme()).get(Config.KEY_COLOR_PRIMARY);
        progressDialog = new MaterialDialog.Builder(context)
                .title(title)
                .titleColor(ContextCompat.getColor(context, theme))
                .content(message)
                .progress(true, 0).build();
        progressDialog.show();
    }

    public static void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void progress(Context context) {
        progress(context, context.getString(R.string.text_loading), context.getString(R.string.please_wait));
    }

    public static void toast(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();

    }

    public static void snackbar(Activity activity, String title) {
        Snackbar.make(activity.getWindow().getDecorView().getRootView(), title, Snackbar.LENGTH_LONG).show();
    }

    public static void progress(Activity activity, Boolean isConnected, String message) {
        View view = activity.getWindow().getDecorView();
        TextView txtConnectionStatus = (TextView) view.findViewById(R.id.text_connection_status);
        ProgressWheel progressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
        if (isConnected) {
            txtConnectionStatus.setVisibility(View.GONE);
            txtConnectionStatus.setText(Config.EMPTY_STRING);
            progressWheel.setVisibility(View.GONE);
        } else {
            txtConnectionStatus.setVisibility(View.VISIBLE);
            txtConnectionStatus.setText(message);
            progressWheel.setVisibility(View.VISIBLE);
        }
        //  TestUtils.spec("connection status","display connection status text",txtConnectionStatus.getText().toString());
    }

    public static void progress(Activity activity, Boolean isConnected) {
        progress(activity, isConnected, activity.getString(R.string.text_connection_status));
    }

    public static void localNotification(Context context, Class<?> cls, String title, String message, String userId) {

        //TODO temporary fix
        //investigate and update
//        Activity activity = (Activity) context;
//        if (activity instanceof ChatActivity) {
//            activity.finish();
//        }

        Intent notificationIntent = new Intent(context, StatusActivity.class);
//        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        notificationIntent.putExtra(Config.KEY_NOTIFICATION, NotificationType.NOTIFICATION);
        //notificationIntent.putExtra(Config.KEY_USER_ID, userId);
        EventBus.getDefault().postSticky(userId);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pIntent = PendingIntent.getActivity(context, NotificationType.NOTIFICATION, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent = PendingIntent.getActivity(context, NotificationType.NOTIFICATION, notificationIntent, 0);

        /*Notification notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();*/

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ico_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // hide the notification after its selected
        //notification.flags |=  Notification.FLAG_AUTO_CANCEL;
        //  notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR | Notification.FLAG_AUTO_CANCEL;

//        notificationManager.notify(NotificationType.NOTIFICATION, notification.build());
        notificationManager.notify(0, notification.build());

    }

    public static void notifications(Context context, Class<?> cls, String title, String message, String userId) {

        Intent intent = new Intent(context, StatusActivity.class);
        intent.putExtra(Config.KEY_NOTIFICATION, NotificationType.NOTIFICATION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, NotificationType.NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ico_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        EventBus.getDefault().postSticky(userId);
        MainApplication.IS_NOTIFICATION = true;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification.build());

    }
}
