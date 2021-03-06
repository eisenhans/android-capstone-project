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

public class UpdateWidgetIntentService extends IntentService {

    public UpdateWidgetIntentService() {
        super(UpdateWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RememberMeWidgetProvider.class));

        if (appWidgetIds.length == 0) {
            return;
        }

        WordRepository wordRepository = new WordRepository(getApplicationContext());
        int wordsDue = wordRepository.countWordsToRepeat();

        for (int appWidgetId : appWidgetIds) {
            logInfo("updating widget with id " + appWidgetId);
            updateWidget(appWidgetManager, appWidgetId, wordsDue);
        }
    }

    private void updateWidget(AppWidgetManager appWidgetManager, int appWidgetId, int wordsDue) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);

        String text;
        int imageResId;
        int contentDescriptionResId;
        if (wordsDue == 0) {
            text = getResources().getString(R.string.no_words_to_repeat);
            imageResId = R.mipmap.ic_launcher_green;
            contentDescriptionResId = R.string.green_icon;
        } else if (wordsDue == 1) {
            text = getResources().getString(R.string.one_word_to_repeat);
            imageResId = R.mipmap.ic_launcher;
            contentDescriptionResId = R.string.red_icon;
        } else {
            text = getResources().getString(R.string.i_words_to_repeat, wordsDue);
            imageResId = R.mipmap.ic_launcher;
            contentDescriptionResId = R.string.red_icon;
        }
        views.setTextViewText(R.id.words_to_repeat_textview, text);
        views.setContentDescription(R.id.words_to_repeat_textview, text);
        views.setImageViewResource(R.id.widget_icon, imageResId);
        views.setContentDescription(R.id.widget_icon, getString(contentDescriptionResId));

        Intent launchIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

        logInfo("updating widget " + appWidgetId + ": " + text);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
