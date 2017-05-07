package info.edoardonosotti.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.Date;

public class MovieItem {
    public int id = -1;

    public String originalTitle;
    public String plotSynopsys;
    public URL posterImageUrl;
    public Double averageUserRating;
    public Date releaseDate;

    public MovieTrailer[] trailers;
    public MovieReview[] reviews;

    public long favouriteMovieRecordId;

    public MovieItem() {

    }

    public MovieItem(int id, String originalTitle, String plotSynopsys, URL posterImageUrl,
                     Double averageUserRating, Date releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.plotSynopsys = plotSynopsys;
        this.posterImageUrl = posterImageUrl;
        this.averageUserRating = averageUserRating;
        this.releaseDate = releaseDate;

        this.trailers = new MovieTrailer[0];
        this.reviews = new MovieReview[0];

        this.favouriteMovieRecordId = -1;
    }
}
