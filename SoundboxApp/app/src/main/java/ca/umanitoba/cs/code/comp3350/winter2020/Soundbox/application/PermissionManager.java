package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionManager {
    public static final int ACCESS_PERMISSIONS_REQUEST = 200;

    // returns permissions already obtained
    public static String[] obtainedPermissions(Activity activity, String[] permissions) {
        ArrayList<String> alreadyObtained = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            if (obtainedPermission(activity, permissions[i]))
                alreadyObtained.add(permissions[i]);
        }
        return alreadyObtained.toArray(new String[alreadyObtained.size()]);
    }

    public static void getPermissions(Activity activity, String[] permissions) {
        ArrayList<String> notObtained = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) { // Get the permissions not current obtained
            if (!obtainedPermission(activity, permissions[i]))
                notObtained.add(permissions[i]);
        }
        String[] tempNotObtained = notObtained.toArray(new String[notObtained.size()]);
        if (tempNotObtained.length > 0) // If there are permissions to get request them
            ActivityCompat.requestPermissions(activity, tempNotObtained, ACCESS_PERMISSIONS_REQUEST);
    }

    public static void getPermission(Activity activity, String permission) {
        if (!obtainedPermission(activity, permission)) {
            String[] tmp_permissions = {permission};
            ActivityCompat.requestPermissions(activity, tmp_permissions, ACCESS_PERMISSIONS_REQUEST);
        }
    }

    public static boolean obtainedPermission(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
