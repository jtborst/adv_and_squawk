package android.example.com.squawker.fcm;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkNotificationService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "SQUAWK_MESSAGE_CHANNEL";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> messageData = remoteMessage.getData();

        if (messageData != null) {
            String messagePreview;
            ContentValues messageValues = new ContentValues();

            messageValues.put(SquawkContract.COLUMN_MESSAGE, messageData.get(SquawkContract.COLUMN_MESSAGE));
            if (messageValues.get(SquawkContract.COLUMN_MESSAGE) != null) {
                messagePreview = messageValues.getAsString(SquawkContract.COLUMN_MESSAGE).substring(0, 30);
            } else {
                messagePreview = "Tap to see the message";
            }

            String notificationTitle;
            messageValues.put(SquawkContract.COLUMN_AUTHOR, messageData.get(SquawkContract.COLUMN_AUTHOR));
            if (messageValues.get(SquawkContract.COLUMN_AUTHOR) == null) {
                notificationTitle = "New message";
            } else {
                notificationTitle = "Message from " + messageValues.getAsString(SquawkContract.COLUMN_AUTHOR);
            }


            messageValues.put(SquawkContract.COLUMN_DATE, messageData.get(SquawkContract.COLUMN_DATE));
            messageValues.put(SquawkContract.COLUMN_AUTHOR_KEY, messageData.get(SquawkContract.COLUMN_AUTHOR_KEY));

            getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, messageValues);


            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);



            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(notificationTitle)
                    .setContentText(messagePreview)
                    .setSmallIcon(R.drawable.ic_duck)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, mBuilder.build());
        }

        super.onMessageReceived(remoteMessage);
    }
}
