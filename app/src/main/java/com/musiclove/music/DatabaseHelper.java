package com.musiclove.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sravya on 18-02-2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "jazzitup";
    public static final String TABLE_NAME = "user_table";
    public static final String TABLE2_NAME = "song_table";
    public static final String TABLE3_NAME = "comment_table";
    public static final String COL_1 = "UID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "PIC_URL";
    public static final String COL2_1 = "SID";
    public static final String COL2_2 = "TITLE";
    public static final String COL2_3 = "ARTIST";
    public static final String COL2_4 = "ARTWORK_URL";
    public static final String COL2_5 = "DURATION";
    public static final String COL2_6 = "STREAM_URL";
    public static final String COL2_7 = "UID";
    public static final String COL2_8 = "TIME";
    public static final String COL3_1 = "CID";
    public static final String COL3_2 = "COMMENT";
    public static final String COL3_3 = "UID";
    public static final String COL3_4 = "SID";

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ("+COL_1+" TEXT PRIMARY KEY,"+COL_2+" TEXT,"+COL_3+" TEXT)");
        db.execSQL(" CREATE TABLE "+TABLE2_NAME+" ("+COL2_1+" TEXT PRIMARY KEY ,"+COL2_2+" TEXT,"+COL2_3+" TEXT,"+COL2_4+" TEXT,"+COL2_5+" TEXT,"+COL2_6+" TEXT,"+COL2_7+" TEXT,"+COL2_8+" TEXT, FOREIGN KEY("+COL2_7+") REFERENCES "+TABLE_NAME+"("+COL_1+"))");
        db.execSQL(" CREATE TABLE "+TABLE3_NAME+" ("+COL3_1+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+COL3_2+" TEXT,"+COL3_3+" TEXT,"+COL3_4+" TEXT, FOREIGN KEY("+COL3_3+") REFERENCES "+TABLE_NAME+"("+COL_1+"),FOREIGN KEY("+COL3_4+") REFERENCES "+TABLE2_NAME+"("+COL2_1+"))");
        db.execSQL(" CREATE TABLE friends_table (UID TEXT,FID TEXT,primary key(UID,FID),foreign key(UID) references user_table(UID),foreign key(FID) references user_table(UID))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE3_NAME);
        db.execSQL("DROP TABLE IF EXISTS  friends_table");
        onCreate(db);
    }

    public boolean insertData(String uid,String name,String pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,uid);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,pic);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean insertSongData(String sid,String title,String artist,String artwork,String duration,String streamurl,String uid,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2_1,sid);
        contentValues.put(COL2_2,title);
        contentValues.put(COL2_3,artist);
        contentValues.put(COL2_4,artwork);
        contentValues.put(COL2_5,duration);
        contentValues.put(COL2_6,streamurl);
        contentValues.put(COL2_7,uid);
        contentValues.put(COL2_8,time);

        long result = db.insert(TABLE2_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
    public boolean insertFriend(String uid,String fid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID",uid);
        contentValues.put("FID",fid);
        long result = db.insert("friends_table",null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public  void deleteSong(String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE2_NAME+" WHERE "+COL2_1+" = "+sid);
    }

    public void deleteTable(String table ){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+table);
    }


    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }
}
