package com.psyphertxt.android.cyfa.ui.fragment;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.ParseException;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.channel.StatusChannel;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.StatusFeed;
import com.psyphertxt.android.cyfa.model.Walkthrough;
import com.psyphertxt.android.cyfa.model.Walkthrough.Stage;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.adapters.StatusAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.StatusListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.util.Foreground;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class StatusFragment extends Fragment implements StatusListener.onStatusLoaded {

    private static final String TAG = StatusFragment.class.getSimpleName();

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerview;

    @InjectView(R.id.fab_add_new)
    FloatingActionButton fabAddNew;

    private static Map<Stage, Walkthrough> walkthroughMap = new HashMap<>();
    private StatusAdapter adapter;
    private Context context;
    String deviceUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.status_fragment, container, false);

        ButterKnife.inject(this, view);

        context = view.getContext();
        String deviceUserId = User.getDeviceUserId();

        initWalkthroughs();

        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BottomSheetLayout bottomSheet = ButterKnife.findById(v.getRootView(), R.id.bottomsheet);
                View view = LayoutInflater.from(context).inflate(R.layout.add_new_chat_view, bottomSheet, false);
                bottomSheet.showWithSheetView(view);

            }
        });

        //sets up adapter and recycler view
        setupAdapter(view);

        //check if user profile exist
        try {
            Profile profile = Profile.getProfileByUserId(deviceUserId, true).getFirst();
            if (profile != null) {
                EventBus.getDefault().postSticky(profile);
//                if (profile.getStatus() != null) {
//                    Profile.setStatusUI(getActivity(), profile.getStatus());
//                } else {
//                    setWalkthrough(getWalkthrough(Stage.STATUS_MESSAGE));
//                }
            }
        } catch (ParseException e) {
            //run first walkthrough
         //   setWalkthrough(getWalkthrough(Stage.STATUS_MESSAGE));
        }

        //  StatusChannel.removeAll();
        new StatusChannel.Builder()
                .init()
                .status(deviceUserId)
                .addStatusListener(this)
                        //.load()
                .loadAllStatusMessages(this)
                .connect();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getContext();
    }

    private void setWalkthrough(final Walkthrough walkthrough) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.listManager.addFirst(walkthrough);
                recyclerview.scrollToPosition(0);

            }
        });
    }

    private void setupAdapter(View view) {

        adapter = new StatusAdapter(getActivity());
        //  recyclerview.setItemAnimator(new SlideInDownAnimator());
        recyclerview.setItemAnimator(new LandingAnimator());
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerview.setHasFixedSize(true);
    }

    @Override
    public void populateView(final Map map) {
        final StatusFeed statusFeed = new StatusFeed(map);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                adapter.listManager.addFirst(statusFeed);
                recyclerview.scrollToPosition(0);
            }
        });
    }

    @Override
    public void updateView(final Map status, final int type) {

        final StatusFeed statusFeed = new StatusFeed(status);

        if (type == StatusChannel.UPDATE) {

            adapter.updateStatus(status, statusFeed);

        } else if (type == StatusChannel.RESET) {

            adapter.resetCount(statusFeed);
        }

        recyclerview.scrollToPosition(0);

        if (type == StatusChannel.UPDATE) {
            if (Foreground.get().isBackground()) {
                Alerts.notifications(
                        context,
                        ChatActivity.class,
                        statusFeed.getProfileName(),
                        statusFeed.getLastMessage(),
                        statusFeed.getUserId());
            }
        }
    }

    @Override
    public void loaded() {

        Collections.sort(adapter.listManager.getData(), new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                if (lhs instanceof StatusFeed &&
                        rhs instanceof StatusFeed) {
                    StatusFeed firstFeed = (StatusFeed) lhs;
                    StatusFeed secondFeed = (StatusFeed) rhs;
                    int value = 0;
                    if (new Date(firstFeed.getRawTimestamp()).before(new Date(secondFeed.getRawTimestamp()))) {
                        value = 1;
                    } else if (new Date(firstFeed.getRawTimestamp()).after(new Date(secondFeed.getRawTimestamp()))) {
                        value = -1;
                    }
                    return value;
                }
                return 0;

            }
        });

    }

    void initWalkthroughs() {

        Walkthrough walkthrough = new Walkthrough();
        walkthrough.setStage(Stage.PROFILE_PICTURE);
        walkthrough.setTitle(getResources().getString(R.string.profile_picture));
        walkthrough.setSubtitle(getResources().getString(R.string.profile_subtitle));
        walkthrough.setDescription(getResources().getString(R.string.profile_description));
        walkthroughMap.put(Stage.PROFILE_PICTURE, walkthrough);

        walkthrough = new Walkthrough();
        walkthrough.setStage(Stage.WELCOME);
        walkthrough.setTitle(getResources().getString(R.string.welcome_name));
        walkthrough.setSubtitle(getResources().getString(R.string.welcome_subtitle));
        walkthrough.setDescription(getResources().getString(R.string.welcome_description));
        walkthroughMap.put(Stage.WELCOME, walkthrough);

        walkthrough = new Walkthrough();
        walkthrough.setStage(Stage.STATUS_MESSAGE);
        walkthrough.setTitle(getResources().getString(R.string.status_name));
        walkthrough.setSubtitle(getResources().getString(R.string.status_subtitle));
        walkthrough.setDescription(getResources().getString(R.string.status_description));
        walkthroughMap.put(Stage.STATUS_MESSAGE, walkthrough);

        walkthrough = new Walkthrough();
        walkthrough.setStage(Stage.FIRST_MESSAGE);
        walkthrough.setTitle(getResources().getString(R.string.first_message_name));
        walkthrough.setSubtitle(getResources().getString(R.string.first_message_subtitle));
        walkthrough.setDescription(getResources().getString(R.string.first_message_description));
        walkthroughMap.put(Stage.FIRST_MESSAGE, walkthrough);

    }

    public static Walkthrough getWalkthrough(Stage stage) {
        return walkthroughMap.get(stage);
    }

}