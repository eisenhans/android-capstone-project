package com.gmail.maloef.rememberme.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.activity.main.MainActivity;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxRepository;
import com.gmail.maloef.rememberme.persistence.WordRepository;

import java.util.Map;

public class SendNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WordRepository wordRepository = new WordRepository(context);
        Map<Integer, Integer> wordsToRepeatByBox = wordRepository.countWordsToRepeatByBox();
        if (wordsToRepeatByBox.isEmpty()) {
            return;
        }

        int total = 0;
        for (int wordsInBox : wordsToRepeatByBox.values()) {
            total += wordsInBox;
        }

        String title;
        if (total == 1) {
            title = context.getString(R.string.one_word_to_repeat);
        } else {
            title = context.getString(R.string.n_words_to_repeat, total);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title);

        if (wordsToRepeatByBox.size() > 1) {
            builder.setContentText(createText(context, wordsToRepeatByBox));
        }

        Intent rememberMeIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(rememberMeIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
    }

    private String createText(Context context, Map<Integer, Integer> wordsToRepeatByBox) {
        VocabularyBoxRepository boxRepository = new VocabularyBoxRepository(context);

        String message = "";
        for (int boxId : wordsToRepeatByBox.keySet()) {
            String boxName = boxRepository.findBox(boxId).name;
            int wordsToRepeat = wordsToRepeatByBox.get(boxId);
            if (!message.isEmpty()) {
                message += ", ";
            }
            message += context.getString(R.string.n_from_box_s, wordsToRepeat, boxName);
        }
        return message;
    }
}
