package com.musiclove.music.Model;

/**
 * Created by sravya on 19-02-2017.
 */
public class Post_Song {
    private String id;
    private String title;
    private String artist;
    private String artwork;
    private String duration;
    private String streamurl;
    private String uid;
    private String uname;
    private String time;
    private String user_pic;



    public Post_Song(String id, String title, String artist, String artwork, String duration, String streamurl, String uid, String uname,String user_pic, String time) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.artwork = artwork;
        this.duration = duration;
        this.streamurl = streamurl;
        this.uid = uid;
        this.time = time;
        this.uname = uname;
        this.user_pic = user_pic;

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStreamurl() {
        return streamurl;
    }

    public void setStreamurl(String streamurl) {
        this.streamurl = streamurl;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public void setTime(String time) {
        this.time = time;

    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

}
