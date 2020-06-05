package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils;

import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.IdRes;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.util.HumanReadables;

import org.hamcrest.Matcher;

import java.util.ArrayList;

import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class ViewActions {

    public static ViewAction clickChildWithText(final String text) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return hasDescendant(withText(text));
            }

            @Override
            public String getDescription() {
                return "Click on a child view with text " + text + ": ";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ArrayList<View> views = new ArrayList<>();

                view.findViewsWithText(views, text, View.FIND_VIEWS_WITH_TEXT);
                if (views.size() > 0)
                    views.get(0).performClick();
            }
        };
    }

    public static ViewAction clickChildWithId(@IdRes final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return hasDescendant(withId(id));
            }

            @Override
            public String getDescription() {
                return "Click on a child view with id " + id + ": ";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    /**
     * Sets the progress of a SeekBar through the setProgress method.
     * Will not invoke the fromUser flag inside of SeekBar.OnSeekBarChangeListener.onProgressChanged
     *
     * @param progress
     * @return
     */
    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set the SeekNar progress to " + progress + ": ";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
        };
    }

    /**
     * Sets the progress of a SeekBar through a swiping action.
     * This will invoke the fromUser flag inside of SeekBar.OnSeekBarChangeListener.onProgressChanged
     *
     * @param progress
     * @return
     */
    public static ViewAction scrubProgress(int progress) {
        return actionWithAssertions(new GeneralSwipeAction(
                Swipe.FAST,
                new SeekBarThumbCoordinatesProvider(0),
                new SeekBarThumbCoordinatesProvider(progress),
                Press.PINPOINT));
    }

    private static class SeekBarThumbCoordinatesProvider implements CoordinatesProvider {
        private final int progress;

        public SeekBarThumbCoordinatesProvider(int progress) {
            this.progress = progress;
        }

        private static float[] getVisibleLeftTop(View view) {
            final int[] xy = new int[2];
            view.getLocationOnScreen(xy);
            return new float[]{(float) xy[0], (float) xy[1]};
        }

        @Override
        public float[] calculateCoordinates(View view) {
            if (!(view instanceof SeekBar)) {
                throw new PerformException.Builder()
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new RuntimeException(String.format("SeekBar expected"))).build();
            }
            SeekBar seekBar = (SeekBar) view;
            int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
            double progress = this.progress == 0 ? seekBar.getProgress() : this.progress;
            int xPosition = (int) (seekBar.getPaddingLeft() + width * progress / seekBar.getMax());
            float[] xy = getVisibleLeftTop(seekBar);
            return new float[]{xy[0] + xPosition, xy[1] + 10};
        }

    }

}
