package eu.the5zig.mod.manager.spotify;

public class SpotifyNewStatus {
    private long timestamp;
    private long progress_ms;
    private SpotifyNewTrack item;
    private boolean is_playing;
    private String currently_playing_type;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getProgress() {
        return progress_ms / 1000;
    }

    public SpotifyNewTrack getTrack() {
        return item;
    }

    public boolean isPlaying() {
        return is_playing;
    }

    public SpotifyNewTrack.Type getType() {
        return SpotifyNewTrack.Type.valueOf(currently_playing_type);
    }
}
