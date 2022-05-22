package com.ludan878merpa443.codr;

import android.app.DownloadManager;
import android.app.ListActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Triple;

public class ChatlistFragment extends Fragment {
    private SessionManager sessionManager;
    private ListView chatlist;
    private ArrayList<Pair<String, String>> people = new ArrayList<>();
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
        sessionManager = new SessionManager(getContext());
        chatlist = view.findViewById(R.id.chatlist);
        ChatList chatListAdapter = new ChatList(getActivity(), people);
        chatlist.setAdapter(chatListAdapter);

        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchChats();
    }

    private void fetchChats() {
        QUEUE = Volley.newRequestQueue(getContext());
        String url = "http://codrrip.herokuapp.com/user/chats";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 1; i < response.length(); i++) {
                    try {
                        String p = "";
                        JSONObject chat = response.getJSONObject(i);
                        if(chat.getString("user1_id")==response.getString(0)){
                            p = chat.getString("user2_id");
                        } else {
                            p = chat.getString("user1_id");
                        }
                        String url2 = "http://codrrip.herokuapp.com/user/"+p;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url2, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String user_id = response.getString("user_id");
                                    String pfp = response.getString("pfp");
                                    people.add(new Pair<String,String>(pfp, user_id));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                people.notifyAll();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = null;
                try {
                    token = new JSONObject(sessionManager.getToken()).getString("access-token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> headers = new HashMap();
                headers.put("access-token", token);
                return headers;
            }
        };
    }
}