package uk.co.uwcs.pineappleguice.QueueService;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rayhaan on 16/01/15.
 */
public class MediaItem {

    public enum MediaType {
        LOCAL_VIDEO, YOUTUBE;
    }

    private String name;
    private String queuer_username;
    private String path;
    private String url;
    private MediaType type;

    public static class Builder {
        private String name;
        private String queuer_username;
        private String path;
        private String url;
        private MediaType type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder queuer_username(String queuer_username) {
            this.queuer_username = queuer_username;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder type(MediaType type) {
            this.type = type;
            return this;
        }

        public MediaItem build() {
            return new MediaItem(this);
        }
    }

    private MediaItem(Builder b) {
        this.name = b.name;
        this.path = b.path;
        this.queuer_username = b.queuer_username;
        this.url = b.url;
        this.type = b.type;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getQueuer_username() {
        return queuer_username;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public MediaType getType() {
        return type;
    }
}
