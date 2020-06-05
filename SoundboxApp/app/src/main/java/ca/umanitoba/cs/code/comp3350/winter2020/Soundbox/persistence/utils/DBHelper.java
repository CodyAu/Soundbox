package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PersistenceSession;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

public class DBHelper {

    public static void copyDatabaseToDevice(Context context) {
        String[] assetNames;
        File dataDirectory = PersistenceSession.getDatabaseDirectory(context);
        AssetManager assetManager = context.getAssets();

        try {
            assetNames = assetManager.list(PersistenceSession.getDatabasePath());
            for (int i = 0; i < assetNames.length; i++) {
                assetNames[i] = PersistenceSession.getDatabaseAssetPath() + "/" + assetNames[i];
            }

            copyAssetsToDirectory(context, assetNames, dataDirectory);
            PersistenceSession.setDatabasePath(getWritePath(dataDirectory));

        } catch (final IOException ioe) {
            Dispatchers.alertWarning(context, "Unable to access application data: " + ioe.getMessage());
        }
    }

    public static String getWritePath(File dataDirectory){
        return dataDirectory.getAbsolutePath() + "/" + PersistenceSession.getDatabaseName();
    }

    private static void copyAssetsToDirectory(Context context, String[] assets, File directory) throws IOException {
        AssetManager assetManager = context.getAssets();

        for (String asset : assets) {
            String[] components = asset.split("/");
            String copyPath = directory.toString() + "/" + components[components.length - 1];

            char[] buffer = new char[1024];
            int count;

            File outFile = new File(copyPath);

            if (!outFile.exists()) {
                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                FileWriter out = new FileWriter(outFile);

                count = in.read(buffer);
                while (count != -1) {
                    out.write(buffer, 0, count);
                    count = in.read(buffer);
                }

                out.close();
                in.close();
            }
        }
    }

}
