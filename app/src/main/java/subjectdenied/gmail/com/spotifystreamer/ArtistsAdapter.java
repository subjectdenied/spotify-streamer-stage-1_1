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
 * Created by chris on 06.06.15.
 */
public class ArtistsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchedArtist> artists;
    private LayoutInflater layoutInflater;

    public ArtistsAdapter(final Context context, ArrayList<SearchedArtist> artists) {
        this.context = context;
        this.artists = artists;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<SearchedArtist> artists) {

        this.artists = artists;
        this.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return this.artists.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.searchresult_item, parent, false);
        }

        // get data
        SearchedArtist artist = this.artists.get(position);
        String name = artist.artistName;
        String spotifyID = artist.artistID;

        ImageView imgArtistPreview = (ImageView) convertView.findViewById(R.id.imgArtistPreview);
        TextView lblArtist = (TextView) convertView.findViewById(R.id.lblArtistName);

        // load artists preview image
        Picasso.with(context).load(artist.albumArt).into(imgArtistPreview);

        // set name
        lblArtist.setText(name);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return this.artists.get(position);
    }
}
