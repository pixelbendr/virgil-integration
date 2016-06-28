package com.psyphertxt.android.cyfa.ui.viewholder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.Config.NotificationType;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.StatusFeed;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * View holder for status messages
 */

public class StatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RippleView.OnRippleCompleteListener {

    @InjectView(android.R.id.title)
    TextView txtTitle;

    @InjectView(android.R.id.summary)
    TextView txtMessage;

    @InjectView(R.id.message_count)
    TextView messageCount;

    @InjectView(R.id.timestamp)
    TextView txtTimeStamp;

    @InjectView(R.id.icon_media)
    ImageView mediaIcon;

    @InjectView(R.id.icon_user)
    ImageView userIcon;

    @InjectView(R.id.icon_message)
    ImageView messageIcon;

    @InjectView(R.id.rippleView)
    RippleView rippleView;

    private StatusFeed statusFeed;

    public StatusViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        //rippleView.setOnClickListener(this);
        rippleView.setOnRippleCompleteListener(this);
    }

    public StatusFeed getStatus() {
        return statusFeed;
    }

    public void setStatus(StatusFeed statusFeed) {

        this.statusFeed = statusFeed;

        txtTitle.setText(statusFeed.getProfileName());
        txtMessage.setText(statusFeed.getLastMessage());

        if (statusFeed.getTimestamp() != null) {
            txtTimeStamp.setText(statusFeed.getTimestamp().toLowerCase());
        }

        if (statusFeed.getCount() == Config.ZERO_LENGTH) {
            messageCount.setText(Config.EMPTY_STRING);
        } else {
            messageCount.setText(statusFeed.getCount() + Config.EMPTY_STRING);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onComplete(RippleView rippleView) {

        ContextUser contextUser = ContextUser.findOne(statusFeed.getUserId());

        if (contextUser != null) {

            EventBus.getDefault().postSticky(contextUser);
            Intent intent = new Intent(rippleView.getContext(), ChatActivity.class);
            intent.putExtra(Config.KEY_NOTIFICATION, NotificationType.TABBED);
            rippleView.getContext().startActivity(intent);

        }else {
            Alerts.show(rippleView.getContext(),"Not Found","This user is not in your contacts. Pending issue, should be addressed in the next update.");
        }
    }
}