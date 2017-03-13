package com.musiclove.music.Model;

/**
 * Created by Sathya on 3/11/2017.
 */
public class FriendsDB {
     private String Id;
      private String uid;
      private String fid;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public FriendsDB(String uid, String fid) {
        this.uid = uid;
        this.fid = fid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
}
