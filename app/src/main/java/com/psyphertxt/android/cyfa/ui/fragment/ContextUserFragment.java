package com.psyphertxt.android.cyfa.ui.fragment;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.Fab;
import com.psyphertxt.android.cyfa.ui.adapters.ContextUserAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.viewholder.FabViewHolder;
import com.psyphertxt.android.cyfa.util.ContactUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContextUserFragment extends Fragment {

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.loader_relative_layout)
    RelativeLayout loaderLayout;

    private ContextUserAdapter adapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.context_user_fragment, container, false);

        ButterKnife.inject(this, view);

        context = view.getContext();

        setupAdapter();

        // FabViewHolder.defaultFabButtons(adapter);

        //Evans
        ContextUser evans = new ContextUser();
        evans.setProfileName("Evans");
        evans.setUserId("SY479Ak6ZK");
        Profile evansProfile = new Profile();
        evansProfile.setPlayerId("b0207b89-f9a1-45b3-b2e7-b5e37bc2ad8a");
        evans.setProfile(evansProfile);
        adapter.listManager.addItem(evans);

        //Codebender
        ContextUser youssouf = new ContextUser();
        youssouf.setProfileName("Codebendr");
        youssouf.setUserId("SfGEM50jJ7");
        Profile yousoufProfile = new Profile();
        yousoufProfile.setPlayerId("b0207b89-f9a1-45b3-b2e7-b5e37bc2ad8a");
        youssouf.setProfile(yousoufProfile);
        adapter.listManager.addItem(youssouf);

        //Codebender
        ContextUser teonit = new ContextUser();
        teonit.setProfileName("Teonit");
        teonit.setUserId("o1cR9BMrhY");
        Profile teonitProfile = new Profile();
        teonitProfile.setPlayerId("b3a59dfa-d428-468e-bc73-2ca9050fd346");
        teonit.setProfile(teonitProfile);
        adapter.listManager.addItem(teonit);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getContext();
    }

    private void setupAdapter() {
        adapter = new ContextUserAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }


}