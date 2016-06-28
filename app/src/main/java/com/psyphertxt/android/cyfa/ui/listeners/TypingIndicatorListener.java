package com.psyphertxt.android.cyfa.ui.listeners;

public class TypingIndicatorListener {

    public static final Integer FINISHED = 0;
    public static final Integer STARTED = 1;
    public static final Integer LIVE = 2;
    public static final Integer PAUSED = 3;

    public interface onTypingIndicator {

        void startedTyping();

        void liveTyping(String s);

        void finishedTyping();

    }
}

