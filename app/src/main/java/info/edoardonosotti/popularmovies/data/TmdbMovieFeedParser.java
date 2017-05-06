package info.edoardonosotti.popularmovies.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.edoardonosotti.popularmovies.helpers.TmdbApiHelper;

public class TmdbMovieFeedParser {
    private static final String TAG = TmdbMovieFeedParser.class.getSimpleName();

    public static MovieItem[] parse (String jsonFeed) {
        ArrayList<MovieItem> movieItems = new ArrayList<>();

        try {
            JSONObject feed = new JSONObject(jsonFeed);
            JSONArray movies = feed.getJSONArray("results");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);
                Date releaseDate = new Date();

                try {
                    releaseDate = sdf.parse(movie.getString("release_date"));
                } catch (ParseException ex) {
                    Log.e(TAG, "Could not parse release_date: " + movie.getString("release_date"));
                }

                movieItems.add(new MovieItem(
                        movie.getInt("id"),
                        movie.getString("original_title"),
                        movie.getString("overview"),
                        TmdbApiHelper.buildImageUrl(movie.getString("poster_path")),
                        movie.getDouble("vote_average"),
                        releaseDate
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieItems.toArray(new MovieItem[0]);
    }
}
