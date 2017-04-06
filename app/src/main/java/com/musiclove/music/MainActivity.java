package com.musiclove.music;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.musiclove.music.Adapter.SongAdapter;
import com.musiclove.music.Model.Song;
import com.musiclove.music.Model.SongsDB;
import com.musiclove.music.Request.SoundcloudApiRequest;
import com.musiclove.music.Utility.Utility;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    private static final String TAG = "APP";
    private RecyclerView recycler;
    private SongAdapter mAdapter;
    private ArrayList<Song> songList;
    private int currentIndex;
    private TextView tb_title, tb_duration, tv_time;
    private ImageView iv_play, iv_next, iv_previous;
    private ProgressBar pb_loader, pb_main_loader;
    private MediaPlayer mediaPlayer;
    private long currentSongLength;
    private SeekBar seekBar;
    public static DatabaseHelper sqLiteHelper;
    private boolean firstLaunch = true;
    private FloatingActionButton fab_search;
    private MobileServiceClient mClient;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mClient = new MobileServiceClient(
                    "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ImageButton butt1 = (ImageButton)findViewById(R.id.but1);
        ImageButton butt2 = (ImageButton)findViewById(R.id.but2);
        ImageButton butt3 = (ImageButton)findViewById(R.id.but3);
        ImageButton butt4 = (ImageButton)findViewById(R.id.but4);
        ImageButton butt5 = (ImageButton)findViewById(R.id.but5);
        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1= new Intent(MainActivity.this, MainActivity.class);
                startActivity(int1);
            }
        });
        butt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int2= new Intent(MainActivity.this, Timeline.class);
                startActivity(int2);
            }
        });
        butt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int3= new Intent(MainActivity.this, Friendslist.class);
                startActivity(int3);
            }
        });
        butt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int4= new Intent(MainActivity.this, SongPostList.class);
                startActivity(int4);
            }
        });
        butt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int5= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(int5);
            }
        });

        myDb = new DatabaseHelper(this);
        initializeViews();
        getSongList("");

        songList = new ArrayList<>();

        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new SongAdapter(getApplicationContext(), songList, new SongAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Song song, int position) {
                createDialog2(song,position);
                /*firstLaunch = false;
                changeSelectedSong(position);
                prepareSong(song);*/


            }



        });
        recycler.setAdapter(mAdapter);

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
                if(currentIndex + 1 < songList.size()){
                    Song next = songList.get(currentIndex + 1);
                    changeSelectedSong(currentIndex+1);
                    prepareSong(next);
                }else{
                    Song next = songList.get(0);
                    changeSelectedSong(0);
                    prepareSong(next);
                }
            }
        });

        handleSeekbar();
        //music controls
        pushPlay();
        pushPrevious();
        pushNext();
        //search
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
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

    private void prepareSong(Song song){

        currentSongLength = song.getDuration();
        pb_loader.setVisibility(View.VISIBLE);
        tb_title.setVisibility(View.GONE);
        iv_play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selector_play));
        tb_title.setText(song.getTitle());
        tv_time.setText(Utility.convertDuration(song.getDuration()));
        String stream = song.getStreamUrl()+"?client_id="+Config.CLIENT_ID;
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(stream);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void insertSongDB(Song song){

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
        recycler = (RecyclerView) findViewById(R.id.recycler);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        tv_time = (TextView) findViewById(R.id.tv_time);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

    }

    public void getSongList(String query){
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        SoundcloudApiRequest request = new SoundcloudApiRequest(queue);
        pb_main_loader.setVisibility(View.VISIBLE);
        request.getSongList(query, new SoundcloudApiRequest.SoundcloudInterface() {
            @Override
            public void onSuccess(ArrayList<Song> songs) {
                currentIndex = 0;
                pb_main_loader.setVisibility(View.GONE);
                songList.clear();
                songList.addAll(songs);
                mAdapter.notifyDataSetChanged();
                mAdapter.setSelectedPosition(0);

            }

            @Override
            public void onError(String message) {
                pb_main_loader.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSelectedSong(int index){
        mAdapter.notifyItemChanged(mAdapter.getSelectedPosition());
        currentIndex = index;
        mAdapter.setSelectedPosition(currentIndex);
        mAdapter.notifyItemChanged(currentIndex);
    }

    private void pushPlay(){
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying() && mediaPlayer != null){
                    iv_play.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.selector_play));
                    mediaPlayer.pause();
                }else{
                    if(firstLaunch){
                        Song song = songList.get(0);
                        changeSelectedSong(0);
                        prepareSong(song);
                    }else{
                        mediaPlayer.start();
                        firstLaunch = false;
                    }
                    iv_play.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.selector_pause));
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
                        Song previous = songList.get(currentIndex - 1);
                        changeSelectedSong(currentIndex - 1);
                        prepareSong(previous);
                    }else{
                        changeSelectedSong(songList.size() - 1);
                        prepareSong(songList.get(songList.size() - 1));
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

                    if(currentIndex + 1 < songList.size()){
                        Song next = songList.get(currentIndex + 1);
                        changeSelectedSong(currentIndex + 1);
                        prepareSong(next);
                    }else{
                        changeSelectedSong(0);
                        prepareSong(songList.get(0));
                    }

                }
            }
        });

    }

    public void createDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setTitle(R.string.rechercher);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText et_search = (EditText) view.findViewById(R.id.et_search);
                String search = et_search.getText().toString().trim();
                if(search.length() > 0){
                    getSongList(search);
                }else{
                    Toast.makeText(MainActivity.this, "Search Successful.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.create().show();


    }

    public void createDialog2(final Song song, final int position){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.sharesong, null);
        builder.setTitle("Select Option...");
        builder.setView(view);
        builder.setPositiveButton("SHARE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences user_details = getSharedPreferences("mysettings",
                        Context.MODE_PRIVATE);

                String user_id = user_details.getString("uid", "defaultvalue");
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();
                SongsDB item = new SongsDB(song.getTitle(),song.getArtist(),song.getArtworkUrl(),String.valueOf(song.getDuration()),song.getStreamUrl(),user_id,df.format(dateobj));

                mClient.getTable(SongsDB.class).insert(item, new TableOperationCallback<SongsDB>() {
                    public void onCompleted(SongsDB entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            // Insert succeeded
                        } else {
                            // Insert failed
                        }
                    }
                });





            }
        });

        builder.setNegativeButton("PLAY",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firstLaunch = false;
                changeSelectedSong(position);
                prepareSong(song);
            }

        });

        final AlertDialog dialog = builder.create();
        dialog.show(); //show() should be called before dialog.getButton().


        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();

        positiveButtonLL.gravity = Gravity.CENTER_HORIZONTAL;

        positiveButton.setLayoutParams(positiveButtonLL);


    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
