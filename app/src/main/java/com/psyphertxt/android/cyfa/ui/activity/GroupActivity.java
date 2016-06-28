package com.psyphertxt.android.cyfa.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.model.Member;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.ui.adapters.ChatAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.widget.ChatActionBarActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Activity for displaying new features, communicating with users, taking actions like
 * accepting to receive a reward or to play a mini game.
 */

public class GroupActivity extends ChatActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.text_group_name)
    TextView txtGroupName;

    @InjectView(R.id.text_last_message_one)
    TextView txtLastMessageOne;

    @InjectView(R.id.text_last_title)
    TextView txtStatusName;

    @InjectView(R.id.status_view)
    LinearLayout mLinearLayout;

    @InjectView(R.id.text_group_members)
    TextView txtGroupMembers;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected ChatAdapter mAdapter;

    protected ChildEventListener mChildEventListener;

    protected MessageListener.onSendMessage mSendMessageListener;

    protected Firebase mFirebaseRef;
    protected ValueEventListener mConnectedListener;
    protected Firebase inbox;
    protected Firebase member;
    protected Firebase members;

    protected Boolean isLoaded = false;
    protected Member mMember;
    protected List<Member> mMemberList;
    protected String mGroupId;
    protected User mDeviceUser;
    protected String mDeviceUserId;
    protected String mGroupName;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // super.onCreate(savedInstanceState, R.layout.activity_group);

        //pull data from the previous activity
        final Bundle bundle = getIntent().getExtras();

        //set values from the previous activity
        mGroupId = bundle.getString(Config.KEY_GROUP_ID);
        mGroupName = bundle.getString(Config.KEY_GROUP_NAME);

        txtGroupName.setText(mGroupName);

        // Setup our Firebase
        mFirebaseRef = new Firebase(Config.getFirebaseURL());

        mMember = new Member();
        mMemberList = new ArrayList<>();

        //TODO move to a UI utils class or custom ActionBarActivity
        initUI();

        //checks to see if there is an active web socket connection
        setupAppConnection();

        //init this user as active in this group
        initMember();

        //get group members when they are added
        setUpGroupMembersListener();

        //get group messages when they are added
        setupGroupChatListener();

        //send message listener
        setSendMessageListener(new MessageListener.onSendMessage() {
            @Override
            public void sendMessage(String text) {

                sendChat(text);
            }
        });


    }

    protected void initUI() {

        mDeviceUser = User.getDeviceUser();
        mDeviceUserId = User.getDeviceUserId();

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_black_arrow);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //temporary remove device user from group
                member.removeValue();

                if (mMemberList.size() == Config.NUMBER_ONE) {
                    inbox.removeValue();
                }

                GroupActivity.this.onBackPressed();

            }
        });

        mAdapter = new ChatAdapter(this,mRecyclerView, context);
        mAdapter.setHasStableIds(true);

        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    public void initMember() {

        member = mFirebaseRef.child(Config.REF_GROUPS).child(mGroupId).child(Config.REF_CHILD_MEMBERS).child(mDeviceUserId);

        mMember = new Member(mDeviceUser.getProfileName(), mDeviceUserId);
        member.setValue(mMember);

    }

    protected void setUpGroupMembersListener() {

        members = mFirebaseRef.child(Config.REF_GROUPS).child(mGroupId).child(Config.REF_CHILD_MEMBERS);

        members.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // if (mIsLoaded) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.getValue() instanceof HashMap) {

                        Member member = new Member();
                        member.setMember(dataSnapshot.getValue(HashMap.class));

                        //TODO re-check logic for 2000 users or limit to 100
                        //TODO is member is just one = diplay only you here

                        mMemberList.add(member);
                        memberCount();

                    }
                }
                // }
            }

            //display all active members in this group
            protected void memberCount() {

                txtGroupMembers.setText(String.format("%s %s", mMemberList.size(), getString(R.string.members_text)));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.getValue() instanceof HashMap) {

                        Member member = new Member();
                        member.setMember(dataSnapshot.getValue(HashMap.class));

                        mMemberList.remove(member);
                        memberCount();

                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setupGroupChatListener() {

        inbox = mFirebaseRef.child(Config.REF_GROUPS).child(mGroupId).child(Config.REF_MESSAGES);

        mChildEventListener = inbox.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //  if (mIsLoaded) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.getValue() instanceof HashMap) {

                        Message message = new Message(dataSnapshot.getValue(HashMap.class));

                        if (!message.getUserId().equals(mDeviceUserId)) {

                            Chat chat = new Chat();
                            chat.setUserId(message.getUserId());
                            chat.setMessageId(dataSnapshot.getKey());

                            for (Member savedMember : mMemberList) {

                                if (savedMember.getUserId().contains(message.getUserId())) {

                                    chat.setName(savedMember.getName());

                                    if (savedMember.getUserId().equals(mDeviceUserId)) {

                                        chat.setDirection(Chat.Direction.OUTGOING);

                                    }

                                    chat.setDirection(Chat.Direction.INCOMING);
                                    break;

                                }
                            }

                            chat.setText(message.getText());
                            chat.setDeliveredAt(message.getDeliveredAt());
                            mAdapter.listManager.addItem(chat);

                        }


                    }
                }
                //  }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setupAppConnection() {

        //listen for connections to the server
        mConnectedListener = mFirebaseRef.child(Config.REF_CONNECTION_STATUS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {

                    //temporary remove device user from group
                    member.onDisconnect().removeValue();

                    if (mMemberList.size() == Config.NUMBER_ONE) {
                        inbox.onDisconnect().removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
    }

    protected void sendChat(String text) {

        //create a firebase push token
        Firebase chatId = inbox.push();

        //create a new chat message
        final Chat chat = new Chat();
        chat.setUserId(mDeviceUserId);
        chat.setMessageId(chatId.getKey());
        chat.setName(mDeviceUser.getProfileName());
        chat.setText(text);

        //create a message and set the values
        //needed for firebase storage
       // Message message = Message.fromChat(chat);
        //chatId.setValue(message);
        mAdapter.addChat(chat,false);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //temporary remove device user from group
            member.removeValue();

            if (mMemberList.size() == Config.NUMBER_ONE) {
                inbox.removeValue();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
