package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SoundboxObserver;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.ItemTouchHelperCallback;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters.TracklistAdapter;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Dispatchers;

/**
 * Class: PlayingFragment.java
 * Playing activity provides currently playing song list
 * it is connected with fragment_playing.xml which is its content view
 */
public class PlayingFragment extends BottomNavigationTab {
    private MainActivity mainActivity;

    private SoundboxObserver<Boolean> shuffleObserver;
    private SoundboxObserver<Song> songObserver;
    private SoundboxObserver<Song> addSongObserver;
    private SoundboxObserver<Song> deleteSongObserver;
    private SoundboxObserver<int[]> swapSongObserver;

    private Toolbar toolbar;

    private View emptyQueue;

    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;
    private TracklistAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        shuffleObserver = (observer, arg) -> updateShuffle(observer.getValue());
        songObserver = (observer, arg) -> updateSong(observer.getValue());
        addSongObserver = (observer, arg) -> updateAddSong(observer.getValue());
        deleteSongObserver = (observer, arg) -> updateDeleteSong(observer.getValue());
        swapSongObserver = (observer, arg) -> updateSwapSong(observer.getValue());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity.changeNavigationView(1);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        emptyQueue = view.findViewById(R.id.empty_queue);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.START | ItemTouchHelper.END);
        callback.setItemViewSwipeEnabled(true);
        callback.setOnMoveListener(new ItemTouchHelperCallback.OnMoveListener() {
            @Override
            public void onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = source.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                PlaybackController.swapSongPlaybackQueue(fromPosition, toPosition);
            }
        });
        callback.setOnSwipedListener(new ItemTouchHelperCallback.OnSwipedListener() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Dispatchers.information(mainActivity, "Removed");

                PlaybackController.deletePlaybackQueue(viewHolder.getAdapterPosition());
            }
        });
        callback.setOnClearListener(new ItemTouchHelperCallback.OnClearListener() {
            @Override
            public void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                updateHighlighted(PlaybackController.getPlaybackQueue().getIndex());
            }
        });
        itemTouchHelper = new ItemTouchHelper(callback);

        adapter = new TracklistAdapter(mainActivity, new SongCollection(PlaybackController.getPlaybackQueue().getQueue()));
        adapter.setOnClickListener(new TracklistAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, Song song, int position) {
                PlaybackController.setSongByIndex(position);
                PlaybackController.play();
            }

            @Override
            public void onItemLongClick(View view, Song song, int position) {
                // Pass
            }
        });
        adapter.setOnStartDragListener(new TracklistAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(TracklistAdapter.SongViewHolder holder, View view, Song song, int position) {
                itemTouchHelper.startDrag(holder);
            }
        });
        adapter.setOnContextMenuClickListener(new TracklistAdapter.OnContextMenuClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem, Song song, int position) {
                switch (menuItem.getItemId()) {
                    case R.id.action_remove:
                        Dispatchers.information(mainActivity, "Removed");

                        PlaybackController.deletePlaybackQueue(position);
                        break;
                    case R.id.action_details:
                        DetailsFragment.openDialog(mainActivity, song);
                        break;
                    case R.id.action_play_next:
                        Dispatchers.information(mainActivity, "Play next");

                        PlaybackController.playNext(position);
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
                return true;
            }
        });
        adapter.setOnCreateRowListener(new TracklistAdapter.OnCreateRowListener() {
            @Override
            public void onCreateRow(TracklistAdapter.SongViewHolder holder, View view, int viewType) {
                holder.add.setVisibility(View.GONE);
            }
        });

        recyclerView = view.findViewById(R.id.tracklist_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter((RecyclerView.Adapter) adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        initToolbar();
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.title_playing));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_clear_queue:
                        PlaybackController.clearPlaybackQueue();
                        return true;
                    case R.id.action_jump_playing:
                        if (!PlaybackController.getPlaybackQueue().isEmpty())
                            recyclerView.smoothScrollToPosition(PlaybackController.getPlaybackQueue().getIndex());
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Attempt to set optional icons to true by casting Menu -> MenuBuilder
        try {
            MenuBuilder menuBuilder = (MenuBuilder) toolbar.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
        } catch (ClassCastException e) {
            Log.e(PlaybackControlsFragment.class.getName(), PlayingFragment.class.getName() + ": Could not set optional icons visible.");
        }

        toolbar.inflateMenu(R.menu.menu_playing);
    }

    private void registerObservers() {
        Observables.getPlaybackShuffleObservable().addObserver(shuffleObserver);
        Observables.getPlaybackSongObservable().addObserver(songObserver);
        Observables.getAddSongPlaybackQueueObservable().addObserver(addSongObserver);
        Observables.getDeleteSongPlaybackQueueObservable().addObserver(deleteSongObserver);
        Observables.getSwapSongPlaybackQueueObservable().addObserver(swapSongObserver);
    }

    private void unregisterObservers() {
        Observables.getPlaybackShuffleObservable().deleteObserver(shuffleObserver);
        Observables.getPlaybackSongObservable().deleteObserver(songObserver);
        Observables.getAddSongPlaybackQueueObservable().deleteObserver(addSongObserver);
        Observables.getDeleteSongPlaybackQueueObservable().deleteObserver(deleteSongObserver);
        Observables.getSwapSongPlaybackQueueObservable().deleteObserver(swapSongObserver);
    }

    private void updateShuffle(boolean shuffle) {
        updateList();
    }

    public void updateSong(Song song) {
        updateHighlighted(PlaybackController.getPlaybackQueue().getIndex());
    }

    public void updateAddSong(Song song) {
        updateList();
    }

    public void updateDeleteSong(Song song) {
        updateList();
    }

    public void updateSwapSong(int[] positions) {
        adapter.notifyItemMoved(positions[0], positions[1]);
    }

    public void updateHighlighted(int position) {
        adapter.setHighlightedItem(position);
    }

    public void updateList() {
        if (PlaybackController.getPlaybackQueue().isEmpty() && emptyQueue.getVisibility() != View.VISIBLE)
            emptyQueue.setVisibility(View.VISIBLE);
        else if (!PlaybackController.getPlaybackQueue().isEmpty() && emptyQueue.getVisibility() != View.GONE)
            emptyQueue.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
        updateHighlighted(PlaybackController.getPlaybackQueue().getIndex());
    }

    @Override
    public void onDuplicateTap() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager.isSmoothScrolling()) {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateList();
        registerObservers();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterObservers();
    }

    @Override
    public void onDestroy() {
        unregisterObservers();

        super.onDestroy();
    }

}
