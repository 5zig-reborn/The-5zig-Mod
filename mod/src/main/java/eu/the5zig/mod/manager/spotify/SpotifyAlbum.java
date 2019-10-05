package eu.the5zig.mod.manager.spotify;

import java.util.List;

public class SpotifyAlbum {

    private List<AlbumImage> images;

    public List<AlbumImage> getImages() {
        return images;
    }

    public static class AlbumImage {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
