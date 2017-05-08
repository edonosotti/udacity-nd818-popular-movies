package info.edoardonosotti.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import info.edoardonosotti.popularmovies.data.db.DBHelper;
import info.edoardonosotti.popularmovies.data.db.FavouriteMoviesContract;

public class FavouriteMoviesContentProvider extends ContentProvider {
    private DBHelper mDBHelper;

    private static final int MOVIES = 10;
    private static final int MOVIE_ID = 20;

    private static final String AUTHORITY = "info.edoardonosotti.popularmovies.favouritemoviesprovider";

    private static final String BASE_PATH = "favouritemovies";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, MOVIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);

        validateProjection(projection);

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriType) {
            case MOVIES:
                cursor = db.query(
                        FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
                        projection, null, null, null, null, sortOrder
                );
                break;

            case MOVIE_ID:
                selection = FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID + " = ?";
                String[] args = { uri.getLastPathSegment() };
                cursor = db.query(
                        FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
                        projection, selection, args, null, null, null
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id = 0;

        switch (uriType) {
            case MOVIES:
                id = db.insert(FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME, null, contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int rowsDeleted = 0;

        switch (uriType) {
            case MOVIES:
                rowsDeleted = db.delete(FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:
                selection = FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID + " = ?";
                String[] args = { uri.getLastPathSegment() };
                rowsDeleted = db.delete(FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME, selection, args);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int rowsUpdated = 0;

        switch (uriType) {
            case MOVIES:
                rowsUpdated = db.update(
                        FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case MOVIE_ID:
                String id = uri.getLastPathSegment();
                selection = FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID + " = ?";
                selectionArgs[0] = String.valueOf(id);
                rowsUpdated = db.update(
                        FavouriteMoviesContract.FavouriteMovieItem.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private void validateProjection(String[] projection) {
        String[] valid = {
                FavouriteMoviesContract.FavouriteMovieItem._ID,
                FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID,
                FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE,
                FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(valid));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
