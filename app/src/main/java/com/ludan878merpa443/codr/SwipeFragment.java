package com.ludan878merpa443.codr;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class SwipeFragment extends Fragment {
    /**
     * Declares necessary vars.
     */
    private SessionManager sessionManager;
    private TextView userName;
    private TextView userDescription;
    private ImageView userImage;
    private TextView favCount;
    private Button favButton;
    private Button btnNo;
    private Button btnYes;
    private StorageReference storageReference;
    int currentUserNum;


    public SwipeFragment() {
        // Required empty public constructor
    }

    public static SwipeFragment newInstance(String param1, String param2) {
        SwipeFragment fragment = new SwipeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

    /**
     * Initializes all vars and assigns listeners to the buttons.
     * @param v
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        storageReference = FirebaseStorage.getInstance().getReference();
        sessionManager = new SessionManager(getContext());
        btnNo = getActivity().findViewById(R.id.btnNo);
        btnYes = getActivity().findViewById(R.id.btnYes);
        favButton = getActivity().findViewById(R.id.buttonFav);
        userName = getActivity().findViewById(R.id.tv_username);
        favCount = getActivity().findViewById(R.id.tvFavCount);
        userDescription = getActivity().findViewById(R.id.tv_d);
        userImage = getActivity().findViewById(R.id.ivPfp);
        getUserList();
        favButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Favorites the user when clicked, works as a like button
             * @param view
             */
            @Override
            public void onClick(View view) {
                try {
                    favUser(sessionManager.getCurrent());
                    likeUser(sessionManager.getCurrent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            /**
             * When clicked it will go the next user in the feed.
             * @param view
             */
            @Override
            public void onClick(View view) {
                nextUser();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            /**
             * When clicked it will like the user with the likeUser() method.
             * @param view
             */
            @Override
            public void onClick(View view) {
                try {
                    likeUser(sessionManager.getCurrent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Does a JsonObjectRequest to the heroku server to like the selected user.
     * Also triggers the nextUser method.
     * @param user_id
     * @throws JSONException
     */
    private void likeUser(String user_id) throws JSONException {
        String url = "http://codrrip.herokuapp.com/user/like";

        Map<String, String> params = new HashMap();
        params.put("target", user_id);

        JSONObject jsonParams = new JSONObject(params);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: Success");
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

        nextUser();
    }

    /**
     * Does a JsonObjectRequest to the heroku server to favorite the selected user.
     * @param user_id
     * @throws JSONException
     */
    private void favUser(String user_id) throws JSONException {
        String url = "http://codrrip.herokuapp.com/user/"+user_id+"/favorite";

        Map<String, String> params = new HashMap();
        params.put("target", user_id);

        JSONObject jsonParams = new JSONObject(params);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: Success");
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

    /**
     * Goes to the next user and updates the feed.
     */
    private void nextUser() {
        currentUserNum ++;
        getUserList();
    }

    /**
     * Makes a JsonArrayRequest to the server, fetching all users that the user hasnt liked.
     * Sets the current user to the screen in the list. Fetching the users description and profilepic.
     */
    private void getUserList(){
        String url = "http://codrrip.herokuapp.com/users";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest getReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String uName = "";
                String uDesc = "     No users left to like!";
                int fCount = 0;
                try {
                    if(response.length()>0){
                        String pfp = response.getJSONObject(currentUserNum%(response.length())).getString("pfp");
                        uName = response.getJSONObject(currentUserNum%(response.length())).getString("user_id");
                        uDesc = response.getJSONObject(currentUserNum%(response.length())).getString("desc");
                        fCount = response.getJSONObject(currentUserNum%(response.length())).getInt("fav_count");
                        setImage(pfp);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                sessionManager.setCurrent(uName);
                favCount.setText(String.valueOf(fCount));
                userName.setText(uName);
                userDescription.setText(uDesc);
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
        requestQueue.add(getReq);
    }

    private void setImage(String filename) throws IOException {
        StorageReference imgReference = storageReference.child("images/"+filename);
        imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                if(getActivity() != null) {
                    Glide.with(getContext()).load(u).into(userImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}