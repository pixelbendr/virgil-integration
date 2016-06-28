package com.psyphertxt.android.cyfa.ui.listeners;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.model.Chat;

import android.content.DialogInterface;

/**
 * Listener for activities that have the ability to send and receive messages
 */
public class MessageListener {

    public interface onSendMessage {

        void sendMessage(String text);

    }

    public interface onMediaTypeSelection {

        void mediaType(int mediaType);

    }

    public interface onMessageChange {

        void onDataChange(DataSnapshot child);

        void onCancelled(FirebaseError firebaseError);

    }

    public interface onChatMessage {

        void received(Message message, DataSnapshot child);

        void read(Message message, DataSnapshot child);

        void edited(Message message, DataSnapshot child);

        void deleted(Message message, DataSnapshot child);

        void removed(DataSnapshot child);

        void failed(FirebaseError firebaseError);

    }

    public interface onMessageLoaded {

        void populateView(Message message, DataSnapshot child);

        void updateView();

    }

    public interface onMessageAdded {

        void onChildAdded(DataSnapshot child, String s);

        void onChildChanged(DataSnapshot child, String s);

        void onChildRemoved(DataSnapshot child);

        void onChildMoved(DataSnapshot child, String s);

        void onCancelled(FirebaseError firebaseError);

    }

    public interface onMessageRead {

        void updateView(String messageId);
    }

    public interface onMessageUpdate {

        void updateAfterMessageEdit(Chat chat);
    }


    public interface onMessage {

        void pending(Chat chat);

        void sent(Chat chat);

        void unread(Chat chat, final Firebase firebase);

    }

    public interface onMessageChannel {

        void get(Message message);

    }

    public interface onMessageDirection {

        void incoming();
        void outgoing();

    }

}

