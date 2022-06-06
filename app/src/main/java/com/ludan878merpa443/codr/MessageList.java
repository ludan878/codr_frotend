package com.ludan878merpa443.codr;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;


public class MessageList extends ArrayAdapter {
    private ArrayList<Pair<String, String>> people = new ArrayList<>();
    private Activity context;
    private TextView messageUser;
    private TextView message;
    private StorageReference storageReference;
    private SessionManager sessionManager;

    public MessageList(Activity context, ArrayList<Pair<String, String>> message) {
        super(context, R.layout.message_item, message);
        this.context = context;
        this.people = message;
    }


    /**
     * Makes a custom ListView with custom rows showing both the user and the message.
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String username = people.get(position).second;
        String m = people.get(position).first;
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null) {
            row = inflater.inflate(R.layout.message_item, null, true);
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        messageUser = (TextView) row.findViewById(R.id.message);
        message = (TextView) row.findViewById(R.id.messageUser);
        sessionManager = new SessionManager(getContext());
        messageUser.setText(username);
        message.setText(m);

        return row;
    }

}
