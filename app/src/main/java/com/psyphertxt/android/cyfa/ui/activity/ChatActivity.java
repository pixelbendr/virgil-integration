package com.psyphertxt.android.cyfa.ui.activity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.Config.NotificationType;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.channel.MessageChannel;
import com.psyphertxt.android.cyfa.backend.firebase.channel.PresenceChannel;
import com.psyphertxt.android.cyfa.backend.firebase.channel.StatusChannel;
import com.psyphertxt.android.cyfa.backend.firebase.channel.UserChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Media;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.backend.onesignal.Notifications;
import com.psyphertxt.android.cyfa.backend.parse.People;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.model.ContentType;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.DeliveryStatus;
import com.psyphertxt.android.cyfa.ui.adapters.ChatAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.PresenceListener;
import com.psyphertxt.android.cyfa.ui.listeners.StatusListener;
import com.psyphertxt.android.cyfa.ui.listeners.TypingIndicatorListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.ui.widget.ChatActionBarActivity;
import com.psyphertxt.android.cyfa.ui.widget.Shape;
import com.psyphertxt.android.cyfa.util.Foreground;
import com.psyphertxt.android.cyfa.util.ResizeAnimation;
import com.psyphertxt.android.cyfa.util.SecurityUtils;
import com.psyphertxt.android.cyfa.util.Testable;
import com.psyphertxt.android.cyfa.util.TimeUtils;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.exceptions.ServiceException;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.Base64;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ChatActivity extends ChatActionBarActivity implements PresenceListener.OnConnection,
        PresenceListener.onConnectionChange, StatusListener.onStatusLoaded, MessageListener.onMessageLoaded,
        MessageListener.onChatMessage, TypingIndicatorListener.onTypingIndicator, Foreground.Listener,
        MessageListener.onMessageUpdate {

    //TODO move all heavy UI logic to chat action bar activity
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.text_profile_name)
    TextView txtProfileName;

    @InjectView(R.id.text_last_message_one)
    TextView txtLastMessageOne;

    @InjectView(R.id.text_last_title)
    TextView txtStatusName;

    @InjectView(R.id.status_view)
    LinearLayout linearLayout;

    @InjectView(R.id.text_connection_status)
    TextView txtConnectionStatus;

    @InjectView(R.id.frame_layout)
    FrameLayout layoutFrame;

    @InjectView(R.id.fab_back_navi)
    FloatingActionButton fabBackNavi;

    @InjectView(R.id.toolbar_relative_layout)
    RelativeLayout layoutRelativeToolbar;

    @InjectView(R.id.send_message_view)
    RelativeLayout sendMessageView;

    @InjectView(R.id.loader_relative_layout)
    RelativeLayout loaderLayout;

    @InjectView(R.id.profile_image)
    CircularImageView profileImage;

    @InjectView(R.id.private_mode_image)
    ImageView imagePrivateMode;

    //live type UI
    @InjectView(R.id.chat_view_advance)
    FrameLayout liveTypingFrameLayout;

    @InjectView(android.R.id.title)
    TextView txtLiveTypingTitle;

    @InjectView(android.R.id.summary)
    TextView txtLiveTypingMessage;

    @InjectView(R.id.timestamp)
    TextView txtLiveTypingTimeStamp;

    @InjectView(R.id.line_divider)
    FrameLayout liveTypingViewDivider;

    @InjectView(R.id.fab_live_typing)
    FloatingActionButton fabLiveTyping;

    @InjectView(R.id.fab_live_typing_active)
    FloatingActionButton fabLiveTypingActive;

    @InjectView(R.id.fab_private)
    FloatingActionButton fabPrivate;

    @InjectView(R.id.fab_private_active)
    FloatingActionButton fabPrivateActive;

    @InjectView(R.id.fab_regular)
    FloatingActionButton fabRegular;

    @InjectView(R.id.fab_regular_active)
    FloatingActionButton fabRegularActive;

    @InjectView(R.id.fab_hide_regular)
    FloatingActionButton fabHideRegular;

    @InjectView(R.id.fab_hide_regular_active)
    FloatingActionButton fabHideRegularActive;

