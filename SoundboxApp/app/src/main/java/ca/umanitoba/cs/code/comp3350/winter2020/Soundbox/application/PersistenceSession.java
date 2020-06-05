package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application;

import android.content.Context;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.utils.DBHelper;

public class PersistenceSession {
    private static final String DB_PATH = "db";
    private static final String DB_NAME = "soundboxdb";

    private static String dbPath = DB_PATH;
    private static boolean shouldShutdown = false;

    public static File getDatabaseDirectory(Context context) {
        return context.getDir(DB_PATH, Context.MODE_PRIVATE);
    }

    public static void setDatabasePath(final String name) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dbPath = name;
    }

    public static String getDatabasePath() {
        return dbPath;
    }

    public static String getDatabaseAssetPath() {
        return DB_PATH;
    }

    public static String getDatabaseName() {
        return DB_NAME;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + (shouldShutdown ? ";shutdown=true" : ";"), "SA", "");
    }

    public static void shutdownDatabase() {
        try (Connection connection = getConnection()) {
            connection.createStatement().execute("SHUTDOWN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void shutdownDatabase(Context context) {
        try (Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:" + DBHelper.getWritePath(getDatabaseDirectory(context)) + ";ifexists=true", "SA", "")) {
            connection.createStatement().execute("SHUTDOWN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
