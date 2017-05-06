package info.edoardonosotti.popularmovies.data.db;

import android.provider.BaseColumns;

public class FavouriteMoviesContract {
    private FavouriteMoviesContract() {}

    public static class FavouriteMovieItem implements BaseColumns {
        public static final String TABLE_NAME = "favouriteMovies";
        public static final String COLUMN_NAME_MOVIE_ID = "movieId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POSTER = "poster";
    }
}