//    @InjectView(R.id.fab_timer)
//    FloatingActionButton fabTimer;
//
//    @InjectView(R.id.fab_timer_active)
//    FloatingActionButton fabTimerActive;
//
//    @InjectView(R.id.text_timer)
//    TextView txtTimer;

    private ChatAdapter adapter;

    private String contextUserId;
    private String deviceUserId;

    public String conversationId = Config.EMPTY_STRING;
    private User deviceUser;

    private String contextUserProfileName;
    private int notificationType;

    private PresenceChannel deviceUserPresence;
    private PresenceChannel contextUserPresence;

    private UserChannel deviceUserChannel;
    private UserChannel contextUserChannel;

    private StatusChannel statusChannel;
    private MessageChannel messageChannel;

    private ContextUser contextUser;
    private Context context;

    private int statusColor;
    private int statusGreenColor;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private String encryptionKey;

    private Boolean isChatCreated = false;
    private Boolean isPrivateMessages = false;

    private Foreground.Binding listenerBinding;

    //virgil-integration
    ClientFactory clientFactory;

    //virgil-integration
    private ClientFactory getClientFactory() {
        if (clientFactory == null) {
            clientFactory = new ClientFactory(getString(R.string.access_token));
        }
        return clientFactory;
    }

    private KeyPair keyPair;
    private UserRegisterTask authTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // super.onCreate(savedInstanceState, R.layout.activity_chat);

        //listen for application in background
        listenerBinding = Foreground.get(getApplication()).addListener(this);

        //virgil-integration
        if(settings.getCardId().isEmpty() && settings.getPrivateKey().isEmpty() && settings.getPublicKey().isEmpty()) {
            authTask = new UserRegisterTask(User.getDeviceUser().getUsername());
            authTask.execute((Void) null);
        }else{
            publicKey = new PublicKey(settings.getPublicKey());
            privateKey = new PrivateKey(settings.getPrivateKey());
        }

        //prevent screenshot
        SecurityUtils.setScreenCaptureAllowed(ChatActivity.this, false);

        showKeyboard(getComposeMessageFabButton(), new CallbackListener.completion() {
            @Override
            public void done() {
            }
        });

        setListenerToRootView(new CallbackListener.completion() {
            @Override
            public void done() {
            }
        });

        context = this;
        statusColor = ContextCompat.getColor(context, R.color.gray);
        statusGreenColor = ContextCompat.getColor(context, R.color.md_teal_400);

        //pull data from the previous activity
        final Bundle bundle = getIntent().getExtras();

        //lets retrieve a notification type by default
        //this will help us better manage a user
        //tabbing the activity or clicking on a notification
        notificationType = bundle.getInt(Config.KEY_NOTIFICATION);

        deviceUser = User.getDeviceUser();
        deviceUserId = User.getDeviceUserId();

        //set needed variables for this chat
        //chat is setup via intent bundle
        //or a cheap lookup by user id to retrieve
        //a user object and a message id
        setUpChat();

        txtProfileName.setText(contextUserProfileName);

        //register references and set the device user as active
        initDeviceUserChannel();

        //write device users connection to the server
        //read context users connection from server
        setupConnectionChannel();

        //listen for status value changes on context user
        initContextUserChannel();

        //listen for values changes on the status channel
        setupStatusChannel();

        //the app toolbar
        setupToolbar();

        //setup adapter and recycler view
        setupAdapter();

        //init the message channel and activate event listeners
        setupMessageChannel();

        //click event which enables and disables private messaging
        togglePrivateMessaging(fabPrivate);
        togglePrivateMessaging(fabPrivateActive);
        togglePrivateMessaging(fabRegular);
        togglePrivateMessaging(fabRegularActive);

        //set message listener for sending new chat messages
        setSendMessageListener(new MessageListener.onSendMessage() {

            @Override
            public void sendMessage(final String text) {

                //call create text to initialize message reference
                messageChannel.createText(text, ContentType.TEXT);

                ChatActivity.this.sendMessage();

            }

        });

        //set message listener for sending new media messages
        setMediaTypeSelectionListener(new MessageListener.onMediaTypeSelection() {
            @Override
            public void mediaType(int mediaType) {

                if (!isChatCreated) {
                    messageChannel.createText("Media Sharing", ContentType.IMAGE);
                    isChatCreated = true;
                }

                Media media = new Media();
                media.setCaption("media testing v1");
                media.setLink("some awesome media");
                media.setType(mediaType);

                messageChannel.createMediaLink(media);
                messageChannel.createMediaLink(media);
                messageChannel.createMediaLink(media);

                ChatActivity.this.sendMessage();

            }
        });

        txtLiveTypingMessage.setTextColor(ContextCompat.getColor(context, getColorPrimary()));


    }

    private void sendMessage() {

        isChatCreated = false;

        //optionally call create status if we want to update
        //the device users status channel
        messageChannel.createStatus(contextUserProfileName);

        //send message to server via multiple channel update
        //virgil-integration
        messageChannel.send(deviceUserId, publicKey,privateKey, new MessageListener.onMessage() {
            @Override
            public void pending(final Chat chat) {

                if (deviceUserChannel.isLiveTyping()) {
                    liveTypingFrameLayout.setVisibility(View.GONE);
                    txtLiveTypingMessage.setText(Config.EMPTY_STRING);
                }

                //pending message might not have been delivered yet
                //add it to the list view to let the user know the
                //message was created
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addChat(chat, isPrivateMessages);
                    }
                });
            }

            @Override
            public void sent(Chat chat) {

                //update the chat as sent so the list item is updated
                updateSentChatUI(chat.getMessageId());

                //the message was sent but the user might be offline or
                //in active let notification method determine what to do
                notification(chat);

            }

            @Override
            public void unread(final Chat chat, final Firebase firebase) {

                //message has been sent but unread
                //if user is inactive lets add the message to a queue
                //if it's multiple messages lets listen for event changes on the last message sent
                contextUserChannel.user(new StatusListener.onUser() {

                    @Override
                    public void active(UserChannel userChannel) {

                    }

                    @Override
                    public void inactive(UserChannel userChannel) {

                        messageChannel.unreadMessagesQueue(firebase, chat);
                        messageChannel.lastUnreadMessageQueueListener(new MessageListener.onMessageRead() {
                            @Override
                            public void updateView(String messageId) {
                                updateReadChatUI(messageId);
                            }
                        });
                    }

                });
            }
        });
    }

    private void togglePrivateMessaging(FloatingActionButton floatingActionButton) {

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Message.IS_PRIVATE) {

                    regularMode();

                } else {

                    if (!Config.IS_SAVED_PASSCODE) {

                        Alerts.choosePasscode(context, new CallbackListener.completion() {
                            @Override
                            public void done() {

                                privateMode();

                            }
                        });

                    } else if (!Config.IS_PASSCODE) {

                        Alerts.passcode(context, new CallbackListener.completion() {
                            @Override
                            public void done() {

                                privateMode();

                            }
                        });

                    } else {

                        privateMode();
                    }
                }
            }
        });
    }

    private void regularMode() {

        Message.IS_PRIVATE = false;
        Message.IS_TIMER = false;

        Shape.modeColor(layoutRelativeToolbar, Color.WHITE);
        txtProfileName.setTextColor(ContextCompat.getColor(context, R.color.black_54_pc));

        ResizeAnimation resizeAnimation = new ResizeAnimation(layoutFrame, 20);
        resizeAnimation.setDuration(400);
        layoutFrame.startAnimation(resizeAnimation);

        txtConnectionStatus.setVisibility(View.VISIBLE);

        //show private mode UI
        fabPrivate.setVisibility(View.VISIBLE);
        fabRegular.setVisibility(View.GONE);

        fabPrivateActive.setVisibility(View.VISIBLE);
        fabRegularActive.setVisibility(View.GONE);

        fabBackNavi.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_black_arrow));

        imagePrivateMode.setVisibility(View.GONE);

        //show live typing buttons only in regular mode
        if (deviceUserChannel != null) {
            if (deviceUserChannel.isLiveTyping()) {
                fabLiveTyping.setVisibility(View.GONE);
                fabLiveTypingActive.setVisibility(View.VISIBLE);
            } else {
                fabLiveTyping.setVisibility(View.VISIBLE);
                fabLiveTypingActive.setVisibility(View.GONE);
            }
        }

        //hide timer buttons
