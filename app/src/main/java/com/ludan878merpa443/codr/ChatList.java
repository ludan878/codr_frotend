package com.ludan878merpa443.codr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import kotlin.Triple;


public class ChatList extends ArrayAdapter {
    private ArrayList<Pair<String, String>> people = new ArrayList<>();
    private Activity context;
    private TextView textViewName;
    private ImageView imageFlag;
    private StorageReference storageReference;

    public ChatList(Activity context, ArrayList<Pair<String, String>> people) {
        super(context, R.layout.row_item, people);
        this.context = context;
        this.people = people;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String username = people.get(position).first;
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null) {
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        textViewName = (TextView) row.findViewById(R.id.textViewName);
        textViewName.setText(username);
        return row;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }
}
