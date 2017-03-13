package com.musiclove.music.Model;

/**
 * Created by Sathya on 3/9/2017.
 */
public class Friend {
    private String fid;
    private String fname;
    private String fpic;
    private String uid;

    public Friend(String fid, String fname, String fpic, String uid) {
        this.fid = fid;
        this.fname = fname;
        this.fpic = fpic;
        this.uid = uid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpic() {
        return fpic;
    }

    public void setFpic(String fpic) {
        this.fpic = fpic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String fpic) {
        this.uid = uid;
    }
}
