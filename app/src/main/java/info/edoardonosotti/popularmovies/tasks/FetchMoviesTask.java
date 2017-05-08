package info.edoardonosotti.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.TmdbMovieFeedParser;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.helpers.TmdbApiHelper;

public class FetchMoviesTask extends AsyncTask<Void, Void, MovieItem[]> {

    private static final String TAG = FetchMoviesTask.class.getSimpleName();

    private FetchMoviesTaskConfiguration config;
    private IOnFetchMoviesTaskCompleted listener;

    public static final int SORT_MODE_POPULAR = 0;
    public static final int SORT_MODE_RATING = 1;

    public FetchMoviesTask(FetchMoviesTaskConfiguration config, IOnFetchMoviesTaskCompleted listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    protected MovieItem[] doInBackground(Void... voids) {
        MovieItem[] movieItems = new MovieItem[0];

        URL apiUrl = TmdbApiHelper.buildPopularMoviesApiUrl(config.apiKey);

        if (config.sortMode == SORT_MODE_RATING) {
            apiUrl = TmdbApiHelper.buildTopRatedMoviesApiUrl(config.apiKey);
        }

        try {
            String movieData = NetworkHelper.getResponseFromHttpUrl(apiUrl);
            movieItems = TmdbMovieFeedParser.parseMovieList(movieData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return movieItems;
    }

    @Override
    protected void onPostExecute(MovieItem[] movieItems) {
        listener.onFetchMoviesTaskCompleted(movieItems);
    }

    public static class FetchMoviesTaskConfiguration {
        public String apiKey = "";
        public int sortMode = 0;

        public FetchMoviesTaskConfiguration(String apiKey) {
            this.apiKey = apiKey;
        }

        public FetchMoviesTaskConfiguration(String apiKey, int sortMode) {
            this.apiKey = apiKey;
            this.sortMode = sortMode;
        }
    }
}