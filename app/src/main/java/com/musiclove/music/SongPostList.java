package com.musiclove.music;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.v7.app.AlertDialog;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musiclove.music.Adapter.SongPostListAdapter;
import com.musiclove.music.Model.FriendsDB;
import com.musiclove.music.Model.Post_Song;
import com.musiclove.music.Model.SongsDB;
import com.musiclove.music.Model.User;
import com.musiclove.music.Utility.Utility;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Created by sravya on 19-02-2017.
 */
public class SongPostList extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean firstLaunch = true;
    ListView listView;

    /**
     * Mobile Service Table used to access data
     */
    DatabaseHelper myDb = new DatabaseHelper(this);
    private MobileServiceTable<User> mUser;
    private MobileServiceTable<SongsDB> mSong;
    private MobileServiceTable<FriendsDB> mFrnd;
    private int currentIndex;
    private TextView tb_title, tb_duration, tv_time;
    private ImageView iv_play, iv_next, iv_previous;
    private long currentSongLength;
    private ProgressBar pb_loader, pb_main_loader;
    private SeekBar seekBar;
    ArrayList<Post_Song> list;
    SongPostListAdapter adapter = null;
    private MobileServiceClient mClient;


    public class ListClickHandler implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            TextView t1 = (TextView) view.findViewById(R.id.song_title);
            TextView t2 = (TextView) view.findViewById(R.id.artist);
            TextView t3 = (TextView) view.findViewById(R.id.duration);
            TextView t4 = (TextView) view.findViewById(R.id.streamurl);
            TextView t5 = (TextView) view.findViewById(R.id.sid);
            String title = t1.getText().toString();
            String artist = t2.getText().toString();
            String duration = t3.getText().toString();
            String streamurl = t4.getText().toString();
            String id = t5.getText().toString();


            Post_Song clicksong = new Post_Song(id,title,artist,null,duration,streamurl,null,null,null,null);
            createDialog(clicksong);



        }

        public void createDialog(final Post_Song song){


            AlertDialog.Builder builder = new AlertDialog.Builder(SongPostList.this);
            builder.setTitle("SELECT OPTION...");
            builder.setItems(new CharSequence[]
                            {"Play"},
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which) {
                                case 0:
                                    prepareSong(song);
                                    break;
                                

                            }
                        }
                    });
            builder.create().show();
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("TEST CODE IN 1");
        super.onCreate(savedInstanceState);
        System.out.println("TEST CODE IN 2");
        setContentView(R.layout.my_feed);
        System.out.println("TEST CODE IN 3");
        myDb.deleteTable("user_table");
        myDb.deleteTable("song_table");
        myDb.deleteTable("friends_table");
        ImageButton butt1 = (ImageButton)findViewById(R.id.but1);
        ImageButton butt2 = (ImageButton)findViewById(R.id.but2);
        ImageButton butt3 = (ImageButton)findViewById(R.id.but3);
        ImageButton butt4 = (ImageButton)findViewById(R.id.but4);
        ImageButton butt5 = (ImageButton)findViewById(R.id.but5);
        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1= new Intent(SongPostList.this, MainActivity.class);
                startActivity(int1);
            }
        });
        butt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int2= new Intent(SongPostList.this, Timeline.class);
                startActivity(int2);
            }
        });
        butt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int3= new Intent(SongPostList.this, Friendslist.class);
                startActivity(int3);
            }
        });
        butt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int4= new Intent(SongPostList.this, SongPostList.class);
                startActivity(int4);
            }
        });
        butt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int5= new Intent(SongPostList.this, LoginActivity.class);
                startActivity(int5);
            }
        });

        System.out.println("TEST CODE IN 4");

        listView = (ListView) findViewById(R.id.listview);
        System.out.println("TEST CODE IN 5");
        initializeViews();
        System.out.println("TEST CODE IN 6");
        try {
            // Create the Mobile Service Client instance, using the provided
            mClient = new MobileServiceClient(
                    "xxxxxxxxxxxxxxxxxxxxxxxxxx",
                    this
            );

            System.out.println("TEST CODE IN 7");
            // Mobile Service URL and key


            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
            System.out.println("TEST CODE IN 8");
            mUser = mClient.getTable(User.class);
            mSong = mClient.getTable(SongsDB.class);
            mFrnd = mClient.getTable(FriendsDB.class);
            System.out.println("TEST CODE IN 9");
        }catch (Exception e){
            System.out.println("TEST CODE IN 10");
            System.out.println("CLIENT:"+e);
        }
        System.out.println("TEST CODE IN 11");
        SharedPreferences user_details = getSharedPreferences("mysettings",
                Context.MODE_PRIVATE);
        System.out.println("TEST CODE IN 12");
        String user_id = user_details.getString("uid", null);
        System.out.println("SONG LIST USER:"+user_id);

        try {

            URL url;
            URL url2;
            HttpURLConnection urlConnection = null;
            HttpURLConnection urlConnection2 = null;
            url = new URL("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                    "");
            url2 = new URL("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" +
                    "");
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection2 = (HttpURLConnection) url2
                    .openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStream is2 = urlConnection2.getInputStream();
            System.out.println("InputStream:"+is);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2, "UTF-8"));

            String webPage = "",data="";

            while ((data = reader.readLine()) != null){
                System.out.println("DATA:"+data);
                webPage += data + "\n";
            }

            String webPage2 = "",data2="";

            while ((data2 = reader2.readLine()) != null){
                System.out.println("DATA:"+data2);
                webPage2 += data2 + "\n";
            }
            System.out.println("RESULT:"+webPage);
            JSONArray js  = new JSONArray(webPage);
            JSONArray js2 = new JSONArray(webPage2);
            System.out.println("JSON"+js);
            System.out.println("JSON OBJ:"+js.getJSONObject(0));
            System.out.println("JSON OBJ FIELD:"+js.getJSONObject(0).getString("title"));
            for(int i =0;i<js.length();i++){
                String sid = js.getJSONObject(i).getString("id");

                String title = js.getJSONObject(i).getString("title");
                System.out.println("SONG SHARED"+title);

                String artist = js.getJSONObject(i).getString("artist");
                String artwork = js.getJSONObject(i).getString("artworkUrl");
                String duration = js.getJSONObject(i).getString("duration");
                String stream = js.getJSONObject(i).getString("streamUrl");
                String uid = js.getJSONObject(i).getString("uid");
                String time = js.getJSONObject(i).getString("time");
                System.out.println("VALS:"+sid+","+title+","+artist+","+artwork+","+duration+","+stream+","+uid+","+time);
                boolean isInserted =myDb.insertSongData(sid,title,artist,artwork,duration,stream,uid,time);
                if(isInserted == true){
                    System.out.println("Inserted");
                }else{
                    System.out.println("Not Inseted");
                }

            }

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





            is.close();
            is2.close();



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
        System.out.println("TEST CODE IN 120");


        list = new ArrayList<>();
        System.out.println("TEST CODE IN 19");

        adapter = new SongPostListAdapter(list,R.layout.my_feed_item,this);
        System.out.println("TEST CODE IN 20");

        listView.setAdapter(adapter);
        System.out.println("TEST CODE IN 12");

        listView.setOnItemClickListener(new ListClickHandler());


            // Get the Mobile Service Table instance to use



            //Initialisation of media player

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                togglePlay(mp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentIndex + 1 < list.size()) {
                    Post_Song next = list.get(currentIndex + 1);
                    changeSelectedSong(currentIndex + 1);
                    prepareSong(next);
                } else {
                    Post_Song next = list.get(0);
                    changeSelectedSong(0);
                    prepareSong(next);
                }
            }
        });

        //Gestion de la seekbar
        handleSeekbar();

        //Controle de la chanson
        pushPlay();
        pushPrevious();
        pushNext();



        MainActivity.sqLiteHelper = new DatabaseHelper(this);
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT sid,title,artist,artwork_url,duration,stream_url,song_table.uid,time,name,pic_url FROM user_table, song_table WHERE user_table.uid = song_table.uid and song_table.uid = "+user_id+" ORDER BY sid DESC");
        list.clear();
        while(cursor.moveToNext()){
              String sid = cursor.getString(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String artwork_url = cursor.getString(3);
            String duration = cursor.getString(4);
            String stream_url = cursor.getString(5);
            String uid = cursor.getString(6);
            String time = cursor.getString(7);
            String name = cursor.getString(8);
            String upic = cursor.getColumnName(9);

            list.add(new Post_Song(sid, title, artist, artwork_url,duration, stream_url, uid,name,upic,time));
        }
        adapter.notifyDataSetChanged();

    }




    private void handleSeekbar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void prepareSong(Post_Song song){

        currentSongLength = Long.parseLong(song.getDuration());
        pb_loader.setVisibility(View.VISIBLE);
        tb_title.setVisibility(View.GONE);
        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selector_play));
        tb_title.setText(song.getTitle());
        tv_time.setText(Utility.convertDuration(Long.parseLong(song.getDuration())));
        String stream = song.getStreamurl()+"?client_id="+Config.CLIENT_ID;
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(stream);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void togglePlay(MediaPlayer mp){

        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
        }else{
            pb_loader.setVisibility(View.GONE);
            tb_title.setVisibility(View.VISIBLE);
            mp.start();
            iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selector_pause));
            final Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBar.setMax((int) currentSongLength / 1000);
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    tv_time.setText(Utility.convertDuration((long)mediaPlayer.getCurrentPosition()));
                    mHandler.postDelayed(this, 1000);

                }
            });
        }

    }

    private void initializeViews(){

        tb_title = (TextView) findViewById(R.id.tb_title);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_previous = (ImageView) findViewById(R.id.iv_previous);
        pb_loader = (ProgressBar) findViewById(R.id.pb_loader);
        pb_main_loader = (ProgressBar) findViewById(R.id.pb_main_loader);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        tv_time = (TextView) findViewById(R.id.tv_time);


    }

    private void changeSelectedSong(int index){
        adapter.notifyDataSetChanged(); ;
        currentIndex = index;
        adapter.setPositionSelected(currentIndex);
        adapter.notifyDataSetChanged();
    }

    private void pushPlay(){
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying() && mediaPlayer != null){
                    iv_play.setImageDrawable(ContextCompat.getDrawable(SongPostList.this, R.drawable.selector_play));
                    mediaPlayer.pause();
                }else{
                    if(firstLaunch){
                        Post_Song song = list.get(0);
                        changeSelectedSong(0);
                        prepareSong(song);
                    }else{
                        mediaPlayer.start();
                        firstLaunch = false;
                    }
                    iv_play.setImageDrawable(ContextCompat.getDrawable(SongPostList.this, R.drawable.selector_pause));
                }

            }
        });
    }

    private void pushPrevious(){

        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstLaunch = false;
                if(mediaPlayer != null){

                    if(currentIndex - 1 >= 0){
                        Post_Song previous = list.get(currentIndex - 1);
                        changeSelectedSong(currentIndex - 1);
                        prepareSong(previous);
                    }else{
                        changeSelectedSong(list.size() - 1);
                        prepareSong(list.get(list.size() - 1));
                    }

                }
            }
        });

    }

    private void pushNext(){

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstLaunch = false;
                if(mediaPlayer != null){

                    if(currentIndex + 1 < list.size()){
                        Post_Song next = list.get(currentIndex + 1);
                        changeSelectedSong(currentIndex + 1);
                        prepareSong(next);
                    }else{
                        changeSelectedSong(0);
                        prepareSong(list.get(0));
                    }

                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        super.onDestroy();
    }

}
