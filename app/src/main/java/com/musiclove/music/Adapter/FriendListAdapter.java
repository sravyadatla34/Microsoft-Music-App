package com.musiclove.music.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.musiclove.music.Model.Friend;
import com.musiclove.music.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by rajudatla on 3/10/2017.
 */
public class FriendListAdapter extends BaseAdapter{

    private Context context;
    private int layout;



    private ArrayList<Friend> frndlist;

    public FriendListAdapter(ArrayList<Friend> frndlist, int layout, Context context) {
        this.frndlist = frndlist;
        System.out.println("frndlisy:"+frndlist);
        this.layout = layout;
        this.context = context;

    }




    @Override
    public int getCount() {
        return frndlist.size();
    }

    @Override
    public Object getItem(int position) {
        return frndlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView frndname;

        ImageView frndpic;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        ViewHolder holder = new ViewHolder();
        if(row ==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.frndname = (TextView)row.findViewById(R.id.frndname);
            holder.frndpic = (ImageView)row.findViewById(R.id.frndpic);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }
        Friend frnds = frndlist.get(position);
        String fid = frnds.getFid();
        String imgurl = "https://graph.facebook.com/"
                + fid + "/picture?type=large";
        holder.frndname.setText(frnds.getFname());
        final ViewHolder finalHolder = holder;

        Picasso.with(context).load(imgurl).placeholder(R.drawable.user_default).transform(new CircleTransform()).into(holder.frndpic);

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
