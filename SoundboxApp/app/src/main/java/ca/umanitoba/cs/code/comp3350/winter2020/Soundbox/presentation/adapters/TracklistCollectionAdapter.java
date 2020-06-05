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
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;

public class TracklistCollectionAdapter extends RecyclerView.Adapter<TracklistCollectionAdapter.SongCollectionHolder> {
    private OnItemClickListener clickListener;
    private OnContextMenuClickListener contextMenuClickListener;
    private MainActivity mContext;
    private List<SongCollection> songCollections;


    public TracklistCollectionAdapter(MainActivity mContext, List<SongCollection> songCollections) {
        this.mContext = mContext;
        this.songCollections = songCollections != null ? songCollections : new ArrayList<SongCollection>();

        this.clickListener = null;
    }

    @NonNull
    @Override
    public SongCollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.tracklist_collection_card, parent, false);
        return new SongCollectionHolder(row);
    }

    @Override
    public void onBindViewHolder(final SongCollectionHolder holder, final int position) {
        final SongCollection songCollection = songCollections.get(position);

        holder.title.setText(songCollection.getName());
        holder.count.setText(songCollection.songsSize() + " songs");

        Song thumbnailSong = songCollection.getCoverSong();
        Bitmap bitmap = (thumbnailSong != null) ?
                AudioLoader.loadImageFromUri(mContext, Uri.parse(thumbnailSong.getFilepath())) :
                null;

        if (bitmap != null)
            holder.thumbnail.setImageBitmap(bitmap);
        else // loading playlist cover using Glide library
            Glide.with(mContext)
                    .load(R.drawable.playlist1)
                    .into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, songCollection, position);
            }
        });
    }

    public void setOnItemClickListener(TracklistCollectionAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnContextMenuClickListener(TracklistCollectionAdapter.OnContextMenuClickListener listener) {
        this.contextMenuClickListener = listener;
    }

    public List<SongCollection> getSongCollections() {
        return Collections.unmodifiableList(songCollections);
    }

    @Override
    public int getItemCount() {
        return songCollections.size();
    }

    public void addSongCollection(SongCollection songCollection) {
        songCollections.add(songCollection);

        notifyDataSetChanged();
    }

    public void removeSongCollection(int position) {
        songCollections.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, songCollections.size());
    }

    public class SongCollectionHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public SongCollectionHolder(View itemView) {
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
                    clickListener.onClick(view, songCollections.get(position), position);
                }
            });
        }
    }

    private void showPopupMenu(View view, SongCollection collection, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_song_collection, popup.getMenu());

        if (contextMenuClickListener != null) {
            Menu menu = popup.getMenu();
            for (int i = 0; i < menu.size(); i++){
                MenuItem item = popup.getMenu().getItem(i);
                boolean filter = contextMenuClickListener.filterItem(item, collection, position);

                item.setVisible(filter);
                item.setEnabled(filter);
            }
        }
        popup.setOnMenuItemClickListener(item -> {
            if (contextMenuClickListener == null) return false;

            contextMenuClickListener.onItemClick(item, collection, position);
            return true;
        });

        popup.show();
    }

    public interface OnItemClickListener {
        void onClick(View view, SongCollection songCollection, int position); //pass your object types.
    }

    public interface OnContextMenuClickListener {
        void onItemClick(MenuItem menuItem, SongCollection songCollection, int position);

        boolean filterItem(MenuItem menuItem, SongCollection songCollection, int position);
    }

}
