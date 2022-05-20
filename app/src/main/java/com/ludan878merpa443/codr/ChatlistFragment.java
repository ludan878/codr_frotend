package com.ludan878merpa443.codr;

import android.app.DownloadManager;
import android.app.ListActivity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Triple;

public class ChatlistFragment extends Fragment {

    private ListView chatlist;
    private ArrayList<Triple<String, String, Integer>> people = new ArrayList<>();
    private RequestQueue QUEUE;




    public ChatlistFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        QUEUE = Volley.newRequestQueue(getContext());
        String url = "";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                for (int i = 0; i < response.length()-1; i++) {
                    
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        chatlist = view.findViewById(R.id.chatlist);
        ChatList chatListAdapter = new ChatList(getActivity(), people);
        chatlist.setAdapter(chatListAdapter);

        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), people.get(i).component2(), Toast.LENGTH_SHORT).show();
                chatListAdapter.add(new Triple<>("https://www.seekpng.com/png/detail/966-9665493_my-profile-icon-blank-profile-image-circle.png", "Ludvig Anderstedt", 20));
                chatlist.setSelection(chatListAdapter.getCount() - 1);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}