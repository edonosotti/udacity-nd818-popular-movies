package info.edoardonosotti.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.MovieItemsAdapter;
import info.edoardonosotti.popularmovies.data.TmdbMovieFeedParser;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.helpers.TmdbApiHelper;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieItemsAdapter.MovieItemsAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TMDB_API_KEY = "";

    public static final String INTENT_SELECTED_MOVIE = "SELECTED_MOVIE";
    public static final String MOVIE_SORT_POPULAR = "SORT_POPULAR";
    public static final String MOVIE_SORT_RATING = "SORT_RATING";

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private MovieItemsAdapter mMovieItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieItemsAdapter = new MovieItemsAdapter(this);
        mRecyclerView.setAdapter(mMovieItemsAdapter);

        if (TMDB_API_KEY.equals("")) {
            Toast.makeText(this, R.string.error_missing_api_key, Toast.LENGTH_LONG).show();
        } else {
            loadMovieData(MOVIE_SORT_POPULAR);
        }
    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, MovieInfoActivity.class);
        intent.putExtra(INTENT_SELECTED_MOVIE, movieItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_sort_by_popularity) {
            loadMovieData(MOVIE_SORT_POPULAR);
            return true;
        }
        else if (id == R.id.menu_action_sort_by_rating){
            loadMovieData(MOVIE_SORT_RATING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void loadMovieData(String sortType) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        if (NetworkHelper.networkIsAvailable(MainActivity.this)) {
            new FetchMoviesTask().execute(sortType);
        } else {
            showErrorMessage();
        }
    }

    private void showMoviesData() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieItem[]> {

        @Override
        protected MovieItem[] doInBackground(String... params) {

            MovieItem[] movieItems = new MovieItem[0];

            String sortType = params[0];
            URL apiUrl = TmdbApiHelper.buildPopularMoviesApiUrl(TMDB_API_KEY);

            if (sortType.equals(MOVIE_SORT_RATING)) {
                apiUrl = TmdbApiHelper.buildTopRatedMoviesApiUrl(TMDB_API_KEY);
            }

            try {
                String movieData = NetworkHelper.getResponseFromHttpUrl(apiUrl);
                movieItems = TmdbMovieFeedParser.parse(movieData);
                mMovieItemsAdapter.setMovieData(movieItems);

                runOnUiThread(new Runnable() {
                    public void run() {
                        mMovieItemsAdapter.notifyDataSetChanged();
                        showMoviesData();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            return movieItems;
        }
    }
}
