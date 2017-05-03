package info.edoardonosotti.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.Date;

public class MovieItem implements Parcelable {
    public String originalTitle;
    public String plotSynopsys;
    public URL posterImageUrl;
    public Double averageUserRating;
    public Date releaseDate;

    public MovieItem(String originalTitle, String plotSynopsys, URL posterImageUrl,
                     Double averageUserRating, Date releaseDate) {
        this.originalTitle = originalTitle;
        this.plotSynopsys = plotSynopsys;
        this.posterImageUrl = posterImageUrl;
        this.averageUserRating = averageUserRating;
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(originalTitle);
        out.writeString(plotSynopsys);
        out.writeValue(posterImageUrl);
        out.writeDouble(averageUserRating);
        out.writeValue(releaseDate);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private MovieItem(Parcel in) {
        originalTitle = in.readString();
        plotSynopsys = in.readString();
        posterImageUrl = (URL) in.readValue(URL.class.getClassLoader());
        averageUserRating = in.readDouble();
        releaseDate = (Date) in.readValue(Date.class.getClassLoader());
    }
}
