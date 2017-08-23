package com.mrerror.tm.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mrerror.tm.Inbox;
import com.mrerror.tm.MainActivity;
import com.mrerror.tm.R;

import java.util.Map;

/**
 * Created by kareem on 8/1/2017.
 */

public class MyMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // Check if message contains a data payload.

        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {

            // Send a notification that you got a new message
            sendNotification(data);

        }
    }
    private void sendNotification(Map<String, String> data) {

          Intent intent = new Intent(this, MainActivity.class);

      String mWhere=data.get("where");
        if(mWhere.equals("question")){
             intent = new Intent(this, Inbox.class);

        }else if(mWhere.equals("news")){
             intent = new Intent(this, MainActivity.class);
            intent.putExtra("where",data.get("where"));


        }else if(mWhere.equals("answers")) {
             intent = new Intent(this, MainActivity.class);

            intent.putExtra("where", data.get("where"));
        }

        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String message="";
        String message_title="";

        if(data.keySet().contains("message_body")){
            message=data.get("message_body");
        }else {message="New notification";}

        if(data.keySet().contains("message_title")){
            message_title =data.get("message_title");

        }else {message_title="Mr.TAWFIK";}


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_school_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(message))
                .setContentTitle(message_title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