//        fabTimer.setVisibility(View.GONE);
//        fabTimerActive.setVisibility(View.GONE);
//        txtTimer.setVisibility(View.GONE);

        //hide regular messages button
        fabHideRegular.setVisibility(View.GONE);
        fabHideRegularActive.setVisibility(View.GONE);

        reloadMessages();

    }

    private void reloadMessages() {

        //clear adapter items
        adapter.listManager.clearItems();

        //disconnect message channel to allow reload
        if (messageChannel != null) {
            messageChannel.disconnect();
        }

        //setup message channel
        setupMessageChannel();
    }

    private void privateMode() {

        Message.IS_PRIVATE = true;
        txtProfileName.setTextColor(Color.WHITE);
        Shape.modeColor(layoutRelativeToolbar, Color.BLACK);

        ResizeAnimation resizeAnimation = new ResizeAnimation(layoutFrame, 0);
        resizeAnimation.setDuration(400);
        layoutFrame.startAnimation(resizeAnimation);

        txtProfileName.setTextColor(Color.WHITE);
        txtConnectionStatus.setVisibility(View.GONE);

        //show regular mode UI
        fabRegular.setVisibility(View.VISIBLE);
        fabPrivate.setVisibility(View.GONE);

        fabRegularActive.setVisibility(View.VISIBLE);
        fabPrivateActive.setVisibility(View.GONE);

        fabBackNavi.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_white_arrow));

        imagePrivateMode.setVisibility(View.VISIBLE);

        //hide live typing buttons in private mode
        fabLiveTyping.setVisibility(View.GONE);
        fabLiveTypingActive.setVisibility(View.GONE);

        //show timer buttons
