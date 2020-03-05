package com.nazmul.finalyearproject.Utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.nazmul.finalyearproject.R;

public class NotificationHelper extends ContextWrapper {

    private static final String EDMT_CHANNEL_ID = "com.nazmul.finalyearproject";
    private static final String EDMT_CHANNEL_NAME = "Realtime2019";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel edmtchannel = new NotificationChannel(EDMT_CHANNEL_ID,
                EDMT_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        edmtchannel.enableLights(false);
        edmtchannel.enableVibration(true);
        edmtchannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(edmtchannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNewRealTrackingTimeNotification(String title, String content, Uri defaultSound) {

        return new Notification.Builder(getApplicationContext(),EDMT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);

    }
}
