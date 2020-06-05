package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Themes;

public class TracklistAdapter extends RecyclerView.Adapter<TracklistAdapter.SongViewHolder> {
    private Context context;
    private OnClickListener clickListener;
    private OnStartDragListener dragListener;
    private OnContextMenuClickListener contextMenuClickListener;
    private OnCreateRowListener createRowListener;

    private SongCollection songs;
    private SparseBooleanArray selectedItems;
    private int highlightedItem;

    public TracklistAdapter(@NonNull Context context, SongCollection songs) {
        this.context = context;
        this.songs = (songs != null ? songs : new SongCollection());

        this.clickListener = null;
        this.dragListener = null;
        this.contextMenuClickListener = null;
        this.selectedItems = new SparseBooleanArray();
        this.highlightedItem = -1;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.tracklist_row, parent, false);
        SongViewHolder holder = new SongViewHolder(row);

        if (createRowListener != null)
            createRowListener.onCreateRow(holder, row, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // Get the data item for this position
        final Song song = songs.getSong(position);

        // Populate the data into the template view using the data object
        holder.title.setText(song.getSongName());
        holder.artist.setText(song.getArtist());
        holder.duration.setText(Calculate.formatTime(song.getLength()));
        setDisplayImage(holder, song, position);
        setFavorite(holder, song, position);
        setHighlight(holder, song, position);

        holder.container.setActivated(selectedItems.get(position, false));
        holder.container.setOnClickListener(view -> {
            if (clickListener == null) return;

            clickListener.onItemClick(view, song, position);
        });
        holder.container.setOnLongClickListener(view -> {
            if (clickListener == null) return false;

            clickListener.onItemLongClick(view, song, position);
            return true;
        });
        holder.drag.setOnTouchListener((view, motionEvent) -> {
            if (dragListener == null) return false;

            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder, view, song, position);
                return true;
            }
            return false;
        });
        holder.favorite.setOnClickListener(view -> {
            // Toggle favorite visibility
            SongStatistic favoriteStatistic = song.getStatisticByType(SongStatistic.Statistic.LIKES);
            if (toggleFavorite(holder))
                favoriteStatistic.setValue(1);
            else
                favoriteStatistic.setValue(0);

            (new SongController()).updateSong(song);
            Observables.getFavoriteSongObservable().setValue(song);
        });
        holder.more.setOnClickListener(view -> showPopupMenu(holder.more, song, position));
    }

    private void setDisplayImage(SongViewHolder holder, Song song, int position) {
        if (selectedItems.get(position, false)) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        Bitmap bitmapImage = (song.hasThumbnail()) ? AudioLoader.loadImageFromUri(context, Uri.parse(song.getFilepath())) : null;

        if (bitmapImage != null) holder.imageView.setImageBitmap(bitmapImage);
        else Glide.with(context).load(R.drawable.playlist1).into(holder.imageView);
    }

    private void setFavorite(SongViewHolder holder, Song song, int position) {
        int value = song.getStatisticByType(SongStatistic.Statistic.LIKES).getValue();
        if (value > 0 && holder.favoriteFill.getVisibility() != View.VISIBLE)
            toggleFavorite(holder);
        else if (value <= 0 && holder.favoriteFill.getVisibility() != View.GONE)
            toggleFavorite(holder);
    }

    private boolean toggleFavorite(SongViewHolder holder) {
        if (holder.favoriteFill.getVisibility() != View.GONE) {
            holder.favoriteFill.setVisibility(View.GONE);
            return false;
        }

        holder.favoriteFill.setVisibility(View.VISIBLE);
        return true;
    }

    private void setHighlight(SongViewHolder holder, Song song, int position) {
        int color = position == highlightedItem ? Themes.getThemeColor(context, R.attr.themeTextColorHighlight) : Themes.getThemeColor(context, R.attr.themeTextColorPrimary);
        holder.title.setTextColor(color);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnStartDragListener(OnStartDragListener listener) {
        this.dragListener = listener;
    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener listener) {
        this.contextMenuClickListener = listener;
    }

    public void setOnCreateRowListener(OnCreateRowListener listener) {
        this.createRowListener = listener;
    }

    public SongCollection getSongs() {
        return songs;
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++)
            items.add(selectedItems.keyAt(i));
        return items;
    }

    public List<Song> getSelectedSongs() {
        List<Song> items = new ArrayList<Song>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++)
            items.add(songs.getSong(selectedItems.keyAt(i)));
        return items;
    }

    /**
     * Add new songs to its tracklist when user clicks the add button
     */
    public void addSong(Song song) {
        songs.insertSong(song);

        notifyDataSetChanged();
    }

    public void addAll(List<Song> songs) {
        this.songs.insertSongs(songs);

        notifyDataSetChanged();
    }

    /**
     * Replaces the current songs with the given songs preserving selection state.
     *
     * @param songs
     */
    public void setSongs(List<Song> songs) {
        List<Song> selected = getSelectedSongs();

        clear();
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            this.songs.insertSong(song);
            if (selected.contains(song))
                toggleSelection(i);
        }

        notifyDataSetChanged();
    }

    public void removeSong(int position) {
        songs.deleteSong(position);
        selectedItems.delete(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, songs.songsSize());
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }

        notifyItemChanged(position);
    }

    public void setHighlightedItem(int position) {
        if (highlightedItem != position) {
            int oldPosition = highlightedItem;
            highlightedItem = position;

            notifyItemChanged(position);
            notifyItemChanged(oldPosition);
        }
    }

    public void clear() {
        songs.clearSongs();
        clearSelections();
    }

    public void clearSelections() {
        selectedItems.clear();

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songs.songsSize();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, Song song, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_tracklist_more, popup.getMenu());

        if (contextMenuClickListener != null) {
            Menu menu = popup.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                boolean filter = contextMenuClickListener.filterItem(item, song, position);

                item.setVisible(filter);
                item.setEnabled(filter);
            }
        }
        popup.setOnMenuItemClickListener(item -> {
            if (contextMenuClickListener == null) return false;

            contextMenuClickListener.onItemClick(item, song, position);
            return true;
        });

        popup.show();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout container;
        public TextView title, artist, duration;
        public FrameLayout favorite;
        public ImageView imageView, checkBox, drag, add, favoriteBorder, favoriteFill, more;

        public SongViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.tracklist_container);

            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);

            checkBox = itemView.findViewById(R.id.check_box);
            imageView = itemView.findViewById(R.id.list_image);

            duration = itemView.findViewById(R.id.duration);

            favorite = itemView.findViewById(R.id.tracklist_row_favorite);

            drag = itemView.findViewById(R.id.tracklist_drag_handle);
            add = itemView.findViewById(R.id.tracklist_row_add);
            favoriteBorder = itemView.findViewById(R.id.tracklist_row_favorite_border);
            favoriteFill = itemView.findViewById(R.id.tracklist_row_favorite_fill);
            more = itemView.findViewById(R.id.tracklist_row_more);
        }

    }

    public interface OnCreateRowListener {
        void onCreateRow(SongViewHolder holder, View view, int viewType);
    }

    public interface OnClickListener {
        void onItemClick(View view, Song song, int position);

        void onItemLongClick(View view, Song song, int position);
    }

    public interface OnStartDragListener {
        void onStartDrag(SongViewHolder holder, View view, Song song, int position);
    }

    public interface OnContextMenuClickListener {
        void onItemClick(MenuItem menuItem, Song song, int position);

        boolean filterItem(MenuItem menuItem, Song song, int position);
    }

}