package info.edoardonosotti.popularmovies.data;

import java.net.URL;

public class MovieReview {
    public String id;
    public String author;
    public String content;

    public URL url;

    public MovieReview(String id, String author, String content, URL url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }
}
