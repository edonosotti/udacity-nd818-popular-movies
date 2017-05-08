package info.edoardonosotti.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import info.edoardonosotti.popularmovies.data.FavouriteMoviesContentProvider;
import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.data.MovieReview;
import info.edoardonosotti.popularmovies.data.MovieReviewsAdapter;
import info.edoardonosotti.popularmovies.data.MovieTrailer;
import info.edoardonosotti.popularmovies.data.MovieTrailersAdapter;
import info.edoardonosotti.popularmovies.data.db.FavouriteMoviesContract;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.tasks.FetchMovieInfoTask;
import info.edoardonosotti.popularmovies.tasks.IOnFetchMoviesTaskCompleted;

public class MovieInfoActivity extends AppCompatActivity
        implements IOnFetchMoviesTaskCompleted,
        MovieTrailersAdapter.MovieTrailersAdapterOnClickHandler,
        MovieReviewsAdapter.MovieReviewsAdapterOnClickHandler {

    MovieItem mMovieItem;

    TextView mTitle;
    TextView mRelease;
    TextView mRating;
    TextView mPlot;
    ImageView mPoster;
    Button mFavouriteButton;

    RecyclerView mRecyclerViewTrailers;
    RecyclerView mRecyclerViewReviews;

    MovieTrailersAdapter mMovieTrailersAdapter;
    MovieReviewsAdapter mMovieReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        mTitle = (TextView) findViewById(R.id.tv_movie_info_title);
        mRelease = (TextView) findViewById(R.id.tv_movie_info_release);
        mRating = (TextView) findViewById(R.id.tv_movie_info_rating);
        mPlot = (TextView) findViewById(R.id.tv_movie_info_plot);
        mPoster = (ImageView) findViewById(R.id.iv_movie_info_poster);
        mFavouriteButton = (Button) findViewById(R.id.tb_move_info_favourite);

        mRecyclerViewTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);

        loadMovieData(getIntent());
        handleFavouriteButtonPress();
    }


    @Override
    public void onFetchMoviesTaskCompleted(Object output) {
        if (output != null) {
            mMovieItem = (MovieItem) output;
            showMovieInfo();
            setFavouriteButtonStatus();
        } else {
            showErrorMessage(R.string.generic_error);
        }
    }

    protected void showMovieInfo() {
        if (mMovieItem != null) {
            String release = DateUtils.formatDateTime(this, mMovieItem.releaseDate.getTime(), DateUtils.FORMAT_SHOW_YEAR);

            mTitle.setText(mMovieItem.originalTitle);
            mRelease.setText(String.format(getString(R.string.movie_info_release), release));
            mRating.setText(String.format(getString(R.string.movie_info_rating), String.valueOf(mMovieItem.averageUserRating)));
            mPlot.setText(mMovieItem.plotSynopsys);

            Picasso
                    .with(MovieInfoActivity.this)
                    .load(mMovieItem.posterImageUrl.toString())
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_no_image)
                    .into(mPoster);

            loadTrailersData();
            loadReviewsData();
        }
    }

    protected void handleFavouriteButtonPress() {
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavouriteStatus();
                setFavouriteButtonStatus();
            }
        });
    }

    protected boolean isFavourite() {
        if (mMovieItem != null) {
            Uri uri = Uri.parse(FavouriteMoviesContentProvider.CONTENT_URI + "/" +
                    String.valueOf(mMovieItem.id));

            Cursor cursor = getContentResolver().query(uri, new String[0], "", new String[0], "");

            return (cursor.getCount() > 0);
        }

        return false;
    }

    protected boolean addFavourite() {
        Uri uri = FavouriteMoviesContentProvider.CONTENT_URI;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID, mMovieItem.id);
        contentValues.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE, mMovieItem.originalTitle);
        contentValues.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER, mMovieItem.posterImageUrl.toString());

        try {
            getContentResolver().insert(uri, contentValues);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    protected boolean removeFavourite() {
        Uri uri = Uri.parse(FavouriteMoviesContentProvider.CONTENT_URI + "/" +
                String.valueOf(mMovieItem.id));

        try {
            int affectedRows = getContentResolver().delete(uri, "", new String[0]);
            return (affectedRows > 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    protected void changeFavouriteStatus() {
        boolean success = false;

        if (mMovieItem != null) {
            if (!isFavourite()) {
                success = addFavourite();
            } else {
                success = removeFavourite();
            }
        }

        if (!success) {
            showErrorMessage(R.string.error_cannot_write_database);
        }
    }

    protected void setFavouriteButtonStatus() {
        if (isFavourite()) {
            mFavouriteButton.setText(R.string.btn_favourite_on);
            mFavouriteButton.setBackgroundColor(Color.RED);
        } else {
            mFavouriteButton.setText(R.string.btn_favourite_off);
            mFavouriteButton.setBackgroundColor(Color.GREEN);
        }
    }

    protected void loadMovieData(Intent intent) {
        int movieId = intent.getIntExtra(MainActivity.INTENT_SELECTED_MOVIE, -1);

        if (movieId > -1) {
            if (NetworkHelper.networkIsAvailable(MovieInfoActivity.this)) {
                FetchMovieInfoTask.FetchMovieInfoTaskConfiguration config =
                        new FetchMovieInfoTask.FetchMovieInfoTaskConfiguration(Common.TMDB_API_KEY, movieId);
                new FetchMovieInfoTask(config, this).execute();
            } else {
                showErrorMessage(R.string.generic_error);
            }
        }
    }

    protected void loadTrailersData() {
        mRecyclerViewTrailers.setLayoutManager(new GridLayoutManager(this, 1));
        mMovieTrailersAdapter = new MovieTrailersAdapter(this);
        mRecyclerViewTrailers.setAdapter(mMovieTrailersAdapter);
        mMovieTrailersAdapter.setMovieTrailersData(mMovieItem.trailers);
        mMovieTrailersAdapter.notifyDataSetChanged();
    }

    protected void loadReviewsData() {
        mRecyclerViewReviews.setLayoutManager(new GridLayoutManager(this, 1));
        mMovieReviewsAdapter = new MovieReviewsAdapter(this);
        mRecyclerViewReviews.setAdapter(mMovieReviewsAdapter);
        mMovieReviewsAdapter.setMovieReviewsData(mMovieItem.reviews);
        mMovieReviewsAdapter.notifyDataSetChanged();
    }

    protected void showErrorMessage(int errorStringId) {
        Toast.makeText(this, errorStringId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(MovieTrailer movieTrailer) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(movieTrailer.url.toString()));
        startActivity(i);
    }

    @Override
    public void onClick(MovieReview movieReview) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(movieReview.url.toString()));
        startActivity(i);
    }
}
