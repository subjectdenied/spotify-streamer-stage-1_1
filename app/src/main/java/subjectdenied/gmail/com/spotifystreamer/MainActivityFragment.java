package subjectdenied.gmail.com.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private SearchView txtSearchArtist;
    private ListView listViewSearchResults;

    private SpotifyApi api = new SpotifyApi();
    private SpotifyService spotify = api.getService();

    private String currentSearchText;
    private ArrayList<SearchedArtist> artists = new ArrayList<SearchedArtist>();
    private ArtistsAdapter artistsAdapter;

    private AsyncArtistsSearcher asyncArtistsSearcher;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        txtSearchArtist = (SearchView) rootView.findViewById(R.id.txtSearchArtist);
        listViewSearchResults = (ListView) rootView.findViewById(R.id.listSearchResults);

        // create listAdapter
        artistsAdapter = new ArtistsAdapter(getActivity(), artists);
        listViewSearchResults.setAdapter(artistsAdapter);

        listViewSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get item data
                SearchedArtist artist = (SearchedArtist) artistsAdapter.getItem(position);
                String artistID = artist.artistID;
                String artistName = artist.artistName;

                // create intent
                Intent intent = new Intent(getActivity(), TopTracksActivity.class);
                intent.putExtra("artistID", artistID);
                intent.putExtra("artistName", artistName);
                startActivity(intent);
            }
        });

        txtSearchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchText = query;

                // display a floating message
                getArtists();
                txtSearchArtist.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // check if savedInstance exists
        if (savedInstanceState != null) {
            currentSearchText = savedInstanceState.getString("query");
            artists = (ArrayList<SearchedArtist>) savedInstanceState.getSerializable("searchedArtists");
            txtSearchArtist.setQuery(currentSearchText, false);
            txtSearchArtist.clearFocus();

            artistsAdapter.setData(artists);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("query", currentSearchText);
        outState.putSerializable("searchedArtists", artists);
        super.onSaveInstanceState(outState);
    }

    public void getArtists() {
        asyncArtistsSearcher = new AsyncArtistsSearcher();
        asyncArtistsSearcher.execute(currentSearchText);
    }

    public class AsyncArtistsSearcher extends AsyncTask<String, Void, ArrayList<SearchedArtist>> {

        @Override
        protected ArrayList<SearchedArtist> doInBackground(String... params) {

            ArtistsPager artistsPager = new ArtistsPager();
            artists = new ArrayList<SearchedArtist>();

            try {
               artistsPager = spotify.searchArtists(params[0]);
            } catch (RetrofitError error) {
                Log.d("fetch artists", error.getMessage());
            }

            if (artistsPager.artists != null) {
                for (Artist artist : artistsPager.artists.items) {
                    SearchedArtist searchedArtist = new SearchedArtist();
                    searchedArtist.artistID = artist.id;
                    searchedArtist.artistName = artist.name;

                    // get image-url
                    String imgUrl = null;

                    if (artist.images.size() > 0) {

                        if (artist.images.get(0).url != null) {

                            imgUrl = artist.images.get(0).url;

                        }

                    } else {

                        Log.d("imgArtistPreview", "artist image preview not found");

                    }

                    searchedArtist.albumArt = imgUrl;

                    artists.add(searchedArtist);

                }
            }

            return artists;
        }

        @Override
        protected void onPostExecute(ArrayList<SearchedArtist> artists) {
            artistsAdapter.setData(artists);

            if (artists.size() == 0) {
                Toast.makeText(getActivity(), "no results found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
