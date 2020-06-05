package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;

public class Dispatchers {
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;

    public static void alertError(final Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.error));
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public static void alertWarning(final Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.warning));
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public static void alertInformation(final Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    /**
     * @param context  contexts
     * @param message  The message to display
     * @param duration Should either be 0 or 1. Can use Dispatchers.LENGTH_LONG or Dispatchers.LENGTH_SHORT
     */
    public static void information(final Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void information(final Context context, String message) {
        Toast.makeText(context, message, LENGTH_SHORT).show();
    }

    public static void notification(final Context context, String title, String content, int icon) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Notification notification =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build();

        notificationManagerCompat.notify(NotificationId.getID(), notification);
    }

    public static void mediaNotification(final Context context, String title, String content, Bitmap largeIcon, int smallIcon, boolean sticky, NotificationCompat.Action[] actions) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "DISPATCHERS");
        MediaControllerCompat controller = mediaSessionCompat.getController();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_id))
                        .setSmallIcon(smallIcon)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(controller.getSessionActivity())
                        .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true) //show notification for only first time
                        .setShowWhen(false)
                        .setOngoing(sticky)
                        .setAutoCancel(false)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSessionCompat.getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2)
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        for (int i = 0; i < actions.length; i++) { //have 3 compat slots
            builder.addAction(actions[i]);
        }
        Notification notification = builder.build();

        notificationManagerCompat.notify(1, notification); //use 1 as the notification id. Only allow one sticky
    }

    public static void mediaNotification(final Context context, String title, String content, Bitmap largeIcon, int smallIcon, NotificationCompat.Action[] actions){
        mediaNotification(context, title, content, largeIcon, smallIcon, true, actions);
    }

    public static void mediaNotification(final Context context, String title, String content, int smallIcon, boolean sticky, NotificationCompat.Action[] actions){
        mediaNotification(context, title, content, null, smallIcon, sticky, actions);
    }

    public static void createNotificationChannel(final Context context, String channelId, String channelName, String channelDescription, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
