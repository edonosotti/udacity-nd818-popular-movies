package info.edoardonosotti.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import info.edoardonosotti.popularmovies.R;
import info.edoardonosotti.popularmovies.data.db.FavouriteMoviesContract;

public class MovieItemsAdapter extends RecyclerView.Adapter<MovieItemsAdapter.MovieItemsAdapterViewHolder> {
    private Context mContext;
    private MovieItem[] mMovieItems;

    private final MovieItemsAdapterOnClickHandler mClickHandler;

    public MovieItemsAdapter(MovieItemsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieItemsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieItemsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieItemsAdapterViewHolder holder, int position) {
        MovieItem movieItem = mMovieItems[position];
        Picasso
                .with(mContext)
                .load(movieItem.posterImageUrl.toString())
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_no_image)
                .into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        return (mMovieItems != null) ? mMovieItems.length : 0;
    }

    public void setMovieData(MovieItem[] movieItems) {
        mMovieItems = movieItems;
    }

    public void setMovieData(Cursor cursor) {
        ArrayList<MovieItem> movies = new ArrayList<>();

        while (cursor.moveToNext()) {
            MovieItem movieItem = new MovieItem();

            movieItem.favouriteMovieRecordId = cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem._ID));
            movieItem.id = cursor.getInt(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_MOVIE_ID));
            movieItem.originalTitle = cursor.getString(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_TITLE));

            String posterImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FavouriteMoviesContract.FavouriteMovieItem.COLUMN_NAME_POSTER));

            try {
                movieItem.posterImageUrl = new URL(posterImageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            movies.add(movieItem);
        }

        mMovieItems =  movies.toArray(new MovieItem[0]);
    }

    public interface MovieItemsAdapterOnClickHandler {
        void onClick(MovieItem movieItem);
    }

    public class MovieItemsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mMoviePoster;

        public MovieItemsAdapterViewHolder(View view) {
            super(view);
            mMoviePoster = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieItem movieItem = mMovieItems[adapterPosition];
            mClickHandler.onClick(movieItem);
        }
    }

}
