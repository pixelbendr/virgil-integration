package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.ui.listeners.TokenListener;

public class MessageToken {

    public static Boolean IS_VALIDATING = false;
    public static Boolean IS_TOKEN = false;

    //TODO refactor to remove listener on logout
    //TODO to prevent leaks
    public static void onChange(final TokenListener.onTokenChange onTokenChange) {
        new Firebase(Config.getFirebaseURL())
                .addAuthStateListener(new Firebase.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(AuthData authData) {
                        if (authData != null) {
                            onTokenChange.valid();
                            IS_TOKEN = true;
                        } else {
                            onTokenChange.invalid();
                            IS_TOKEN = false;
                        }
                    }
                });
    }

    public static void invalidate() {
        new Firebase(Config.getFirebaseURL()).unauth();
    }

    public static void validate(String token) {
        if (!IS_VALIDATING) {
            IS_VALIDATING = true;
            new Firebase(Config.getFirebaseURL())
                    .authWithCustomToken(token, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticationError(FirebaseError error) {
                            IS_VALIDATING = false;
                            IS_TOKEN = false;
                        }

                        @Override
                        public void onAuthenticated(AuthData authData) {
                            IS_VALIDATING = false;
                            IS_TOKEN = true;
                        }
                    });
        }
    }
}

