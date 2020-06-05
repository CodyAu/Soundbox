package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.AudioLoader;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.PlaybackController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Recommendation;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;

public class RecListAdapter extends ArrayAdapter<Recommendation> implements View.OnClickListener {
    private List<Recommendation> data;
    Context mContext;
    private int lastPos = -1;

    //View lookup cache
    private static class ViewHolder {
        TextView songName;
        TextView artist;
        ImageView album_art;
    }

    public RecListAdapter(Context context, List<Recommendation> data) {
        super(context, R.layout.recom_entry_layout, data);

        this.data = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rec_entry) {
            Song s = ((Song) v.getTag(R.string.rec_song));

            if (s != null) {
                PlaybackController.setSong(s);
                PlaybackController.play();
            }
        }
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup group) {
        //Get the data item for this position
        Recommendation rec = getItem(pos);
        List<Song> songs = rec.getRecs();
        Song cur;
        if (songs.size() > 0) {
            int index = ThreadLocalRandom.current().nextInt(songs.size());
            cur = songs.get(index);
        } else {
            cur = new Song.Builder()
                    .setSongId(-1)
                    .setSongName("No recommendations found")
                    .build();
        }
        ViewHolder viewHolder;//this is the view lookup stored cache
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.recom_entry_layout, group, false);
            viewHolder.songName = convertView.findViewById(R.id.song_name);
            viewHolder.artist = convertView.findViewById(R.id.artist);
            viewHolder.album_art = convertView.findViewById(R.id.album_cover);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.songName.setText(cur.getSongName());
        viewHolder.artist.setText(cur.getArtist());

        convertView.setTag(R.string.rec_song, cur);
        convertView.findViewById(R.id.rec_entry).setOnClickListener(this);

        ImageView thumnail = ((ImageView) convertView.findViewById(R.id.album_cover));
        Bitmap bitmapImage = (cur.hasThumbnail()) ? AudioLoader.loadImageFromUri(mContext, Uri.parse(cur.getFilepath())) : null;

        if (bitmapImage != null) thumnail.setImageBitmap(bitmapImage);
        else Glide.with(mContext).load(R.drawable.playlist1).into(thumnail);

        //Return the completed view to the render on the screen
        return convertView;
    }
}

