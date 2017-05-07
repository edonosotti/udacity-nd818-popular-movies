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

    private static Date parseReleaseDate(String jsonDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = new Date();

        try {
            releaseDate = sdf.parse(jsonDate);
        } catch (ParseException ex) {
            Log.e(TAG, "Could not parse release_date: " + jsonDate);
        }

        return releaseDate;
    }

    public static MovieItem[] parseMovieList(String jsonFeed) {
        ArrayList<MovieItem> movieItems = new ArrayList<>();

        try {
            JSONObject feed = new JSONObject(jsonFeed);
            JSONArray movies = feed.getJSONArray("results");

            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);

                movieItems.add(new MovieItem(
                        movie.getInt("id"),
                        movie.getString("original_title"),
                        movie.getString("overview"),
                        TmdbApiHelper.buildImageUrl(movie.getString("poster_path")),
                        movie.getDouble("vote_average"),
                        parseReleaseDate(movie.getString("release_date"))
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieItems.toArray(new MovieItem[0]);
    }

    public static MovieItem parseMovieInfo(String jsonFeed) {
        MovieItem movieItem = new MovieItem();

        try {
            JSONObject movie = new JSONObject(jsonFeed);

            movieItem = new MovieItem(
                    movie.getInt("id"),
                    movie.getString("original_title"),
                    movie.getString("overview"),
                    TmdbApiHelper.buildImageUrl(movie.getString("poster_path")),
                    movie.getDouble("vote_average"),
                    parseReleaseDate(movie.getString("release_date"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieItem;
    }

    public static MovieTrailer[] parseTrailerList(String jsonFeed) {
        ArrayList<MovieTrailer> movieTrailers = new ArrayList<>();

        try {
            JSONObject feed = new JSONObject(jsonFeed);
            JSONArray trailers = feed.getJSONArray("results");

            for (int i = 0; i < trailers.length(); i++) {
                JSONObject trailer = trailers.getJSONObject(i);

                String site = trailer.getString("site");
                String key = trailer.getString("key");

                if (site.toUpperCase().equals(("YOUTUBE"))) {
                    movieTrailers.add(new MovieTrailer(
                            trailer.getString("id"),
                            key,
                            trailer.getString("name"),
                            site,
                            trailer.getString("type"),
                            trailer.getString("iso_639_1") + "-" + trailer.getString("iso_3166_1"),
                            TmdbApiHelper.buildYouTubeTrailerUrl(key)
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieTrailers.toArray(new MovieTrailer[0]);
    }

    public static MovieReview[] parseReviewList(String jsonFeed) {
        ArrayList<MovieReview> movieTrailers = new ArrayList<>();

        try {
            JSONObject feed = new JSONObject(jsonFeed);
            JSONArray reviews = feed.getJSONArray("results");

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject review = reviews.getJSONObject(i);

                movieTrailers.add(new MovieReview(
                        review.getString("id"),
                        review.getString("author"),
                        review.getString("content"),
                        TmdbApiHelper.validateReviewUrl(review.getString("url"))
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieTrailers.toArray(new MovieReview[0]);
    }
}
