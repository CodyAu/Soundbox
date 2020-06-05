package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.UserManager;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.utils.DBHelper;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

public class SoundboxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //check if user is subject to teleportations.
        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        if(userManager != null && userManager.isUserAGoat())
            Dispatchers.information(this, "User is an absolute goat");

        //remaking a notification channel with same values is safe
        Dispatchers.createNotificationChannel(
                this,
                getString(R.string.channel_id),
                getString(R.string.channel_name),
                getString(R.string.channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        DBHelper.copyDatabaseToDevice(this);
        PersistenceSession.shutdownDatabase(); // If we didn't shutdown properly last time
    }

}
