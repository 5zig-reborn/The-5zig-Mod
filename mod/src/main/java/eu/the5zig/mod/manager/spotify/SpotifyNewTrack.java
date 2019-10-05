package eu.the5zig.mod.manager.spotify;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpotifyNewTrack {
    private List<SpotifyArtist> artists;
    private long duration_ms;
    private String name;
    private String id;
    private SpotifyAlbum album;

    private String base64Image;

    public List<SpotifyArtist> getArtists() {
        return artists;
    }

    public String getArtistsString() {
        return artists.stream().map(SpotifyArtist::getName).collect(Collectors.joining(", "));
    }

    public long getDuration() {
        return duration_ms / 1000;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return base64Image;
    }

    public void setImage(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getImageUrl() {
        return album.getImages().get(0).getUrl();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        return Objects.equals(id, ((SpotifyNewTrack) obj).id);
    }

    @Override
    public int hashCode() {
        int hash = id.length();
        for(char c : id.toCharArray()) {
            hash ^= c;
        }
        return hash;
    }

    public enum Type {
        track, episode, unknown, ad;
    }
}
