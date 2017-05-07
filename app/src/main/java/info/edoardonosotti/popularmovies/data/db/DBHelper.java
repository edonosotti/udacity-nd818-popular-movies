package info.edoardonosotti.popularmovies.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "popular_movies.db";

    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME + " (" +
            FavouriteMoviesContract.FavouriteMovieItem._ID + " INTEGER PRIMARY KEY," +
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID + " INTEGER," +
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE + " TEXT," +
            FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
