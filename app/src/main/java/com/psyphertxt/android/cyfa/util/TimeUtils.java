package com.psyphertxt.android.cyfa.util;

import android.text.format.DateUtils;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static void scheduleTask(final long delayInSeconds, final CallbackListener.completion completion) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                completion.done();
            }
        }, delayInSeconds * 1000);
    }

    public static void countDown(final Long targetNumber, final CallbackListener.timer timer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long counter = targetNumber;
                    boolean keepCounting = true;
                    while (keepCounting) {
                        counter = counter - 1;
                        Thread.sleep(1000);
                        if (counter == 0) {
                            keepCounting = false;
                            timer.running(counter);
                            timer.done();
                        } else {
                            timer.running(counter);
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String formatTime(Long time) {

        //outputs example FRI 06:44 PM
        DateFormat dateFormat = new SimpleDateFormat("EEE, KK:mm a");

        if (time != null) {
            return dateFormat.format(time);
        }

        return Config.EMPTY_STRING;
    }

    public static String xAgo(long time) {

        return DateUtils.getRelativeTimeSpanString(
                time,
                new Date().getTime(),
                DateUtils.SECOND_IN_MILLIS).toString();

    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "JUST NOW";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A MINUTE AGO";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "M AGO";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "AN HOUR AGO";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "H AGO";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "YESTERDAY";
        } else {
            return diff / DAY_MILLIS + " DAYS AGO";
        }
    }
}
