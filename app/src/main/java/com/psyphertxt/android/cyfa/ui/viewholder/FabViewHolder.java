package com.psyphertxt.android.cyfa.ui.viewholder;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.Fab;
import com.psyphertxt.android.cyfa.ui.adapters.ContextUserAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.psyphertxt.android.cyfa.util.ContactUtils.findContactsAsync;

public class FabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @InjectView(R.id.fab)
    FloatingActionButton fabButton;

    @InjectView(R.id.text_name)
    TextView txtName;

    private Context context;
    private Fab fab;
    private ContextUserAdapter contextUserAdapter;
    private Boolean isContacts = false;

    public void setAdapterView(ContextUserAdapter contextUserAdapter) {
        this.contextUserAdapter = contextUserAdapter;
    }

    private ContextUserAdapter getAdapterView() {
        return this.contextUserAdapter;
    }

    public FabViewHolder(View itemView) {

        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
        fabButton.setOnClickListener(this);
        this.context = itemView.getContext();

    }

    public Fab getFab() {
        return this.fab;
    }

    public void setFab(Fab fab) {

        this.fab = fab;
        txtName.setText(fab.getName());

        if (fab.getResId() != -1) {
            fabButton.setImageResource(fab.getResId());
        }
    }

    @Override
    public void onClick(final View v) {

        switch (getFab().getButtonType()) {
            case Fab.ButtonType.ADD_FRIENDS:
                getContacts(v);
                break;
        }
    }

    private void getContacts(final View v) {

        getAdapterView().listManager.clearItems();
        getAdapterView().notifyDataSetChanged();
        final View loaderLayout = v.getRootView().findViewById(R.id.loader_relative_layout);
        loaderLayout.setVisibility(View.VISIBLE);

        Settings settings = new Settings(context);
        if (!settings.getCallingCode().equals(Config.EMPTY_STRING)) {
            findContactsAsync(context, settings.getCallingCode(), new CallbackListener.callbackForResults() {
                @Override
                public void success(Object object) {

                    loaderLayout.setVisibility(View.GONE);

                    final HashMap<String, Object> result = (HashMap<String, Object>) object;
                    ContextUser.removeAll(new CallbackListener.callback() {
                        @Override
                        public void success() {
                            List<Object> contextUserList = (List<Object>) (List<?>) ContextUser.create(result);
                            if (contextUserList.size() != 0) {
                                defaultFabButtons(getAdapterView());
                                getAdapterView().listManager.refillManually(contextUserList);
                                // getAdapterView().addContextUsers(contextUserList);

                            } else {
                                Alerts.show(context, context.getString(R.string.unknown_error_title), context.getString(R.string.unknown_error_message));
                            }
                        }

                        @Override
                        public void error(String error) {
                            Alerts.show(context, context.getString(R.string.unknown_error_title), context.getString(R.string.unknown_error_message));
                        }
                    });

                }

                @Override
                public void error(String error) {
                    Alerts.show(context, context.getString(R.string.unknown_error_title), context.getString(R.string.unknown_error_message));
                }
            });
        } else {
            Alerts.show(context, context.getString(R.string.unknown_error_title), context.getString(R.string.unknown_error_message));
        }
    }

    public static void defaultFabButtons(ContextUserAdapter adapter) {

        //add friends button
        Fab fab = new Fab();
        fab.setButtonType(Fab.ButtonType.ADD_FRIENDS);
        fab.setName("Add Friends");

        Fab fabGroups = new Fab();
        fabGroups.setResId(R.drawable.ic_contact_groups);
        fabGroups.setButtonType(Fab.ButtonType.ADD_GROUPS);
        fabGroups.setName("Groups");

        Fab fabRecent = new Fab();
        fabRecent.setResId(R.drawable.ic_contacts_recent);
        fabRecent.setButtonType(Fab.ButtonType.RECENT);
        fabRecent.setName("Recent");

        adapter.listManager.addItem(fab);
        adapter.listManager.addItem(fabGroups);
        adapter.listManager.addItem(fabRecent);
    }
}