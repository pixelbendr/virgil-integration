package com.psyphertxt.android.cyfa.ui.viewholder;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.Config.NotificationType;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * View holder for reading phone contacts
 */

public class ContextUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @InjectView(R.id.text_name)
    TextView txtName;

    @InjectView(R.id.icon_user)
    ImageView imgUser;

    protected ContextUser contextUser;
    protected Context context;

    public ContextUserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
        this.context = itemView.getContext();
    }

    public ContextUser getContextUser() {
        return contextUser;
    }

    public void setContextUser(ContextUser contextUser) {
        this.contextUser = contextUser;
        txtName.setText(contextUser.getProfileName());
//        Picasso.with(context).load(contextUser.getImage()).into(imgUser);
    }

    @Override
    public void onClick(final View v) {

        final BottomSheetLayout bottomSheet = Alerts.getBottomSheetLayout(v.getRootView(), context, contextUser);

        FloatingActionButton fabSendMessage = ButterKnife.findById(bottomSheet, R.id.fab_send_message);
        fabSendMessage.setVisibility(View.VISIBLE);
        fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().postSticky(contextUser);

                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra(Config.KEY_NOTIFICATION, NotificationType.TABBED);
                v.getContext().startActivity(intent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        bottomSheet.dismissSheet();

                    }
                }, 1000);

            }
        });
    }
}