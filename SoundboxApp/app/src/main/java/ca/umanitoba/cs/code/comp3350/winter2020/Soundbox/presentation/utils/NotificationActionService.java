package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("TRACK_CONTROL")
                .putExtra("actionName", intent.getAction()));
    }

}