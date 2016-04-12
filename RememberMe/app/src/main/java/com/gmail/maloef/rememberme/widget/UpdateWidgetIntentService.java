package com.gmail.maloef.rememberme.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.activity.main.MainActivity;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UpdateWidgetIntentService extends IntentService {

    public UpdateWidgetIntentService() {
        super(UpdateWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RememberMeWidgetProvider.class));

        WordRepository wordRepository = new WordRepository(getApplicationContext());
        int wordsDue = wordRepository.countWordsDue();

        for (int appWidgetId : appWidgetIds) {
            logInfo("updating widget with id " + appWidgetId);
            updateWidget(appWidgetManager, appWidgetId, wordsDue);
        }
    }

    private void updateWidget(AppWidgetManager appWidgetManager, int appWidgetId, int wordsDue) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);

        DateTime now = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
        String text;
        int imageResId;
        if (wordsDue == 0) {
            text = getResources().getString(R.string.no_words_due);
            imageResId = R.mipmap.widget_green_letter;
        } else if (wordsDue == 1) {
            text = getResources().getString(R.string.one_word_due);
            imageResId = R.mipmap.ic_launcher;
        } else {
            text = getResources().getString(R.string.i_words_due, wordsDue);
            imageResId = R.mipmap.ic_launcher;
        }
        views.setTextViewText(R.id.words_to_repeat_textview, text);
        views.setContentDescription(R.id.words_to_repeat_textview, text);
        views.setImageViewResource(R.id.widget_icon, imageResId);

        // Create an Intent to launch MainActivity
        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        logInfo("updating widget " + appWidgetId + ": " + text);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