//        if (settings.isTimerShowing()) {
//            showTimerProps();
//        } else {
//            hideTimerProps();
//        }

        //show regular message button
        if (settings.isHideRegular()) {
            hideRegularProps();
        } else {
            showRegularProps();
        }

        reloadMessages();

    }

    private void setupMessageChannel() {

        if (!conversationId.isEmpty()) {

            messageChannel = new MessageChannel.Builder()
                    .init(conversationId)
                    .chatBetween(deviceUserId, contextUserId)
                    .addMessageListener(this)
                    .loadAllMessages(this)
                    .connect();

            String[] strings = conversationId.split("-");
            encryptionKey = strings[2];

            if (!contextUserId.isEmpty()) {

                //check if context user is online, and monitor connection activity
                contextUserPresence = new PresenceChannel.Builder()
                        .init()
                        .connection(contextUserId)
                        .lastSeen()
                        .addUserPresenceListener(this)
                        .start();

            }
        }
    }

    private void setupStatusChannel() {

        if (!conversationId.isEmpty()) {

            statusChannel = new StatusChannel.Builder()
                    .init()
                    .status(deviceUserId)
                    .addStatusListener(this)
                    .load()
                    .connect();
        }
    }

    private void setupConnectionChannel() {

        //register device user as online, and monitor connection activity
        deviceUserPresence = new PresenceChannel.Builder()
                .init()
                .connection(deviceUserId)
                .lastSeen()
                .addConnectionListener(this)
                .start();

    }

    private void initDeviceUserChannel() {

        if (!conversationId.isEmpty()) {

            deviceUserChannel = new UserChannel.Builder()
                    .init()
                    .channel(deviceUserId, conversationId)
                    .active()
                    .count()
                    .typingIndicator()
                    .sync()
                    .connect();

            //reset status values on server and status values locally
            deviceUserChannel.reset(new StatusListener.onReset() {
                @Override
                public void reset() {
                }
            });

            //contains settings for user enabled / disabled messaging features
            messagingSettings();

            //TODO restrict with type indicator policy
            //enable type indicator for device user
            enableTypingIndicator(deviceUserChannel);

            //if live typing is true, do some UI enhancement
            if (deviceUserChannel.isLiveTyping()) {
                liveTypingFrameLayout.setVisibility(View.GONE);
                txtLiveTypingTitle.setText(contextUserProfileName);
                txtLiveTypingTimeStamp.setText(getString(R.string.live_typing_text));
                int teal = ContextCompat.getColor(context, R.color.md_teal_400);
                txtLiveTypingTitle.setTextColor(teal);
                liveTypingViewDivider.setVisibility(View.GONE);
            }

            //contains user actions for enabling / disabled messaging features
            messagingFeatures();

        }
    }

    private void messagingSettings() {

        if (settings.isLiveTyping()) {
            liveTypingProps();
        }

    }

    private void messagingFeatures() {

        fabLiveTyping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveTypingProps();
            }
        });

        fabLiveTypingActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabLiveTyping.setVisibility(View.VISIBLE);
                fabLiveTypingActive.setVisibility(View.GONE);
                deviceUserChannel.isLiveTyping(false);
                settings.isLiveTyping(false);
            }
        });

      /*  fabTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimerProps();
            }
        });

        fabTimerActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTimerProps();
            }
        });*/

        fabHideRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRegularProps();
            }
        });

        fabHideRegularActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegularProps();
            }
        });

