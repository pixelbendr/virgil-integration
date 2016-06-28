package com.psyphertxt.android.cyfa.ui.commands;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

public class StatusUpdateCommand implements Command {
    public void execute(final View view, final CommandListener listener) {

        new MaterialDialog.Builder(view.getContext())
                .title(R.string.status_name)
                .customView(R.layout.add_status_message_view, true)
                .positiveText(R.string.save_text)
                .negativeText(R.string.later_text)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(final MaterialDialog dialog) {

                        TextView txtDialogStatus = (TextView) dialog.findViewById(R.id.text_status_message);

                        Alerts.progress((Activity) view.getContext(), false, "Updating Status Message");

                        Profile.updateStatusMessage(txtDialogStatus.getText().toString(),
                                new CallbackListener.callbackForResults() {

                                    @Override
                                    public void success(Object result) {
                                        Alerts.progress((Activity) view.getContext(), true);
                                        TextView txtStatus = (TextView) view.getRootView().findViewById(android.R.id.summary);
                                        txtStatus.setText((String) result);
                                        txtStatus.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void error(String error) {
                                        Alerts.progress((Activity) view.getContext(), true);
                                        Alerts.show(view.getContext(), "Error Saving Status", "couldn't save status try again later");

                                    }
                                });
                        //listener.onFinished(view.getContext());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build().show();

    }
}
