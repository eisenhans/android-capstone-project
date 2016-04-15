package com.gmail.maloef.rememberme.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class NotificationHandler {

    static final int NOTIFICATION_ID = 1;

    private final Context context;

    @Inject
    public NotificationHandler(Context context) {
        this.context = context;
    }

    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void setUpAlarm() {
        Intent intent = new Intent(context, SendNotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        DateTime nextMidnight = new DateTime().withTimeAtStartOfDay().plusDays(1);

        // every day at midnight
        alarmManager.setInexactRepeating(AlarmManager.RTC, nextMidnight.getMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
