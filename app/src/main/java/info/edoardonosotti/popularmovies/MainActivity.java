package info.edoardonosotti.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.MovieItemsAdapter;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.tasks.FetchMoviesTask;
import info.edoardonosotti.popularmovies.tasks.IOnTaskCompleted;

public class MainActivity extends AppCompatActivity
        implements MovieItemsAdapter.MovieItemsAdapterOnClickHandler, IOnTaskCompleted {

    public static final String INTENT_SELECTED_MOVIE = "SELECTED_MOVIE";

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private MovieItemsAdapter mMovieItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        setGridLayoutManager();
        setGridAdapter();

        if (Common.TMDB_API_KEY.equals("")) {
            Toast.makeText(this, R.string.error_missing_api_key, Toast.LENGTH_LONG).show();
        } else {
            loadMovieData(FetchMoviesTask.SORT_MODE_POPULAR);
        }
    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, MovieInfoActivity.class);
        intent.putExtra(INTENT_SELECTED_MOVIE, movieItem.id);
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
            loadMovieData(FetchMoviesTask.SORT_MODE_POPULAR);
            return true;
        }
        else if (id == R.id.menu_action_sort_by_rating){
            loadMovieData(FetchMoviesTask.SORT_MODE_RATING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(Object output) {
        MovieItem[] movieItems = (MovieItem[]) output;
        mMovieItemsAdapter.setMovieData(movieItems);
        mMovieItemsAdapter.notifyDataSetChanged();
        showMoviesData();
    }

    protected void setGridLayoutManager() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    protected void setGridAdapter() {
        mMovieItemsAdapter = new MovieItemsAdapter(this);
        mRecyclerView.setAdapter(mMovieItemsAdapter);
    }

    protected void loadMovieData(int sortType) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        if (NetworkHelper.networkIsAvailable(MainActivity.this)) {
            FetchMoviesTask.FetchMoviesTaskConfiguration config =
                    new FetchMoviesTask.FetchMoviesTaskConfiguration(Common.TMDB_API_KEY, sortType);
            new FetchMoviesTask(config, this).execute();
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
}