//        txtTimer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//
//                new MaterialDialog.Builder(v.getContext())
//                        .title(R.string.textChooseTimer)
//                        .items(R.array.timer_text)
//                        .itemsCallbackSingleChoice(settings.getTimerIndex(), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//
//                                messageChannel.setTimer(text.toString());
//                                txtTimer.setText(text);
//                                settings.setTimerIndex(which);
//                                settings.setTimerValue(text.toString());
//
//                                return true;
//                            }
//                        })
//                        .positiveText(R.string.choose)
//                        .show();
//            }
//        });
    }

    public void showRegularProps() {
        fabHideRegular.setVisibility(View.VISIBLE);
        fabHideRegularActive.setVisibility(View.GONE);
        isPrivateMessages = false;
        settings.isHideRegular(false);
        reloadMessages();
    }

    public void liveTypingProps() {
        fabLiveTyping.setVisibility(View.GONE);
        fabLiveTypingActive.setVisibility(View.VISIBLE);
        deviceUserChannel.isLiveTyping(true);
        settings.isLiveTyping(true);
    }

    public void hideRegularProps() {
        fabHideRegular.setVisibility(View.GONE);
        fabHideRegularActive.setVisibility(View.VISIBLE);
        isPrivateMessages = true;
        settings.isHideRegular(true);
        reloadMessages();
    }

  /*  public void hideTimerProps() {
        fabTimer.setVisibility(View.VISIBLE);
        txtTimer.setVisibility(View.GONE);
        fabTimerActive.setVisibility(View.GONE);
        txtTimer.setText(settings.getTimerValue());
        Message.IS_TIMER = false;
        settings.isTimerShowing(false);
    }

    public void showTimerProps() {
        fabTimer.setVisibility(View.GONE);
        txtTimer.setVisibility(View.VISIBLE);
        fabTimerActive.setVisibility(View.VISIBLE);
        txtTimer.setText(settings.getTimerValue());
        Message.IS_TIMER = true;
        settings.isTimerShowing(true);
    }*/

    private void initContextUserChannel() {

        if (!conversationId.isEmpty()) {

            contextUserChannel = new UserChannel.Builder()
                    .init()
                    .channel(contextUserId, conversationId)
                    .active(false)
                    .sync()
                    .addActiveListener()
                    .typingIndicator()
                    .addTypingIndicatorListener(this)
                    .connect();
        }
    }

    private void setupAdapter() {

        // touch guard manager (this class is required to suppress scrolling while swipe-dismiss animation is running)
        RecyclerViewTouchActionGuardManager recyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        recyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        recyclerViewTouchActionGuardManager.setEnabled(true);

        RecyclerViewSwipeManager recyclerViewSwipeManager = new RecyclerViewSwipeManager();

        adapter = new ChatAdapter(this, recyclerView, context);
        adapter.setMessageListener(this);

        RecyclerView.Adapter wrappedAdapter = recyclerViewSwipeManager.createWrappedAdapter(adapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        recyclerView.setItemAnimator(new SlideInUpAnimator());
        // recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(wrappedAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        recyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        recyclerViewSwipeManager.attachRecyclerView(recyclerView);
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        fabBackNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (deviceUserChannel != null) {

                    //#status channel
                    //set device user as inactive
                    deviceUserChannel.isActive(false);

                    //disable type indicator
                    deviceUserChannel.setTypingIndicator(TypingIndicatorListener.FINISHED);

                }

                ChatActivity.this.onBackPressed();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // final BottomSheetLayout bottomSheet = Alerts.getBottomSheetLayout(v.getRootView(), ChatActivity.this, contextUser);
                Alerts.getBottomSheetLayout(v.getRootView(), ChatActivity.this, contextUser);

            }
        });
    }

    private void setUpChat() {
        switch (notificationType) {
            case NotificationType.NOTIFICATION:
                getContextUserFromNotification();
                break;
            case NotificationType.TABBED:
                ContextUser contextUser = EventBus.getDefault().removeStickyEvent(ContextUser.class);
                getContextUser(contextUser);
                break;
            default:
                ChatActivity.this.onBackPressed();

        }
    }

    private void getContextUserFromNotification() {

        String userId = EventBus.getDefault().removeStickyEvent(String.class);

        if (!userId.isEmpty()) {
            ContextUser contextUser = ContextUser.findOne(userId);
            if (contextUser != null) {
                getContextUser(contextUser);
            } else {
                ChatActivity.this.onBackPressed();
            }
        } else {
            ChatActivity.this.onBackPressed();
        }
    }

    private void getContextUser(ContextUser user) {

        if (user != null) {

            if (!user.getUserId().isEmpty()) {

                //lets retrieve the context user object
                //and remove reference to save memory
                contextUser = user;

                //set values from the previous activity
                contextUserId = contextUser.getUserId();
                contextUserProfileName = contextUser.getProfileName();

                if (contextUser.getConversationId() != null
                        && !contextUser.getConversationId().isEmpty()) {

                    conversationId = contextUser.getConversationId();
                    sendMessageView.setVisibility(View.VISIBLE);

                } else {

                    //TODO check if conversation id is available before allowing the user to send messages
                    loaderLayout.setVisibility(View.VISIBLE);

                    People.setUpChannel(contextUserId, new CallbackListener.callbackForResults() {

                        @Override
                        public void success(Object result) {

                            loaderLayout.setVisibility(View.GONE);
                            sendMessageView.setVisibility(View.VISIBLE);

                            conversationId = (String) result;

                            //initialize channels
                            initDeviceUserChannel();
                            initContextUserChannel();
                            setupStatusChannel();
                            setupMessageChannel();

                            contextUser.setProfileName(contextUserProfileName);
                            contextUser.setConversationId((String) result);
                            //contextUser.isAccepted(people.isAccepted());
                            contextUser.pinInBackground(Config.DB_NAME_CONTEXT_USERS);

                        }

                        @Override
                        public void error(String error) {

                            //TODO: handle error gracefully

                        }
                    });
                }

            } else {
                ChatActivity.this.onBackPressed();
            }

        } else {
            ChatActivity.this.onBackPressed();
        }
    }

    private void notification(final Chat chat) {

        //TODO add setting check
        //TODO when user enables push notifications
        contextUserPresence.presence(new PresenceListener.OnPresence() {

            @Override
            public void online() {

            }

            @Override
            public void offline() {
                if (contextUser.getProfile() != null) {
                    if (contextUser.getProfile().getPlayerId() != null &&
                            !contextUser.getProfile().getPlayerId().isEmpty()) {
                        Notifications notifications = new Notifications();
                        notifications.send(contextUser.getProfile().getPlayerId(), chat.getLabel());
                    }
                }
            }
        });

        contextUserChannel.user(new StatusListener.onUser() {

            @Override
            public void active(UserChannel userChannel) {
                //TODO temp implementation investigate
                //TODO save last message on the Context User
                //context user is active
                //lets save the last sent message in our status feed
                userChannel.updateStatus(createStatus(chat), UserChannel.UPDATE_CHILDREN);

            }

            @Override
            public void inactive(UserChannel userChannel) {

                //save the device users last message to the status channel of
                //context user and increment the count by one
                userChannel.updateStatus(createStatus(chat), UserChannel.RUN_TRANSACTION);

            }
        });
    }

    private Status createStatus(Chat chat) {
        final Status status = new Status();
        status.setProfileName(deviceUser.getProfileName());
        status.setLastMessage(chat.getLabel());
        status.setMessageId(chat.getMessageId());
        status.setTimestamp();
        status.setUserId(deviceUserId);
        return status;
    }

    @Override
    public void populateView(Message message, DataSnapshot child) {

        final Chat chat = new Chat();
        chat.updateMessages(message, child, deviceUserId, contextUserProfileName);
        chat.messageDecryption(message, deviceUserId, privateKey);

        //lets check if the message is a timer message
        if (message.getTimer() != null) {

            //set the chat timer so the UI is well updated.
            chat.setTimer(message.getTimer().longValue());

            //if the message time is zero
            if (message.getTimer() == Config.NUMBER_ZERO) {

                //remove it from the server
                //this will fire a remove event in the UI
                chat.getRef().removeValue();

            } else {

                if (Message.IS_PRIVATE && chat.getDirection() == Chat.Direction.INCOMING) {

                    TimeUtils.countDown(message.getTimer().longValue(), new CallbackListener.timer() {
                        @Override
                        public void done() {

                            //remove message from firebase
                            chat.getRef().removeValue();

                            if (messageChannel != null) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeChatUI(chat.getMessageId());
                                    }
                                });
                            }
                        }

                        @Override
                        public void running(long counter) {

                            //decrement timer on server
                            MessageChannel.updateTimedMessage(counter, chat);

                        }
                    });
                }
            }

        } else {

            //queue messages from context user as unread
            messageChannel.unreadMessagesQueue(message, chat);

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addChat(chat, isPrivateMessages);
            }
        });

    }

    @Override
    public void updateView() {

        //listen for the last unread message via child reference, if the value changes
        //set all sent messages to read in the UI
        //query message id's to see if they have been set as read
        messageChannel.lastUnreadMessageQueueListener(new MessageListener.onMessageRead() {
            @Override
            public void updateView(String messageId) {
                updateReadChatUI(messageId);
            }
        });
    }

    @Override
    public void received(final Message message, DataSnapshot child) {

        final Chat chat = new Chat();
        chat.incomingMessages(message, child.getKey(), contextUserProfileName);
        chat.messageDecryption(message, deviceUserId, privateKey);
        chat.setRef(child.getRef());

        if (message.getTimer() != null) {

            if (Message.IS_PRIVATE) {

                if (message.getTimer() != Config.NUMBER_ZERO) {

                    //set the chat timer so the UI is well updated.
                    chat.setTimer(message.getTimer().longValue());

                    TimeUtils.countDown(message.getTimer().longValue(), new CallbackListener.timer() {
                        @Override
                        public void done() {

                            //remove message from firebase
                            chat.getRef().removeValue();

                            if (messageChannel != null) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeChatUI(chat.getMessageId());
                                    }
                                });
                            }
                        }

                        @Override
                        public void running(long counter) {

                            //decrement timer on server
                            MessageChannel.updateTimedMessage(counter, chat);

                        }
                    });
                }
            }

        } else {

            //update messages from device user as read
            messageChannel.updateMessagesAsRead(message, child, new MessageListener.onMessageRead() {
                @Override
                public void updateView(String messageId) {

                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addChat(chat, isPrivateMessages);
            }
        });
    }

    @Override
    public void read(final Message message, DataSnapshot child) {

        if (message.getDeliveryStatus() == DeliveryStatus.SENT &&
                contextUserChannel.getActive() != null &&
                contextUserChannel.getActive() &&
                !Message.IS_PRIVATE && message.getTimer() == null) {

            //set each unread message as read
            messageChannel.markMessagesAsRead(child, new MessageListener.onMessageRead() {
                @Override
                public void updateView(String messageId) {
                    updateReadChatUI(messageId);
                }
            });
        }
    }

    @Override
    public void deleted(Message message, DataSnapshot child) {
        if (message.getDeliveryStatus() == DeliveryStatus.DELETED) {
            updateDeletedChatUI(child.getKey());
        }
    }

    @Override
    public void removed(final DataSnapshot child) {
        if (child.exists()) {
            Message message = new Message(child.getValue(HashMap.class));
            if (message.getUserId().equals(contextUserId)) {
                removeChatUI(child.getKey());
            }
        }
    }

    @Override
    public void edited(Message message, DataSnapshot child) {
        if (message.getDeliveryStatus() == DeliveryStatus.EDITED) {

            try {
                String text = SecurityUtils.decrypt(message.getText(), deviceUserId);
                updateEditedChatUI(child.getKey(), text);
            }catch (Exception e){

            }

         //   updateEditedChatUI(child.getKey(), message.getText());
        }
    }

    @Override
    public void failed(FirebaseError firebaseError) {

    }

    private void updateReadChatUI(String messageId) {
        Chat chat = adapter.findChat(messageId);
        if (chat != null) {
            chat.setDeliveryStatus(DeliveryStatus.READ);
            adapter.listManager.updateItem(chat);
        }
    }

    private void updateSentChatUI(String messageId) {
        Chat chat = adapter.findChat(messageId);
        if (chat != null) {
            chat.setDeliveryStatus(DeliveryStatus.SENT);
            adapter.listManager.updateItem(chat);
        }
    }

    private void removeChatUI(String messageId) {
        int position = adapter.findChatPosition(messageId);
        if (position != -1) {
            adapter.listManager.removeItem(position);
        }
    }

    private void updateDeletedChatUI(String messageId) {
        final Chat chat = adapter.findChat(messageId);
        if (chat != null) {
            chat.setDeliveryStatus(DeliveryStatus.DELETING);
            adapter.listManager.updateItem(chat);
            recyclerView.scrollToPosition(ChatAdapter.CURRENT_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    chat.setDeliveryStatus(DeliveryStatus.DELETED);
                    chat.setText(Config.DELETED_MESSAGE);
                    adapter.listManager.updateItem(chat);
                    recyclerView.scrollToPosition(ChatAdapter.CURRENT_POSITION);

                }
            }, 1500);

        }
    }

    private void updateEditedChatUI(String messageId, final String message) {
        final Chat chat = adapter.findChat(messageId);
        if (chat != null) {
            chat.setDeliveryStatus(DeliveryStatus.EDITING);
            adapter.listManager.updateItem(chat);
            recyclerView.scrollToPosition(ChatAdapter.CURRENT_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    chat.setDeliveryStatus(DeliveryStatus.EDITED);
                    chat.setText(message);
                    adapter.listManager.updateItem(chat);
                    recyclerView.scrollToPosition(ChatAdapter.CURRENT_POSITION);

                }
            }, 1500);
        }
    }

    @Override
    public void populateView(Map map) {

    }

    @Override
    public void loaded() {

    }

    @Override
    public void updateView(Map map, int type) {

        Status status = (Status) map;

        if (messageChannel.isLoaded()) {

            if (deviceUserChannel.getActive() &&
                    !status.getUserId().equals(contextUserId) &&
                    type == StatusChannel.UPDATE) {

                linearLayout.setVisibility(View.VISIBLE);
                txtLastMessageOne.setText(status.getLastMessage());
                txtStatusName.setText(status.getProfileName());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        linearLayout.setVisibility(View.GONE);
                        txtLastMessageOne.setText(Config.EMPTY_STRING);
                        txtStatusName.setText(Config.EMPTY_STRING);

                    }
                }, 5000);

            } else if (type == StatusChannel.UPDATE &&
                    Foreground.get().isBackground() &&
                    status.getUserId().equals(contextUserId)) {

                //check if app is inactive meaning its in the background
                //send local notification
                Alerts.notifications(
                        ChatActivity.this,
                        ChatActivity.class,
                        status.getProfileName(),
                        status.getLastMessage(),
                        status.getUserId());

            } else if (Message.IS_PRIVATE &&
                    status.getUserId().equals(deviceUserId)) {

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        super.onKeyDown(keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (deviceUserChannel != null) {

                //#status channel
                //set device user as inactive
                deviceUserChannel.isActive(false);

                //disable type indicator
                deviceUserChannel.setTypingIndicator(TypingIndicatorListener.FINISHED);

            }

            // return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //OneSignal.onResumed();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //OneSignal.onPaused();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnected() {
        // set me as inactive
        //and remove my typing status
        if (deviceUserChannel != null) {
            deviceUserChannel.onDisconnect();
        }
        loaderLayout.setVisibility(View.GONE);

    }

    @Override
    public void onDisconnected() {
        loaderLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void online() {
        txtConnectionStatus.setTextColor(statusGreenColor);
        txtConnectionStatus.setText(R.string.online_text);
        contextUserPresence.setOnline(true);
    }

    @Override
    public void lastSeen(String lastSeen) {
        txtConnectionStatus.setTextColor(statusColor);
        txtConnectionStatus.setText(lastSeen);
        contextUserPresence.setOnline(false);
    }

    @Override
    public void offline() {
        txtConnectionStatus.setText(R.string.offline_text);
        contextUserPresence.setOnline(false);
    }

    @Override
    public void cancelled() {
        txtConnectionStatus.setText(R.string.offline_text);
    }

    @Override
    public void startedTyping() {
        txtConnectionStatus.setTextColor(statusColor);
        txtConnectionStatus.setText(R.string.typing_indicator_text);
    }

    @Override
    public void liveTyping(String s) {
        liveTypingFrameLayout.setVisibility(View.VISIBLE);
        txtLiveTypingMessage.setText(s);
    }

    @Override
    public void finishedTyping() {
        txtConnectionStatus.setTextColor(statusGreenColor);
        txtConnectionStatus.setText(R.string.online_text);
        liveTypingFrameLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (messageChannel != null) {
            deviceUserPresence.stop();
            contextUserPresence.stop();
            Message.IS_PRIVATE = false;
            messageChannel.disconnect();
            contextUserChannel.disconnect();
            deviceUserChannel.disconnect();
        }

        if (listenerBinding != null) {
            listenerBinding.unbind();
        }

        if (statusChannel != null) {
            statusChannel.disconnect();
        }
    }

    @Override
    public void onBecameForeground() {
        if (deviceUserChannel != null) {
            deviceUserChannel.isActive(true);
        }
    }

    @Override
    public void onBecameBackground() {
        regularMode();
        Config.IS_PASSCODE = false;
        if (deviceUserChannel != null) {
            deviceUserChannel.isActive(false);
        }
    }

    @Override
    public void updateAfterMessageEdit(Chat chat) {
        //lets send a status update after message has been edited
        //but hasn't been sent or read
        if (chat.getDeliveryStatus() == DeliveryStatus.PENDING ||
                chat.getDeliveryStatus() == DeliveryStatus.SENT) {
            contextUserChannel.updateStatus(createStatus(chat), UserChannel.UPDATE_CHILDREN);
        }
    }

    /**
     * Represents an asynchronous registration task used to register new VirgilCard.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private String cardId;

        UserRegisterTask(String email) {
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            keyPair = KeyPairGenerator.generate();
            VirgilCardTemplate.Builder builder = new VirgilCardTemplate.Builder();
            builder.setIdentity(new ValidatedIdentity(IdentityType.EMAIL, email));
            builder.setPublicKey(keyPair.getPublic());

            try {
              //  String actionId = getClientFactory().getIdentityClient().verify(IdentityType.EMAIL, email);

                VirgilCard card = getClientFactory().getPublicKeyClient().createCard(builder.build(), keyPair.getPrivate());

                cardId = card.getId();

                new Testable.Spec("Registered Identity").describe("value of email").expect(email).run();
                new Testable.Spec("Registered Identity").describe("value of card id").expect(card.getId()).run();
            }
            catch (ServiceException e) {
                // TODO: show error message
                new Testable.Spec("Registered Identity").describe("error").expect(e.getMessage()).run();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;

            if (success) {
                settings.setIdentity(email);
                settings.setCardId(cardId);
                settings.setPrivateKey(keyPair.getPrivate().getAsString());
                settings.setPublicKey(keyPair.getPublic().getAsString());
            } else {
                // TODO: registration failed
                new Testable.Spec("Registered Identity").describe("registration failed").expect("error").run();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;

        }
    }

    private void registerVirgilCard(ValidatedIdentity identity) {

        // Obtain public key for the Private Keys Service retrieved from the
        // Public Keys Service
        SearchCriteria criteria = new SearchCriteria();
        criteria.setType(IdentityType.EMAIL);
        criteria.setValue(User.getDeviceUser().getUsername());

        List<VirgilCard> cards = getClientFactory().getPublicKeyClient().search(criteria);
        VirgilCard serviceCard = cards.get(0);

        // search the card by email identity on Virgil Keys service.
        SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setValue(identity.getValue()).setIncludeUnauthorized(true);
        cards = getClientFactory().getPublicKeyClient().search(criteriaBuilder.build());

        // The app is verifying whether the user really owns the provided email
        // address and getting a temporary token for public key registration
        // (in case that the card is not registered, otherwise this token will be
        // used to retrieve a private key).
        VirgilCard card;
        if (!cards.isEmpty()) {
            card = cards.get(0);

            new Testable.Spec("Verify VirgilCard").describe("Virgil Card ID").expect(card.getId()).run();
            new Testable.Spec("Verify VirgilCard").describe("Public key").expect(Base64.decode(card.getPublicKey().getKey()).toString()).run();

            // Load member's keys
            PrivateKeyInfo privateKeyInfo = getClientFactory().getPrivateKeyClient(serviceCard).get(card.getId(), identity);

            new Testable.Spec("Verify VirgilCard").describe("Private key").expect(Base64.decode(privateKeyInfo.getKey()).toString()).run();

            settings.setCardId(card.getId());
            settings.setPublicKey(card.getPublicKey().toString());
            settings.setPrivateKey(card.getPublicKey().getKey());
            settings.setPrivateKey(privateKeyInfo.getKey());

        } else {

            new Testable.Spec("Register Virgil Card").describe("retrieving card failed").run();

            // generate a new public/private key pair.
            KeyPair keyPair = KeyPairGenerator.generate();

            // The app is registering a Virgil Card which includes a
            // public key and an email address identifier. The card will
            // be used for the public key identification and searching
            // for it in the Public Keys Service.
            VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey(keyPair.getPublic());
            card = getClientFactory().getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());

            // Private key can be added to Virgil Security storage if you want to
            // easily synchronise your private key between devices.
            getClientFactory().getPrivateKeyClient(serviceCard).stash(card.getId(), keyPair.getPrivate());

            new Testable.Spec("Register VirgilCard").describe("Virgil Card ID").expect(card.getId()).run();
            new Testable.Spec("Register VirgilCard").describe("Public key").expect(keyPair.getPublic().getAsString()).run();
            new Testable.Spec("Register VirgilCard").describe("Private key").expect(keyPair.getPrivate().getAsString()).run();

            settings.setCardId(card.getId());
            settings.setPublicKey(keyPair.getPublic().getAsString());
            settings.setPrivateKey(keyPair.getPrivate().getAsString());

            publicKey = new PublicKey(keyPair.getPublic().getAsString());
            privateKey = new PrivateKey(keyPair.getPrivate().getAsString());

        }
    }

}
