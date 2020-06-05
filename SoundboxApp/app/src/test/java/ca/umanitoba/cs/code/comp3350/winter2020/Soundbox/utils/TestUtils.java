package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.PersistenceSession;

public class TestUtils {
    private static final File DB_SRC = new File("src/main/assets/db/soundboxdb.script");

    public static File copyDB() throws IOException {
        final File target = File.createTempFile("temp-db", ".script");
        Files.copy(DB_SRC, target);
        PersistenceSession.setDatabasePath(target.getAbsolutePath().replace(".script", ""));
        return target;
    }

}
