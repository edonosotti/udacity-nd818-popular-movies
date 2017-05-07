package info.edoardonosotti.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import info.edoardonosotti.popularmovies.data.MovieItem;
import info.edoardonosotti.popularmovies.helpers.NetworkHelper;
import info.edoardonosotti.popularmovies.tasks.FetchMovieInfoTask;
import info.edoardonosotti.popularmovies.tasks.FetchMoviesTask;
import info.edoardonosotti.popularmovies.tasks.IOnTaskCompleted;

public class MovieInfoActivity extends AppCompatActivity implements IOnTaskCompleted {

    MovieItem mMovieItem;

    TextView mTitle;
    TextView mRelease;
    TextView mRating;
    TextView mPlot;
    ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        mTitle = (TextView) findViewById(R.id.tv_movie_info_title);
        mRelease = (TextView) findViewById(R.id.tv_movie_info_release);
        mRating = (TextView) findViewById(R.id.tv_movie_info_rating);
        mPlot = (TextView) findViewById(R.id.tv_movie_info_plot);
        mPoster = (ImageView) findViewById(R.id.iv_movie_info_poster);

        loadMovieData(getIntent());
    }

    public void showMovieInfo() {
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
        }
    }

    @Override
    public void onTaskCompleted(Object output) {
        if (output != null) {
            mMovieItem = (MovieItem) output;
            showMovieInfo();
        } else {
            showErrorMessage(R.string.generic_error);
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

    protected void showErrorMessage(int errorStringId) {
        Toast.makeText(this, errorStringId, Toast.LENGTH_LONG).show();
    }
}
