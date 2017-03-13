package com.musiclove.music.Model;

/**
 * Created by Sathya on 3/11/2017.
 */
public class User {
    private String Id;
    private String uid;
    private String name;
    private String pic_url;


    public String getUid() {
        return uid;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }





    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return name;
    }

    public void setUname(String uname) {
        this.name = uname;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }
}
