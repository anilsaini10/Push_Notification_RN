/*
 * [ADMHelper.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */
package com.amazon.sample.admmessenger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * The ADMHelper class ia a helper class.
 *
 * @version Revision: 1, Date: 11/20/2019
 */
public class ADMHelper {

    public static final String ADM_CLASSNAME = "com.amazon.device.messaging.ADM";
    public static final String ADMV2_HANDLER = "com.amazon.device.messaging.ADMMessageHandlerJobBase";
    public static final int JOB_ID = 1324124;
    public static final String CHANNEL_ID = "notification_channel";
    public static final String CHANNEL_NAME = "Notification Channel";

    public static final boolean IS_ADM_AVAILABLE;
    public static final boolean IS_ADM_V2;

    private static boolean isClassAvailable(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static {
        IS_ADM_AVAILABLE = isClassAvailable(ADM_CLASSNAME);
        IS_ADM_V2 = IS_ADM_AVAILABLE ? isClassAvailable(ADMV2_HANDLER) : false;
    }

    /**
     * This method posts a notification to notification manager.
     *
     * @param msgKey String to access message field from data JSON.
     * @param timeKey String to access timeStamp field from data JSON.
     * @param intentAction Intent action that will be triggered in onMessage() callback.
     * @param msg Message that is included in the ADM message.
     * @param time Timestamp of the ADM message.
     */
    public static void createADMNotification(final Context context, final String msgKey, final String timeKey,
                                             final String intentAction, final String msg, final String time)
    {

        /* Clicking the notification should bring up the MainActivity. */
        /* Intent FLAGS prevent opening multiple instances of MainActivity. */
        final Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(msgKey, msg);
        notificationIntent.putExtra(timeKey, time);

        /* Android reuses intents that have the same action. Adding a time stamp to the action ensures that */
        /* the notification intent received in onResume() isn't one that was recycled and that may hold old extras. */
        notificationIntent.setAction(intentAction + time);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL);

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("ADM Message Received!")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.iv_notification_image)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else {
            builder = new Notification.Builder(context)
                    .setContentTitle("ADM Message Received!")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.iv_notification_image)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(context.getResources().getInteger(R.integer.sample_app_notification_id), notification);
    }

}
