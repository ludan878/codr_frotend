package com.ludan878merpa443.codr;

import static android.content.ContentValues.TAG;

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
import android.widget.ArrayAdapter;
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
    /**
     * Declares necessary vars.
     */
    private SessionManager sessionManager;
    private ListView chatlist;
    private RequestQueue QUEUE;
    private ArrayAdapter<String> chatListAdapter;
    private JSONObject chatsJson;
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

    /**
     * When view is created, all vars are initialized and listeners made for the buttons.
     * The chatlist is created with a normal simple_list_item_1 list layout from the chatUsernameList.
     * Through fetchChats, it is then updated with the chats of the users profile.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        chatUsernameList = new ArrayList<>();
        chatsJson = new JSONObject();
        chatlist = view.findViewById(R.id.chatlist);
        chatListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, chatUsernameList);
        fetchChats();
        chatlist.setAdapter(chatListAdapter);
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * When a chat is clicked, its position will be used to get the items username
             * It then initializes and sends the user to a chat with whom the user clicked on.
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Toast.makeText(getContext(), chatListAdapter.getItem(i) + chatsJson.getString(chatListAdapter.getItem(i)), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setChat(chatListAdapter.getItem(i)); // User_id from itemlist
                Log.d(TAG, "onItemClick: "+sessionManager.getFriend());
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new ChatFragment()).commit();
            }
        });
    }

    /**
     * Sets the chat in the sessionManager, used in the manner as of a LiveViewModel.
     * @param user_id
     */
    private void setChat(String user_id) {
        sessionManager.setFriend(user_id);
        try {
            sessionManager.setChat(chatsJson.getString(user_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gathers the chats from the database of the user, then adds them, one by one to the adapter.
     * Might be some unecessary requests made, could optimize the code.
     */
    private void fetchChats() {
        QUEUE = Volley.newRequestQueue(getContext());
        String url = "http://codrrip.herokuapp.com/user/chats";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String p = "";
                        JSONObject chat = response.getJSONObject(i);
                        p = chat.getString("user_id");
                        Log.d(TAG, "onResponse: "+chat.getString("chat_id") + p);
                        chatsJson.put(p, chat.getString("chat_id"));
                        chatListAdapter.add(p);
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