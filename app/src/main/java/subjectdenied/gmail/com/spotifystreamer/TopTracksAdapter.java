package subjectdenied.gmail.com.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chris on 07.06.15.
 */
public class TopTracksAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TopTrack> tracks;

    private LayoutInflater layoutInflater;

    public TopTracksAdapter(Context context, ArrayList<TopTrack> tracks) {

        this.context = context;
        this.tracks = tracks;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<TopTrack> tracks) {

        this.tracks = tracks;
        this.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.toptracks_item, parent, false);
        }

        // get Track data
        TopTrack topTrack = tracks.get(position);

        // get ui elements
        ImageView albumArt = (ImageView) convertView.findViewById(R.id.imgTrackPreview);
        TextView lblTrackName = (TextView) convertView.findViewById(R.id.lblTrackName);
        TextView lblAlbumName = (TextView) convertView.findViewById(R.id.lblAlbumName);

        // set ui elements
        lblTrackName.setText(topTrack.trackName);
        lblAlbumName.setText(topTrack.albumName);
        Picasso.with(context).load(topTrack.albumArtLow).into(albumArt);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }


}
