package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Playlist;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private OnItemClickListener clickListener;
    private OnContextMenuClickListener contextMenuClickListener;
    private MainActivity mContext;
    private List<Playlist> playlists;


    public PlaylistAdapter(MainActivity mContext, List<Playlist> playlists) {
        this.mContext = mContext;
        this.playlists = playlists != null ? playlists : new ArrayList<Playlist>();

        this.clickListener = null;
        this.contextMenuClickListener = null;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.playlist_card, parent, false);
        return new PlaylistViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {
        final Playlist playlist = playlists.get(position);

        holder.title.setText(playlist.getName());
        holder.count.setText(playlist.songsSize() + " songs");

        Song thumbnailSong = playlist.getCoverSong();
        Bitmap bitmap = (thumbnailSong != null) ?
                AudioLoader.loadImageFromUri(mContext, Uri.parse(thumbnailSong.getFilepath())) :
                null;

        if (bitmap != null)
            holder.thumbnail.setImageBitmap(bitmap);
        else // loading playlist cover using Glide library
            Glide.with(mContext)
                    .load(playlist.getThumbnail())
                    .fallback(R.drawable.playlist1)
                    .into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, playlist, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener listener) {
        this.contextMenuClickListener = listener;
    }

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);

        notifyDataSetChanged();
    }

    public void addPlaylist(int index, Playlist playlist) {
        playlists.add(index, playlist);

        notifyDataSetChanged();
    }

    public void addAll(List<Playlist> playlists) {
        this.playlists.addAll(playlists);

        notifyDataSetChanged();
    }

    public void removePlaylist(int position) {
        playlists.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, playlists.size());
    }

    public void clearPlaylists() {
        playlists.clear();

        notifyDataSetChanged();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.count);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            overflow = itemView.findViewById(R.id.overflow);

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener == null) return;

                    int position = getAdapterPosition();
                    clickListener.onClick(view, playlists.get(position), position);
                }
            });
        }
    }


    //--------------------------------popup Remove/Play next---------------------------------

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, Playlist playlist, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_playlist_more, popup.getMenu());

        if (contextMenuClickListener != null) {
            Menu menu = popup.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                boolean filter = contextMenuClickListener.filterItem(item, playlist, position);

                item.setVisible(filter);
                item.setEnabled(filter);
            }
        }
        popup.setOnMenuItemClickListener(item -> {
            if (contextMenuClickListener == null) return false;

            contextMenuClickListener.onItemClick(item, playlist, position);
            return true;
        });

        popup.show();
    }

    //--------------------------------popup Remove/Play next---------------------------------

    public interface OnItemClickListener {
        void onClick(View view, Playlist playlist, int position);//pass your object types.
    }

    public interface OnContextMenuClickListener {
        void onItemClick(MenuItem menuItem, Playlist playlist, int position);

        boolean filterItem(MenuItem menuItem, Playlist playlist, int position);
    }

}