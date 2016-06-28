package com.psyphertxt.android.cyfa.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.Walkthrough;
import com.psyphertxt.android.cyfa.model.Walkthrough.Stage;
import com.psyphertxt.android.cyfa.ui.adapters.StatusAdapter;
import com.psyphertxt.android.cyfa.ui.commands.CommandListener;
import com.psyphertxt.android.cyfa.ui.commands.ProfilePictureCommand;
import com.psyphertxt.android.cyfa.ui.commands.StatusUpdateCommand;
import com.psyphertxt.android.cyfa.ui.fragment.StatusFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * View holder for walkthrough messages
 */


public class WalkthroughViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @InjectView(android.R.id.title)
    TextView txtTitle;

    @InjectView(R.id.subtitle)
    TextView txtSubtitle;

    @InjectView(android.R.id.summary)
    TextView txtDescription;

    @InjectView(R.id.icon)
    ImageView imgIcon;

    private Walkthrough mWalkthrough;

    public WalkthroughViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void setWalkthrough(Walkthrough walkthrough) {
        mWalkthrough = walkthrough;
        txtTitle.setText(walkthrough.getTitle());
        txtSubtitle.setText(walkthrough.getSubtitle());
        txtDescription.setText(walkthrough.getDescription());
    }

    private StatusAdapter getAdapterView(View view) {

        // search for recycler view from root and remove view
        RecyclerView rv = ButterKnife.findById(view.getRootView(), R.id.recycler_view);
        //RecyclerView) view.getRootView().findViewById(R.id.recycler_view);
        StatusAdapter adapter = ((StatusAdapter) rv.getAdapter());
        rv.scrollToPosition(0);
        return adapter;
    }

    @Override
    public void onClick(final View v) {
        switch (mWalkthrough.getStage()) {
            case PROFILE_PICTURE:
                new ProfilePictureCommand().execute(v, new CommandListener() {
                    @Override
                    public void onFinished(final Context context) {

                        /* StatusAdapter adapter = getView(v);
                        adapter.addItem(StatusFragment.getWalkthrough(Stage.STATUS_MESSAGE));*/
                        // adapter.removeItem(WalkthroughViewHolder.this.getAdapterPosition());

                    }
                });
                break;

            case STATUS_MESSAGE:
                new StatusUpdateCommand().execute(v, new CommandListener() {
                    @Override
                    public void onFinished(Context context) {
                        getAdapterView(v).listManager.addFirst(StatusFragment.getWalkthrough(Stage.FIRST_MESSAGE));

                    }
                });
                break;
        }
    }
}































