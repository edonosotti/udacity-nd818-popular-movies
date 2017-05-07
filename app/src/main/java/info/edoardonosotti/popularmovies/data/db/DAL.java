package info.edoardonosotti.popularmovies.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import info.edoardonosotti.popularmovies.data.MovieItem;

public class DAL {
    private static final String TAG = DAL.class.getSimpleName();

    Context mContext;
    DBHelper mDBHelper;

    public DAL(Context context) {
        this.mContext = context;
    }

    private SQLiteDatabase getDBInstance(boolean writable) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(mContext);
        }

        return (writable) ?
            mDBHelper.getWritableDatabase() :
            mDBHelper.getReadableDatabase();
    }

    public long addFavouriteMovie(MovieItem movieItem) {
        ContentValues values = new ContentValues();
        values.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID, movieItem.id);
        values.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE, movieItem.originalTitle);
        values.put(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER, movieItem.posterImageUrl.toString());

        return getDBInstance(true)
            .insert(FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME, null, values);
    }

    public MovieItem[] getFavouriteMovies() {
        String[] projection = {
            FavouriteMoviesContract.FavouriteMovieItem._ID,
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID,
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE,
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER
        };

        String sortOrder = FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE;

        Cursor cursor = getDBInstance(false).query(
            FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
            projection, null, null, null, null, sortOrder
        );

        ArrayList<MovieItem> movies = new ArrayList<>();
        while(cursor.moveToNext()) {
            MovieItem movieItem = new MovieItem();

            movieItem.favouriteMovieRecordId = cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem._ID));
            movieItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID));
            movieItem.originalTitle = cursor.getString(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE));

            String posterImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem._ID));

            try {
                movieItem.posterImageUrl = new URL(posterImageUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Could not parseMovieList URL: " + posterImageUrl);
                e.printStackTrace();
            }

            movies.add(movieItem);
        }
        cursor.close();

        return movies.toArray(new MovieItem[0]);
    }

    public boolean isMovieFavourite(int movieId) {
        String[] projection = {
                FavouriteMoviesContract.FavouriteMovieItem._ID
        };

        String selection = FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(movieId) };

        Cursor cursor = getDBInstance(false).query(
            FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
            projection, selection, selectionArgs, null, null, null
        );

        return (cursor.getCount() > 0);
    }

    public void deleteFavouriteMovie(long favouriteMovieRecordId) {
        String selection = FavouriteMoviesContract.FavouriteMovieItem._ID + " = ?";
        String[] selectionArgs = { String.valueOf(favouriteMovieRecordId) };
        getDBInstance(true).delete(FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME, selection, selectionArgs);
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }
}
