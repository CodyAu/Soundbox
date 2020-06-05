package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.utils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAssertion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ViewAssertions {

    public static ViewAssertion recyclerViewItemCount(final int expectedCount) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        };
    }

}
