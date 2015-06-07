package subjectdenied.gmail.com.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private String artistID;
    private String artistName;

    private ListView listViewTopTracksResults;

    private SpotifyApi api = new SpotifyApi();
    private SpotifyService spotify = api.getService();

    private ArrayList<TopTrack> tracks = new ArrayList<TopTrack>();
    private TopTracksAdapter topTracksAdapter;

    private AsyncTopTrackFetcher asyncTopTrackFetcher;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_toptracks, container, false);

        listViewTopTracksResults = (ListView) rootView.findViewById(R.id.listTopTracks);

        // get intent data
        Intent intent = getActivity().getIntent();
        artistID = intent.getStringExtra("artistID");
        artistName = intent.getStringExtra("artistName");

        // set actionbar title
        getActivity().setTitle(artistName);

        if (savedInstanceState != null) {

            tracks = (ArrayList<TopTrack>) savedInstanceState.getSerializable("topTracks");

            // create listadapter
            topTracksAdapter = new TopTracksAdapter(getActivity(), tracks);
            listViewTopTracksResults.setAdapter(topTracksAdapter);
        } else {
            // create listadapter
            topTracksAdapter = new TopTracksAdapter(getActivity(), tracks);
            listViewTopTracksResults.setAdapter(topTracksAdapter);

            getTopTracks(artistID);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("topTracks", tracks);
        super.onSaveInstanceState(outState);
    }

    public void getTopTracks(String artistID) {

        asyncTopTrackFetcher = new AsyncTopTrackFetcher();
        asyncTopTrackFetcher.execute(artistID);

    }

    public class AsyncTopTrackFetcher extends AsyncTask<String, Void, ArrayList<TopTrack>> {

        @Override
        protected ArrayList<TopTrack> doInBackground(String... params) {

            tracks = new ArrayList<TopTrack>();
            Tracks tracksObject = new Tracks();

            Map options = new HashMap();
            options.put("country", "AT");

            try {
                tracksObject = spotify.getArtistTopTrack(params[0], options);
            } catch (RetrofitError error) {
                Log.d("fetch artists", error.getMessage());
            }

            // get tracks list
            if (tracksObject.tracks != null) {

                for (Track track : tracksObject.tracks) {

                    TopTrack topTrack = new TopTrack();
                    topTrack.trackName = track.name;
                    topTrack.albumName = track.album.name;
                    topTrack.previewUrl = track.preview_url;

                    String imgUrlLarge = null;
                    String imgUrlSmall = null;

                    if (track.album.images.size() > 0) {

                        // check if high-quality artwork is avail
                        imgUrlLarge = track.album.images.get(0).url;

                        if (track.album.images.get(1) != null) {
                            imgUrlSmall = track.album.images.get(1).url;
                        } else {
                            imgUrlSmall = imgUrlLarge;
                        }
                    }

                    topTrack.albumArtHigh = imgUrlLarge;
                    topTrack.albumArtLow = imgUrlSmall;

                    tracks.add(topTrack);
                }
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(ArrayList<TopTrack> tracks) {
            topTracksAdapter.setData(tracks);

            if (tracks.size() == 0) {
                Toast.makeText(getActivity(), "no Top-Tracks for artist found!", Toast.LENGTH_SHORT).show();
            }

        }

    }
}