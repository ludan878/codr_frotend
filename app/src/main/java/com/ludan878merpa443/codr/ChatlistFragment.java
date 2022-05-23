package com.ludan878merpa443.codr;

import android.app.DownloadManager;
import android.app.ListActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
    private ChatList chatListAdapter;
    private ArrayList<JSONObject> arrayList;
    private ArrayList<String> chatUsernameList;




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
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        chatUsernameList = new ArrayList<>();
        arrayList = new ArrayList<>();
        chatlist = view.findViewById(R.id.chatlist);
        chatListAdapter = new ChatList(getActivity(), people);
        fetchChats();
        chatlist.setAdapter(chatListAdapter);
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setChat(arrayList.toArray().length-i-1);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new ChatFragment()).commit();
            }
        });
    }

    private void setChat(int i) {
        try {
            sessionManager.setChat(arrayList.get(arrayList.toArray().length-i-1).getString("chat_id"));
            sessionManager.setFriend(arrayList.get(arrayList.toArray().length-i-1).getString("user_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchChats() {
        chatListAdapter.clear();
        arrayList.clear();
        QUEUE = Volley.newRequestQueue(getContext());
        String url = "http://codrrip.herokuapp.com/user/chats";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String p = "";
                        JSONObject chat = response.getJSONObject(i);
                        arrayList.add(chat);
                        p = chat.getString("user_id");
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url2 = "http://codrrip.herokuapp.com/user/"+p;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String user_id = response.getString("user_id");
                                    String pfp = response.getString("pfp");
                                    chatListAdapter.add(new Pair<>(user_id, pfp));
                                    Log.d("Mervan", "onResponse: "+pfp+user_id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap();
                                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                                return headers;
                            }
                        };
                        queue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                return headers;
            }
        };

        QUEUE.add(jsonArrayRequest);
    }
}