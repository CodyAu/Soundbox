package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class NotificationId {
    private final static AtomicInteger id = new AtomicInteger(1);

    public static int getID() {
        return id.incrementAndGet();
    }

}
