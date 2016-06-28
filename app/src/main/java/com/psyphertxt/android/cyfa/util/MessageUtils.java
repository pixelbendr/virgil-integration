package com.psyphertxt.android.cyfa.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;

public class MessageUtils {

    public static ValueEventListener onChange(Query ref, final MessageListener.onMessageChange messageChange) {
        return ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messageChange.onDataChange(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (firebaseError != null) {
                    messageChange.onCancelled(firebaseError);
                }
            }
        });
    }

    public static ChildEventListener onAdded(Query query, final MessageListener.onMessageAdded messageAdded) {

        return query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              if (dataSnapshot.exists()) {
                    messageAdded.onChildAdded(dataSnapshot, s);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              if (dataSnapshot.exists()) {
                    messageAdded.onChildChanged(dataSnapshot, s);
               }
            }

            //TODO check if data exist
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messageAdded.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                messageAdded.onChildMoved(dataSnapshot, s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                messageAdded.onCancelled(firebaseError);
            }
        });
    }

    public static void onLoaded(@NonNull Query ref, final MessageListener.onMessageChange messageChange) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageChange.onDataChange(dataSnapshot);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                messageChange.onCancelled(firebaseError);

            }
        });
    }
}
