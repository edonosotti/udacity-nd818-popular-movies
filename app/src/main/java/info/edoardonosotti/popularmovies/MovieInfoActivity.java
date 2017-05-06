package info.edoardonosotti.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import info.edoardonosotti.popularmovies.data.MovieItem;

public class MovieInfoActivity extends AppCompatActivity {

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

    protected void loadMovieData(Intent intent) {
        MovieItem movieItem = intent.getParcelableExtra(MainActivity.INTENT_SELECTED_MOVIE);

        String release = DateUtils.formatDateTime(this, movieItem.releaseDate.getTime(), DateUtils.FORMAT_SHOW_YEAR);

        mTitle.setText(movieItem.originalTitle);
        mRelease.setText(String.format(getString(R.string.movie_info_release), release));
        mRating.setText(String.format(getString(R.string.movie_info_rating), String.valueOf(movieItem.averageUserRating)));
        mPlot.setText(movieItem.plotSynopsys);

        Picasso
                .with(MovieInfoActivity.this)
                .load(movieItem.posterImageUrl.toString())
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_no_image)
                .into(mPoster);
    }
}
