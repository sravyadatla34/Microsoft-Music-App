package com.musiclove.music.Model;

/**
 * Created by Sathya on 3/11/2017.
 */
public class SongsDB {
    private  int Id;
    private String title;
    private String artist;
    private String artworkUrl;
    private String duration;
    private String streamUrl;
    private String uid;
    private String time;

    public SongsDB(String title, String artist, String artworkUrl, String duration, String streamUrl, String uid, String time) {
        this.title = title;
        this.artist = artist;
        this.artworkUrl = artworkUrl;
        this.duration = duration;
        this.streamUrl = streamUrl;
        this.uid = uid;
        this.time = time;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
