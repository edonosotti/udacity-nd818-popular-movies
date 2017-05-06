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
    private IOnTaskCompleted listener;

    public static final int SORT_MODE_POPULAR = 0;
    public static final int SORT_MODE_RATING = 1;

    public FetchMoviesTask(FetchMoviesTaskConfiguration config, IOnTaskCompleted listener) {
        this.config = config;
        this.listener = listener;
    }

    @Override
    protected MovieItem[] doInBackground(Void... voids) {
        MovieItem[] movieItems = new MovieItem[0];

        URL apiUrl = TmdbApiHelper.buildPopularMoviesApiUrl(config.API_KEY);

        if (config.SORT_MODE == SORT_MODE_RATING) {
            apiUrl = TmdbApiHelper.buildTopRatedMoviesApiUrl(config.API_KEY);
        }

        try {
            String movieData = NetworkHelper.getResponseFromHttpUrl(apiUrl);
            movieItems = TmdbMovieFeedParser.parse(movieData);
            /*mMovieItemsAdapter.setMovieData(movieItems);

            runOnUiThread(new Runnable() {
                public void run() {
                    mMovieItemsAdapter.notifyDataSetChanged();
                    showMoviesData();
                }
            });
*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return movieItems;
    }

    @Override
    protected void onPostExecute(MovieItem[] movieItems) {
        listener.onTaskCompleted(movieItems);
    }

    public static class FetchMoviesTaskConfiguration {
        public String API_KEY = "";
        public int SORT_MODE = 0;

        public FetchMoviesTaskConfiguration(String apiKey) {
            this.API_KEY = apiKey;
        }

        public FetchMoviesTaskConfiguration(String apiKey, int sortMode) {
            this.API_KEY = apiKey;
            this.SORT_MODE = sortMode;
        }
    }
}