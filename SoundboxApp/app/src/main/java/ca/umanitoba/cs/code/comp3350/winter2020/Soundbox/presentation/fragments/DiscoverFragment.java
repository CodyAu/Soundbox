package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Recommendation;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.RecListAdapter;


/**
 * Class: DiscocerActivity.java
 * Discover activity provides recommended songs for the users
 * it is connected with fragment_discover.xml which is its content view
 */

public class DiscoverFragment extends BottomNavigationTab {
    private MainActivity mainActivity;

    private Toolbar toolbar;

    private ListView listView;
    private RecListAdapter recListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity.changeNavigationView(3);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        listView = view.findViewById(R.id.recommend_list_view);
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(),
                new Recommendation(),
                new Recommendation());
        recListAdapter = new RecListAdapter(mainActivity, recs);
        listView.setAdapter((RecListAdapter) recListAdapter);

        initToolbar();
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.title_discover));
        mainActivity.setSupportActionBar(toolbar);
    }

    @Override
    public void onDuplicateTap() {
        listView.smoothScrollToPosition(0);
    }

}
