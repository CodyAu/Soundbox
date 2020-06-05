package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;

public final class Matchers {

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static <T> Matcher<T> firstDisplayed(@NonNull final Matcher<T> itemMatcher){
        checkNotNull(itemMatcher);

        return new BaseMatcher<T>() {
            private boolean isFirst = true;
            private Object object;

            @Override
            public void describeTo(Description description) {
                description.appendText("Returns the first displayed item: ");
                itemMatcher.describeTo(description);
            }

            @Override
            public boolean matches(final Object item) {
                if((isFirst || item.equals(object)) && itemMatcher.matches(item)){
                    isFirst = false;
                    object = item;
                    return true;
                }
                return false;
            }
        };
    }

    /**
     *
     * @param resourceId pass 0 if you want to check for no drawable background
     * @return
     */
    public static Matcher<View> withBackgroundDrawable(@IdRes final int resourceId) {
        return new TypeSafeMatcher<View>() {
            private Resources resources;

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id " + resourceId);
                if (resources != null)
                    description.appendText(resources.getResourceName(resourceId));
                description.appendText(": ");
            }

            @Override
            protected boolean matchesSafely(View item) {
                if (resourceId <= 0)
                    return item.getBackground() == null;

                resources = item.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(resourceId);

                if (expectedDrawable == null) return false;

                Bitmap bitmap = getBitmap(item.getBackground());
                Bitmap otherBitmap = getBitmap(expectedDrawable);

                return bitmap.sameAs(otherBitmap);
            }

            private Bitmap getBitmap(Drawable drawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        };
    }

    public static Matcher<View> withProgress(final int expectedProgress) {
        return withProgress(expectedProgress, 0);
    }

    public static Matcher<View> withProgress(final int expectedProgress, final int threshold) {
        return new BoundedMatcher<View, SeekBar>(SeekBar.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("expected " + expectedProgress + ": ");
            }

            @Override
            public boolean matchesSafely(SeekBar seekBar) {
                return seekBar.getProgress() <= expectedProgress + threshold &&
                        seekBar.getProgress() >= expectedProgress - threshold;
            }
        };
    }

    public static Matcher<RecyclerView.ViewHolder> withViewHolder(@NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);

        return new BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has ViewHolder: ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView.ViewHolder viewHolder) {
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

}
