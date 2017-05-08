package info.edoardonosotti.popularmovies.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.edoardonosotti.popularmovies.R;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {
    private Context mContext;
    private MovieReview[] mMovieReviews;

    private final MovieReviewsAdapterOnClickHandler mClickHandler;

    public MovieReviewsAdapter(MovieReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_review_item, parent, false);
        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapterViewHolder holder, int position) {
        MovieReview movieReview = mMovieReviews[position];
        holder.mAuthor.setText(movieReview.author);
        holder.mContent.setText(movieReview.content);
    }

    @Override
    public int getItemCount() {
        return (mMovieReviews != null) ? mMovieReviews.length : 0;
    }

    public void setMovieReviewsData(MovieReview[] movieReviews) {
        mMovieReviews = movieReviews;
    }

    public interface MovieReviewsAdapterOnClickHandler {
        void onClick(MovieReview movieReview);
    }

    public class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mAuthor;
        public final TextView mContent;

        public MovieReviewsAdapterViewHolder(View view) {
            super(view);
            mAuthor = (TextView) view.findViewById(R.id.tv_review_author);
            mContent = (TextView) view.findViewById(R.id.tv_review_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieReview movieReview = mMovieReviews[adapterPosition];
            mClickHandler.onClick(movieReview);
        }
    }

}
