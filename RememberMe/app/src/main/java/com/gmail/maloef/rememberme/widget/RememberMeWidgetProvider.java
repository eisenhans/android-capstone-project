package com.gmail.maloef.rememberme.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RememberMeWidgetProvider extends AppWidgetProvider {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onEnabled(Context context) {
        logInfo("setting up widget");

        Intent intent = new Intent(context, getClass());
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC, 0, AlarmManager.INTERVAL_DAY, alarmIntent); // every day after midnight
//        alarmManager.setRepeating(AlarmManager.RTC, 0, 10000, alarmIntent); // every 10 sec (for testing only)
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
