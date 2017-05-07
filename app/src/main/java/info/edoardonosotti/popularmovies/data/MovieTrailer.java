package info.edoardonosotti.popularmovies.data;

import java.net.URL;

public class MovieTrailer {
    public String id;
    public String key;
    public String name;
    public String site;
    public String type;
    public String lang;
    public URL url;

    public MovieTrailer(String id, String key, String name, String site, String type,
                        String lang, URL url) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.lang = lang;
        this.url = url;
    }
}
