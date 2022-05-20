package com.ludan878merpa443.codr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import kotlin.Triple;


public class ChatList extends ArrayAdapter {
    private ArrayList<Triple<String, String, Integer>> people = new ArrayList<>();
    private Activity context;

    public ChatList(Activity context, ArrayList<Triple<String, String, Integer>> people) {
        super(context, R.layout.row_item, people);
        this.context = context;
        this.people = people;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String username = people.get(position).component2();
        String imageid = people.get(position).component1();
        Integer age = people.get(position).component3();
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null) {
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
        TextView textViewAge = (TextView) row.findViewById(R.id.textViewAge);
        ImageView imageFlag = (ImageView) row.findViewById(R.id.imageViewFlag);

        textViewName.setText(username);
        textViewAge.setText(String.valueOf(age));
        imageFlag.setImageBitmap(getImageFromLink(imageid));
        return row;
    }

    private Bitmap getImageFromLink(String imageid) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.codr_logo_foreground);
        /*try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageid).getContent());
        } catch (IOException e) {
            Log.d(TAG, "getImageFromLink: "+e.getMessage());
        } */

        return bitmap;
    }

}
