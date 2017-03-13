package com.musiclove.music.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Transformation;
import com.musiclove.music.Model.Post_Song;
import com.musiclove.music.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;


/**

 * Created by sravya on 19-02-2017.
 */

public class SongPostListAdapter extends BaseAdapter {


    private Context context;
    private int layout;


    private int PositionSelected = 0;

    private OnItemClickListener listener;
    private ArrayList<Post_Song> songlist;

    public SongPostListAdapter(ArrayList<Post_Song> songlist, int layout, Context context) {
        this.songlist = songlist;
        this.layout = layout;
        this.context = context;

    }

    public void setPositionSelected(int position)
    {
        PositionSelected = position;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return songlist.size();
    }

    @Override
    public Post_Song getItem(int position) {
        return songlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView name,time,title,artist;
        LinearLayout song_pic;
        TextView stream,duration,id;
        ImageView userImage;

    }





    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();
        if(row ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.artist = (TextView)row.findViewById(R.id.artist);
            holder.name = (TextView)row.findViewById(R.id.user_name);
            holder.time = (TextView)row.findViewById(R.id.time);
            holder.title = (TextView)row.findViewById(R.id.song_title);
            holder.song_pic = (LinearLayout)row.findViewById(R.id.track_pic);
            holder.userImage =(ImageView) row.findViewById(R.id.user_pic);
            holder.stream = (TextView)row.findViewById(R.id.streamurl);
            holder.duration = (TextView)row.findViewById(R.id.duration);
            holder.id = (TextView)row.findViewById(R.id.sid);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }


        Post_Song posts = songlist.get(position);



        String id = posts.getUid();
        System.out.println("YOYOYOY"+id+" ,  "+posts.getUname());
        String imgurl = "https://graph.facebook.com/"
                + id + "/picture?type=large";
        holder.artist.setText(posts.getArtist());
        holder.name.setText(posts.getUname());
        holder.time.setText(posts.getTime());
        holder.title.setText(posts.getTitle());
        holder.stream.setText(posts.getStreamurl());
        holder.duration.setText(posts.getDuration());
        holder.id.setText(posts.getId());
        final ViewHolder finalHolder = holder;
        Picasso.with(context).load(posts.getArtwork()).placeholder(R.drawable.music_placeholder).into(new Target(){

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                finalHolder.song_pic.setBackground(new BitmapDrawable(context.getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });
        Picasso.with(context).load(imgurl).placeholder(R.drawable.user_default).transform(new CircleTransform()).into(holder.userImage);

        //new LoadProfileImage(holder.userImage).execute(posts.getUid());
        return row;
    }



    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }


}




