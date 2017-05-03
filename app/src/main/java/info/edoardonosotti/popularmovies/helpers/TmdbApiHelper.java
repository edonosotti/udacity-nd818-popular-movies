package info.edoardonosotti.popularmovies.helpers;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class TmdbApiHelper {
    private static final String API_BASE_URL = "http://api.themoviedb.org/3";
    private static final String API_METHOD_POPULAR_MOVIES = "movie/popular";
    private static final String API_METHOD_TOP_RATED_MOVIES = "movie/top_rated";
    private static final String QUERY_PARAM_API_KEY = "api_key";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    private static URL buildUrl(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static URL buildApiUrl(String apiMethod, String apiKey) {
        Uri builtUri = Uri.parse(API_BASE_URL)
                .buildUpon()
                .appendEncodedPath(apiMethod)
                .appendQueryParameter(QUERY_PARAM_API_KEY, apiKey)
                .build();

        return buildUrl(builtUri);
    }

    public static URL buildPopularMoviesApiUrl(String apiKey) {
        return buildApiUrl(API_METHOD_POPULAR_MOVIES, apiKey);
    }

    public static URL buildTopRatedMoviesApiUrl(String apiKey) {
        return buildApiUrl(API_METHOD_TOP_RATED_MOVIES, apiKey);
    }

    public static URL buildImageUrl(String relativeUrl) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL)
                .buildUpon()
                .appendEncodedPath("w185")
                .appendEncodedPath(relativeUrl.substring(1))
                .build();

        return buildUrl(builtUri);
    }
}
