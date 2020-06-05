package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.test.espresso.AmbiguousViewMatcherException;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import org.hamcrest.Matcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.IsInstanceOf.any;

public final class Utils {
    private final static int WAIT_INTERVAL = 250;

    public static ViewInteraction waitForWithText(final String text, final int maxWaitTime) {
        return waitFor(withText(text), maxWaitTime);
    }

    public static ViewInteraction waitForWithText(@IdRes final int stringId, final int maxWaitTime) {
        return waitFor(withText(stringId), maxWaitTime);
    }

    public static ViewInteraction waitForWithId(@IdRes final int id, final int maxWaitTime) {
        return waitFor(withId(id), maxWaitTime);
    }

    public static ViewInteraction waitFor(final Matcher<View> itemMatcher, final int maxWaitTime){
        ViewInteraction element;
        int sleepCounter = 0;
        do {
            sleepFor(WAIT_INTERVAL);
            sleepCounter += WAIT_INTERVAL;

            element = onView(itemMatcher);
        } while (!exists(element) && sleepCounter < maxWaitTime);

        return element;
    }

    public static boolean exists(final ViewInteraction interaction) {
        try {
            interaction.perform(new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return any(View.class);
                }

                @Override
                public String getDescription() {
                    return "check for existence";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    // no op, if this is run, then the execution will continue after .perform(...)
                }
            });
            return true;
        } catch (AmbiguousViewMatcherException ex) {
            // if there's any interaction later with the same matcher, that'll fail anyway
            return true; // we found more than one
        } catch (NoMatchingViewException ex) {
            return false;
        } catch (NoMatchingRootException ex) {
            // optional depending on what you think "exists" means
            return false;
        }
    }

    public static void sleepFor(final int milliseconds) {
        final CountDownLatch signal = new CountDownLatch(1);

        try {
            signal.await(milliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

}
