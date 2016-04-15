package com.gmail.maloef.rememberme.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;

public class RememberMeWidgetProvider extends AppWidgetProvider {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onEnabled(Context context) {
        logInfo("setting up widget");

        Intent intent = new Intent(context, getClass());
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        DateTime nextMidnight = new DateTime().withTimeAtStartOfDay().plusDays(1);

        // every day at midnight
        alarmManager.setInexactRepeating(AlarmManager.RTC, nextMidnight.getMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onDisabled(Context context) {
        if (alarmManager != null && alarmIntent != null) {
            logInfo("cancelling alarm for widget update because all widgets have been deleted, intent: " + alarmIntent);
            alarmManager.cancel(alarmIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        logInfo("updating widget because the date has changed");
        context.startService(new Intent(context, UpdateWidgetIntentService.class));
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
