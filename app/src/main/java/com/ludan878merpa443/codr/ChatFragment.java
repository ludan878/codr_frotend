package com.ludan878merpa443.codr;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatFragment extends Fragment {

    private ArrayList<Pair<String, String>> messages;
    private SessionManager sessionManager;
    private StorageReference storageReference;
    private ListView messagesView;
    private Button btnRefresh;
    private Button btnUnfriend;
    private Button sendMessage;
    private EditText messageText;
    private ImageView ivUserpfp;
    private MessageList messageListAdapter;
    private TextView chatUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        storageReference = FirebaseStorage.getInstance().getReference();
        messagesView = getActivity().findViewById(R.id.messageList);
        chatUsername = getActivity().findViewById(R.id.chatUsername);
        btnRefresh = getActivity().findViewById(R.id.buttonRefresh);
        sendMessage = getActivity().findViewById(R.id.buttonSend);
        messageText = getActivity().findViewById(R.id.textSend);
        btnUnfriend = getActivity().findViewById(R.id.buttonUnfriendbuttonUnfriend);
        ivUserpfp = getActivity().findViewById(R.id.chatPfp);
        messages = new ArrayList<>();
        messageListAdapter = new MessageList(getActivity(), messages);
        messagesView.setAdapter(messageListAdapter);
        fetchMessages();
        setUserprofile();
        btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeChat();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchMessages();
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIt();
                messageText.setText("");
                fetchMessages();
            }
        });

    }

    private void removeChat(){
        String url = "http://codrrip.herokuapp.com/chat/delete/"+sessionManager.getFriend();
        Map<String, String> params = new HashMap();

        JSONObject jsonParams = new JSONObject(params);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest postReq = new JsonArrayRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: Success");
                getChildFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SwipeFragment());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                return headers;
            }
        };
        requestQueue.add(postReq);
    }

    private void setUserprofile(){
        String friend = sessionManager.getFriend();
        chatUsername.setText(friend);
        String url = "http://codrrip.herokuapp.com/user/"+friend;

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    setImage(response.getString("pfp"));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                return headers;
            }
        };;
        queue.add(jsonObjectRequest);
    }

    private void setImage(String filename) throws IOException {
        StorageReference imgReference = storageReference.child("images/"+filename);
        imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(getContext()).load(u).into(ivUserpfp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void sendIt() {
        String message = messageText.getText().toString();
        String url = "http://codrrip.herokuapp.com/chat/"+sessionManager.getChat()+"/message";
        JSONObject params = new JSONObject();
        /*try {
            params.put("message", message.toString());
            params.put("user_id", sessionManager.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: message sent");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                headers.put("message", message.toString());
                headers.put("user_id", sessionManager.getUsername());
                return headers;
            }
        };
        requestQueue.add(postReq);
        fetchMessages();
    }

    private void fetchMessages() {
        messageListAdapter.clear();
        String url = "http://codrrip.herokuapp.com/chat/"+sessionManager.getChat();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("MERV", "onResponse: "+response.length());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject message = response.getJSONObject(i);
                        messageListAdapter.add(new Pair<>(message.getString("user_id"),message.getString("message")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer "+sessionManager.getToken());
                return headers;
            }
        };
        queue.add(jsonArrayRequest);
    }

}