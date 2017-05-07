package info.edoardonosotti.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.TmdbMovieFeedParser;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.helpers.TmdbApiHelper;

public class FetchMovieInfoTask extends AsyncTask<Void, Void, MovieItem> {
    private static final String TAG = FetchMovieInfoTask.class.getSimpleName();

    private FetchMovieInfoTaskConfiguration config;
    private IOnTaskCompleted listener;

    public FetchMovieInfoTask(FetchMovieInfoTaskConfiguration config, IOnTaskCompleted listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    protected MovieItem doInBackground(Void... voids) {
        MovieItem movieItem = new MovieItem();

        URL movieInfoUrl = TmdbApiHelper.buildMovieInfoApiUrl(config.movieId, config.apiKey);

        try {
            String movieData = NetworkHelper.getResponseFromHttpUrl(movieInfoUrl);
            movieItem = TmdbMovieFeedParser.parseMovieInfo(movieData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        if (movieItem != null && movieItem.id > -1) {
            try {
                URL movieTrailersUrl = TmdbApiHelper.buildTrailersApiUrl(movieItem.id, config.apiKey);
                String trailersData = NetworkHelper.getResponseFromHttpUrl(movieTrailersUrl);
                movieItem.trailers = TmdbMovieFeedParser.parseTrailerList(trailersData);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            try {
                URL movieReviewsUrl = TmdbApiHelper.buildReviewsApiUrl(movieItem.id, config.apiKey);
                String reviewsData = NetworkHelper.getResponseFromHttpUrl(movieReviewsUrl);
                movieItem.reviews = TmdbMovieFeedParser.parseReviewList(reviewsData);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

        return movieItem;
    }

    @Override
    protected void onPostExecute(MovieItem movieItem) {
        listener.onTaskCompleted(movieItem);
    }

    public static class FetchMovieInfoTaskConfiguration {
        public String apiKey = "";
        public int movieId = 0;

        public FetchMovieInfoTaskConfiguration(String apiKey, int movieId) {
            this.apiKey = apiKey;
            this.movieId = movieId;
        }
    }
}