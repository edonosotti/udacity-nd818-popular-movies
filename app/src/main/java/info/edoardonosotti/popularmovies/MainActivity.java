package info.edoardonosotti.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import info.edoardonosotti.popularmovies.data.FavouriteMoviesContentProvider;
import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.MovieItemsAdapter;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.tasks.FetchMoviesTask;
import info.edoardonosotti.popularmovies.tasks.IOnFetchMoviesTaskCompleted;

public class MainActivity extends AppCompatActivity
        implements MovieItemsAdapter.MovieItemsAdapterOnClickHandler, IOnFetchMoviesTaskCompleted {

    public static final String INTENT_SELECTED_MOVIE = "SELECTED_MOVIE";

    private static final String SAVED_INSTANCE_LIST_TYPE = "LIST_TYPE";
    private static final String SAVED_INSTANCE_FAVOURITES = "FAVOURITES";

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private TextView mListTitle;

    private MovieItemsAdapter mMovieItemsAdapter;

    private int mCurrentRemoteListType = -1;
    private boolean mFavouritesSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mListTitle = (TextView) findViewById(R.id.tv_movie_list_title);

        setGridLayoutManager();
        setGridAdapter();

        if (savedInstanceState != null) {
            mCurrentRemoteListType = savedInstanceState.getInt(SAVED_INSTANCE_LIST_TYPE);
            mFavouritesSelected = savedInstanceState.getBoolean(SAVED_INSTANCE_FAVOURITES);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(Common.TMDB_API_KEY)) {
            Toast.makeText(this, R.string.error_missing_api_key, Toast.LENGTH_LONG).show();
        } else {
            loadMovieData();
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
            mCurrentRemoteListType = FetchMoviesTask.SORT_MODE_POPULAR;
            loadRemoteMovieData();
            return true;
        }
        else if (id == R.id.menu_action_sort_by_rating) {
            mCurrentRemoteListType = FetchMoviesTask.SORT_MODE_RATING;
            loadRemoteMovieData();
            return true;
        }
        else if (id == R.id.menu_action_show_favourites) {
            loadFavouritesMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchMoviesTaskCompleted(Object output) {
        MovieItem[] movieItems = (MovieItem[]) output;
        mMovieItemsAdapter.setMovieData(movieItems);
        mMovieItemsAdapter.notifyDataSetChanged();
        showMoviesData();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SAVED_INSTANCE_LIST_TYPE, mCurrentRemoteListType);
        savedInstanceState.putBoolean(SAVED_INSTANCE_FAVOURITES, mFavouritesSelected);
        super.onSaveInstanceState(savedInstanceState);
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

    protected void loadMovieData() {
        if (mFavouritesSelected) {
            loadFavouritesMovieData();
        } else {
            loadRemoteMovieData();
        }
    }

    protected void loadRemoteMovieData() {
        if (mCurrentRemoteListType < 0) {
            mCurrentRemoteListType = FetchMoviesTask.SORT_MODE_POPULAR;
        }

        mFavouritesSelected = false;
        mLoadingIndicator.setVisibility(View.VISIBLE);

        if (mCurrentRemoteListType == FetchMoviesTask.SORT_MODE_POPULAR) {
            mListTitle.setText(R.string.movie_list_title_popular);
        } else {
            mListTitle.setText(R.string.movie_list_title_toprated);
        }

        if (NetworkHelper.networkIsAvailable(MainActivity.this)) {
            FetchMoviesTask.FetchMoviesTaskConfiguration config =
                    new FetchMoviesTask.FetchMoviesTaskConfiguration(Common.TMDB_API_KEY,
                            mCurrentRemoteListType);
            new FetchMoviesTask(config, this).execute();
        } else {
            showErrorMessage(R.string.network_unavailable);
        }
    }

    protected void loadFavouritesMovieData() {
        mFavouritesSelected = true;
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mListTitle.setText(R.string.movie_list_title_favourite);
        Uri uri = FavouriteMoviesContentProvider.CONTENT_URI;

        try {
            Cursor cursor = getContentResolver().query(uri, new String[0], "", new String[0], "");
            mMovieItemsAdapter.setMovieData(cursor);
            mMovieItemsAdapter.notifyDataSetChanged();
            showMoviesData();
        }
        catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(R.string.generic_error);
        }
    }

    protected void showMoviesData() {
        if (mMovieItemsAdapter.getItemCount() > 0) {
            mLoadingIndicator.setVisibility(View.GONE);
            mErrorMessageDisplay.setVisibility(View.GONE);

            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage(R.string.no_data);
        }
    }

    protected void showErrorMessage(int errorStringId) {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(errorStringId);
    }
}
