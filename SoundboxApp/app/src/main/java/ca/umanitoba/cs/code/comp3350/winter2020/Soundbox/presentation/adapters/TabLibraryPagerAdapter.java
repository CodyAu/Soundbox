package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.DetailsFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.LibraryFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments.TracklistFragment;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

public class TabLibraryPagerAdapter extends FragmentStateAdapter {
    private List<List<SongCollection>> songCollections;
    private SongCollection songs;

    public TabLibraryPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TabLibraryFragment tabLibraryFragment = new TabLibraryFragment();
        List<SongCollection> collection = position < this.songCollections.size() ? this.songCollections.get(position) : null;
        tabLibraryFragment.setData(collection, this.songs, position);

        return tabLibraryFragment;
    }

    public void setData(List<List<SongCollection>> songCollections, SongCollection songs) {
        this.songCollections = songCollections != null ? songCollections : new ArrayList<List<SongCollection>>();
        this.songs = songs != null ? songs : new SongCollection();
    }

    @Override
    public int getItemCount() {
        return songCollections.size() + 1; // +1 for the songs tab
    }

    public static class TabLibraryFragment extends Fragment {
        private MainActivity mainActivity;

        private RecyclerView recyclerView;
        private TracklistAdapter tracklistAdapter;
        private TracklistCollectionAdapter tracklistCollectionAdapter;

        private int tabPosition;

        private LibraryFragment.OnDuplicateTapListener onDuplicateTapListener;
        private LibraryFragment.OnTabsChangedListener onTabsChangedListener;

        private List<SongCollection> collections;
        private SongCollection songs;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mainActivity = (MainActivity) getActivity();

            LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
            onDuplicateTapListener = () -> scrollToTop();
            onTabsChangedListener = (tabChanged) -> {
                if (tabChanged == tabPosition || tabChanged < 0)
                    updateList();
            };
            libraryFragment.addOnDuplicateTapListener(onDuplicateTapListener);
            libraryFragment.addOnTabsChangedListener(onTabsChangedListener);

            if (collections != null)
                tracklistCollectionAdapter = new TracklistCollectionAdapter(mainActivity, collections);
            else tracklistAdapter = new TracklistAdapter(mainActivity, songs);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.content_library_tab, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            if (collections != null) recyclerView = view.findViewById(R.id.recycler_view);
            else recyclerView = view.findViewById(R.id.tracklist_recycle_view);

            if (collections != null) {
                tracklistCollectionAdapter.setOnItemClickListener(new TracklistCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, SongCollection songCollection, int position) {
                        Bundle bundle = new Bundle();
                        long[] songIds = new long[songCollection.songsSize()];
                        for (int i = 0; i < songIds.length; i++)
                            songIds[i] = songCollection.getSongs().get(i).getSongId();
                        bundle.putLongArray(TracklistFragment.SONGS_ARG, songIds);
                        bundle.putString(TracklistFragment.TITLE_ARG, songCollection.getName());

                        mainActivity.switchToTracklistFragment(true, bundle);
                    }
                });
                tracklistCollectionAdapter.setOnContextMenuClickListener(new TracklistCollectionAdapter.OnContextMenuClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem, SongCollection songCollection, int position) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_play_next:
                                Dispatchers.information(mainActivity, "Play next");

                                PlaybackController.playNext(songCollection.getSongs());
                                break;
                            case R.id.action_add_queue:
                                Dispatchers.information(mainActivity, "Added to queue");

                                PlaybackController.addPlaybackQueue(songCollection.getSongs());
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public boolean filterItem(MenuItem menuItem, SongCollection collection, int position) {
                        return true;
                    }
                });

                recyclerView.setLayoutManager(new GridLayoutManager(mainActivity, 2));
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, Calculate.dpToPx(mainActivity, 10), true));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(tracklistCollectionAdapter);
            } else {
                tracklistAdapter.setOnClickListener(new TracklistAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(View view, Song song, int position) {
                        PlaybackController.setSong(song);
                        PlaybackController.play();
                    }

                    @Override
                    public void onItemLongClick(View view, Song song, int position) {
                        // Pass
                    }
                });
                tracklistAdapter.setOnContextMenuClickListener(new TracklistAdapter.OnContextMenuClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem, Song song, int position) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_details:
                                DetailsFragment.openDialog(mainActivity, song);
                                break;
                            case R.id.action_play_next:
                                Dispatchers.information(mainActivity, "Play next");

                                PlaybackController.playNext(song);
                                break;
                            case R.id.action_add_queue:
                                Dispatchers.information(mainActivity, "Added to queue");

                                PlaybackController.addPlaybackQueue(song);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public boolean filterItem(MenuItem menuItem, Song song, int position) {
                        return menuItem.getItemId() != R.id.action_remove;
                    }
                });
                tracklistAdapter.setOnCreateRowListener(new TracklistAdapter.OnCreateRowListener() {
                    @Override
                    public void onCreateRow(TracklistAdapter.SongViewHolder holder, View view, int viewType) {
                        holder.add.setVisibility(View.GONE);
                        holder.drag.setVisibility(View.GONE);
                    }
                });

                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(tracklistAdapter);
            }
        }

        public void setData(List<SongCollection> collections, SongCollection songs, int tabPosition) {
            this.collections = collections;
            this.songs = songs;
            this.tabPosition = tabPosition;

            updateList();
        }

        public void updateList() {
            if (tracklistAdapter != null) tracklistAdapter.notifyDataSetChanged();
            else if (tracklistCollectionAdapter != null)
                tracklistCollectionAdapter.notifyDataSetChanged();
        }

        public void scrollToTop() {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (linearLayoutManager.isSmoothScrolling()) {
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onDestroy() {
            LibraryFragment libraryFragment = (LibraryFragment) getParentFragment();
            libraryFragment.removeOnDuplicateTapListener(onDuplicateTapListener);
            libraryFragment.removeOnTabsChangedListener(onTabsChangedListener);

            super.onDestroy();
        }

        /**
         * RecyclerView item decoration - give equal margin around grid item
         */
        public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
            private int spanCount;
            private int spacing;
            private boolean includeEdge;

            public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
                this.spanCount = spanCount;
                this.spacing = spacing;
                this.includeEdge = includeEdge;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount; // item column

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        }

    }

}
