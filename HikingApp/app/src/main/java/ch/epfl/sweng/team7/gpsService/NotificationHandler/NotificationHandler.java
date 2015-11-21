package ch.epfl.sweng.team7.gpsService.NotificationHandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ch.epfl.sweng.team7.hikingapp.MapActivity;
import ch.epfl.sweng.team7.hikingapp.R;

public class NotificationHandler {

    private static NotificationHandler instance = new NotificationHandler();

    private final String DEFAULT_NOTIFICATION_TITLE = "Tracking a hike";
    private final String DEFAULT_NOTIFICATION_CONTENT = "Click here to check on your progress";

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    public static NotificationHandler getInstance() {
        return instance;
    }

    public void setup(Context context) {
        mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(DEFAULT_NOTIFICATION_TITLE)
                        .setContentText(DEFAULT_NOTIFICATION_CONTENT);
        
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void display() {
        // mId allows you to update the notification later on.
        mNotificationManager.notify(R.id.feedback_notification, mBuilder.build());
    }

    public void display(String title, String content) {
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(R.id.feedback_notification, mBuilder.build());
    }

    public void hide() {
        mNotificationManager.cancel(R.id.feedback_notification);
    }

    private NotificationHandler() {
    }
}
