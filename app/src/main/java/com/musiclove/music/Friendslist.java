package com.musiclove.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import com.musiclove.music.Adapter.FriendListAdapter;
import com.musiclove.music.Model.Friend;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Friendslist extends AppCompatActivity {
    ArrayList<Friend> list;
    FriendListAdapter adapter = null;
    ListView listView;
    DatabaseHelper myDb = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        myDb.deleteTable("user_table");
        myDb.deleteTable("song_table");
        myDb.deleteTable("friends_table");
        ImageButton butt1 = (ImageButton)findViewById(R.id.but1);
        ImageButton butt2 = (ImageButton)findViewById(R.id.but2);
        ImageButton butt3 = (ImageButton)findViewById(R.id.but3);
        ImageButton butt4 = (ImageButton)findViewById(R.id.but4);
        ImageButton butt5 = (ImageButton)findViewById(R.id.but5);
        Button button2 =(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int8= new Intent(Friendslist.this, InviteActivity.class);
                startActivity(int8);
            }
        });

        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1= new Intent(Friendslist.this, MainActivity.class);
                startActivity(int1);
            }
        });
        butt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int2= new Intent(Friendslist.this, Timeline.class);
                startActivity(int2);
            }
        });
        butt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int3= new Intent(Friendslist.this, Friendslist.class);
                startActivity(int3);
            }
        });
        butt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int4 = new Intent(Friendslist.this, SongPostList.class);
                startActivity(int4);
            }
        });
        butt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int5 = new Intent(Friendslist.this, LoginActivity.class);
                startActivity(int5);
            }
        });

        listView = (ListView) findViewById(R.id.frndlistview);
        try{

        URL url2;
        URL url3;
        HttpURLConnection urlConnection2 = null;
        HttpURLConnection urlConnection3 = null;

        url2 = new URL("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "");
        url3 = new URL("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                "");
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        urlConnection2 = (HttpURLConnection) url2
                .openConnection();
        urlConnection3 = (HttpURLConnection) url3
                .openConnection();

        InputStream is2 = urlConnection2.getInputStream();
        InputStream is3 = urlConnection3.getInputStream();
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2, "UTF-8"));
        BufferedReader reader3 = new BufferedReader(new InputStreamReader(is3, "UTF-8"));


        String webPage2 = "",data2="";

        while ((data2 = reader2.readLine()) != null){
            System.out.println("DATA:"+data2);
            webPage2 += data2 + "\n";
        }

        String webPage3 = "",data3="";

        while ((data3 = reader3.readLine()) != null){
            System.out.println("DATA:"+data3);
            webPage3 += data3 + "\n";
        }
            JSONArray js2 = new JSONArray(webPage2);
            JSONArray js3 = new JSONArray(webPage3);
            for(int i =0;i<js2.length();i++){

                String uid = js2.getJSONObject(i).getString("uid");
                String name = js2.getJSONObject(i).getString("name");
                String pic_url = js2.getJSONObject(i).getString("pic_url");
                System.out.println("User INserted"+uid);
                boolean isInserted =myDb.insertData(uid,name,pic_url);
                if(isInserted == true){
                    System.out.println("Inserted");
                }else{
                    System.out.println("Not Inseted");
                }

            }

            for(int i =0;i<js3.length();i++){
                String uid = js3.getJSONObject(i).getString("uid");
                String fid = js3.getJSONObject(i).getString("fid");
                boolean res = myDb.insertFriend(uid,fid);
                if(res){
                    System.out.println("Inserted");
                }else{
                    System.out.println("Not Inserted");
                }
            }

            is2.close();
            is3.close();


        }catch (SQLiteException s){
            System.out.println("TEST CODE IN 16");

            s.printStackTrace();
        }
        catch (MalformedURLException e) {
            System.out.println("TEST CODE IN 18");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("TEST CODE IN 17");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("TEST CODE IN 32");
            e.printStackTrace();
        }





        list = new ArrayList<>();
        adapter = new FriendListAdapter(list,R.layout.friend_item,this);
        listView.setAdapter(adapter);
        SharedPreferences user_details = getSharedPreferences("mysettings",
                Context.MODE_PRIVATE);
        String user_id = user_details.getString("uid", null);
        System.out.println("SONG LIST USER:"+user_id);
        MainActivity.sqLiteHelper = new DatabaseHelper(this);
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT user_table.uid,name,pic_url from user_table where user_table.uid in (select fid from friends_table where friends_table.uid = "+user_id+")");
        System.out.println("RES:" + cursor);
        list.clear();
        while(cursor.moveToNext()){
            String fid = cursor.getString(0);
            System.out.println("CURSOR:"+fid);

            String fname = cursor.getString(1);
            System.out.println("CURSOR:"+fname);

            String fpic_url = cursor.getString(2);
            System.out.println("CURSOR:"+fpic_url);
            list.add(new Friend(fid, fname, fpic_url, user_id));
        }
        adapter.notifyDataSetChanged();

    }
}
