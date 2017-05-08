package info.edoardonosotti.popularmovies.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.edoardonosotti.popularmovies.R;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersAdapterViewHolder> {
    private Context mContext;
    private MovieTrailer[] mMovieTrailers;

    private final MovieTrailersAdapterOnClickHandler mClickHandler;

    public MovieTrailersAdapter(MovieTrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieTrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_trailer_item, parent, false);
        return new MovieTrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapterViewHolder holder, int position) {
        MovieTrailer movieTrailer = mMovieTrailers[position];
        holder.mTitle.setText(movieTrailer.name);
        holder.mDescription.setText(movieTrailer.type);
    }

    @Override
    public int getItemCount() {
        return (mMovieTrailers != null) ? mMovieTrailers.length : 0;
    }

    public void setMovieTrailersData(MovieTrailer[] movieTrailers) {
        mMovieTrailers = movieTrailers;
    }

    public interface MovieTrailersAdapterOnClickHandler {
        void onClick(MovieTrailer movieTrailer);
    }

    public class MovieTrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTitle;
        public final TextView mDescription;

        public MovieTrailersAdapterViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.tv_trailer_title);
            mDescription = (TextView) view.findViewById(R.id.tv_trailer_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer movieTrailer = mMovieTrailers[adapterPosition];
            mClickHandler.onClick(movieTrailer);
        }
    }

}
