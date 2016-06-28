package com.psyphertxt.android.cyfa.ui.adapters;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.hannesdorfmann.adapterdelegates.AdapterDelegatesManager;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.channel.MessageChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.model.Chat.MessageType;
import com.psyphertxt.android.cyfa.model.ContentType;
import com.psyphertxt.android.cyfa.model.DeliveryStatus;
import com.psyphertxt.android.cyfa.model.Themes;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.delegates.ChatAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.delegates.WalkthroughAdapterDelegate;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.SwipeEventListener;
import com.psyphertxt.android.cyfa.ui.listeners.SwipeLeftResultAction;
import com.psyphertxt.android.cyfa.ui.listeners.UnpinResultAction;
import com.psyphertxt.android.cyfa.ui.manager.ListManager;
import com.psyphertxt.android.cyfa.ui.viewholder.ChatViewHolder;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SwipeableItemAdapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private RecyclerView recyclerView;
    private Context context;
    private Chat chat;
    private ChatActivity chatActivity;
    public ListManager listManager;
    private AdapterDelegatesManager<List<? extends Object>> delegatesManager;

    private SwipeEventListener swipeEventListener;
    private MessageListener.onMessageUpdate onMessageUpdate;

    private static final int DEFAULT_MODE = 0;
    private static int MODE = DEFAULT_MODE;
    private static final int EDIT_MODE = 1;
    private static final int DELETE_MODE = 2;
    public static int CURRENT_POSITION = 0;

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }


    public ChatAdapter(Activity activity, RecyclerView recyclerView, final Context context) {

        super();
        this.list = new ArrayList<>();
        this.recyclerView = recyclerView;
        this.context = context;
        listManager = new ListManager(this, list);
        chatActivity = (ChatActivity) activity;

        delegatesManager = new AdapterDelegatesManager<>();
        delegatesManager.addDelegate(new ChatAdapterDelegate(activity, 0));
        delegatesManager.addDelegate(new WalkthroughAdapterDelegate(activity, 1));

        //enable the apps main feature
        //which is editing and deleting sent messages
        valueProposition();

        setHasStableIds(true);
    }

    public SwipeEventListener getSwipeEventListener() {
        return swipeEventListener;
    }

    public void setMessageListener(MessageListener.onMessageUpdate onMessageUpdate) {
        this.onMessageUpdate = onMessageUpdate;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return listManager.getId(position);

    }

    //adds a chat item based on a conditional isPrivate
    public void addChat(Chat chat, Boolean isPrivateOnly) {
        //if we are in private mode add all messages
        if (Message.IS_PRIVATE && isPrivateOnly) {
            if (chat.getMessageType() == MessageType.PRIVATE) {
                listManager.addItem(chat);
            }
        } else if (Message.IS_PRIVATE) {
            listManager.addItem(chat);
        } else {
            //if we are in regular mode add only regular messages
            if (chat.getMessageType() != MessageType.PRIVATE) {
                listManager.addItem(chat);
            }
        }
    }

    public Chat findChat(String messageId) {
        if (list != null && !list.isEmpty()) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Chat chat = (Chat) list.get(i);
                if (list.get(i) instanceof Chat &&
                        chat.getMessageId().equals(messageId)) {
                    CURRENT_POSITION = i;
                    return chat;
                }
            }
        }
        return null;
    }

    public int findChatPosition(String messageId) {
        if (list != null && !list.isEmpty()) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Chat chat = (Chat) list.get(i);
                if (list.get(i) instanceof Chat &&
                        chat.getMessageId().equals(messageId)) {
                    CURRENT_POSITION = i;
                    return i;
                }
            }
        }
        return -1;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public int getItemViewType(int position) {
        return delegatesManager.getItemViewType(list, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return delegatesManager.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ChatViewHolder) {
            ((ChatViewHolder) viewHolder).setSwipeItemHorizontalSlideAmount(0);
            setChat((Chat) list.get(i));
        }
        delegatesManager.onBindViewHolder(list, i, viewHolder);
    }

    @Override
    public SwipeResultAction onSwipeItem(RecyclerView.ViewHolder holder, int position, int result) {
        switch (result) {
            // swipe left -- pin
            //edit message
            case Swipeable.RESULT_SWIPED_RIGHT:
                MODE = EDIT_MODE;
                return new SwipeLeftResultAction(this, position);
            // swipe left -- pin
            //confirm deletion
            case Swipeable.RESULT_SWIPED_LEFT:
                MODE = DELETE_MODE;
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                return new UnpinResultAction(this, position);
        }
    }

    @Override
    public int onGetSwipeReactionType(RecyclerView.ViewHolder holder, int position, int x, int y) {
        if (isPrivateMessage(holder)) {
            return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
        } else if (isRegularMessage(holder)) {
            return Swipeable.REACTION_CAN_SWIPE_RIGHT;
        }
        return Swipeable.REACTION_CAN_NOT_SWIPE_ANY;
    }

    @Override
    public void onSetSwipeBackground(RecyclerView.ViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_delete_left;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_edit_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);

    }

    private Boolean isPrivateMessage(RecyclerView.ViewHolder viewHolder) {
        if (Message.IS_PRIVATE) {
            if (viewHolder instanceof ChatViewHolder) {
                ChatViewHolder chatTextViewHolder = (ChatViewHolder) viewHolder;
                Chat chat = chatTextViewHolder.getChat();
                if (!chat.getUserId().equals(User.getDeviceUserId()) &&
                        chat.getMessageType() != MessageType.REGULAR &&
                        chat.getDeliveryStatus() != DeliveryStatus.DELETED &&
                        chat.getContentType() == ContentType.TEXT) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean isRegularMessage(RecyclerView.ViewHolder viewHolder) {
        if (!Message.IS_PRIVATE) {
            if (viewHolder instanceof ChatViewHolder) {
                ChatViewHolder chatTextViewHolder = (ChatViewHolder) viewHolder;
                Chat chat = chatTextViewHolder.getChat();
                if (!chat.getUserId().equals(User.getDeviceUserId()) &&
                        chat.getMessageType() != Chat.MessageType.PRIVATE &&
                        chat.getContentType() == ContentType.TEXT) {
                    return true;
                }
            }
        }
        return false;
    }

    public void editMessage(final int position) {

        if (position >= 0) {

            final Chat chat = (Chat) list.get(position);
            final String oldMessage = chat.getText();

            MaterialDialog builder = new MaterialDialog.Builder(context)
                    .titleColor(Themes.getStoredColor(context))
                    .title(R.string.edit_message_title)
                    .customView(R.layout.add_status_message_view, true)
                    .positiveText(R.string.edit_text)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            super.onPositive(dialog);
                            final TextView txtEditedMessage = (TextView) dialog.findViewById(R.id.text_status_message);

                            if (!txtEditedMessage.getText().toString().isEmpty()) {

                                chat.setText(txtEditedMessage.getText().toString());
                                notifyItemChanged(position);
                                recyclerView.scrollToPosition(position);

                                try {
                                    MessageChannel.editMessage(chatActivity.deviceUserEncryption(), chat, txtEditedMessage.getText().toString(), new CallbackListener.callback() {
                                        @Override
                                        public void success() {

                                            onMessageUpdate.updateAfterMessageEdit(chat);
                                        }

                                        @Override
                                        public void error(String error) {

                                            chat.setText(oldMessage);

                                            notifyItemChanged(position);
                                            recyclerView.scrollToPosition(position);

                                            Alerts.show(context,
                                                    context.getString(R.string.edit_message_error_title),
                                                    context.getString(R.string.edit_message_error_message));

                                        }
                                    });
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                unpinItem(chat, position);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            unpinItem(chat, position);
                        }

                    })
                    .build();

            View view = builder.getCustomView();
            TextView textView = ButterKnife.findById(view, R.id.text_status_message);
            textView.setText(chat.getText());

            builder.setCanceledOnTouchOutside(false);
            builder.show();

        } else {
            Alerts.show(context,
                    context.getString(R.string.edit_message_error_title),
                    context.getString(R.string.edit_message_error_message));
        }

    }

    private void unpinItem(Chat chat, int position) {
        chat.isPinned(false);
        notifyItemChanged(position);
        recyclerView.scrollToPosition(position);
        MODE = DEFAULT_MODE;
    }

    public void deleteMessage(final int position) {

        if (position >= 0) {

            final Chat chat = (Chat) list.get(position);
            final String oldMessage = chat.getText();

            MaterialDialog builder = new MaterialDialog.Builder(context)
                    .titleColor(Themes.getStoredColor(context))
                    .title(R.string.delete_message_title)
                    .content(chat.getText())
                    .positiveText(R.string.delete_message_text)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);

                            chat.setDeliveryStatus(DeliveryStatus.DELETED);
                            chat.setText(Config.DELETED_MESSAGE);
                            notifyItemChanged(position);
                            recyclerView.scrollToPosition(position);
                            try {
                                MessageChannel.deletePrivateMessage(chatActivity.deviceUserEncryption(), chat, new CallbackListener.callback() {
                                    @Override
                                    public void success() {

                                    }

                                    @Override
                                    public void error(String error) {

                                        chat.setText(oldMessage);

                                        notifyItemChanged(position);
                                        recyclerView.scrollToPosition(position);

                                        Alerts.show(context,
                                                context.getString(R.string.delete_message_error_title),
                                                context.getString(R.string.delete_message_error_message));

                                    }
                                });
                            } catch (GeneralSecurityException e) {
                                Alerts.show(context,
                                        context.getString(R.string.delete_message_error_title),
                                        context.getString(R.string.delete_message_error_message));
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            unpinItem(chat, position);
                        }
                    })
                    .build();

            builder.setCanceledOnTouchOutside(false);
            builder.show();

        } else {

            Alerts.show(context,
                    context.getString(R.string.delete_message_error_title),
                    context.getString(R.string.delete_message_error_message));

        }
    }

    private void valueProposition() {

        swipeEventListener = new SwipeEventListener() {
            @Override
            public void onItemRemoved(int position) {

            }

            @Override
            public void onItemPinned(int position) {
                if (MODE == EDIT_MODE) {
                    editMessage(position);
                } else if (MODE == DELETE_MODE) {
                    deleteMessage(position);
                }
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
            }
        };
    }


}
