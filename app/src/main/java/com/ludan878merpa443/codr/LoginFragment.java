package com.ludan878merpa443.codr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    // Init variables
    Button button_login;
    Button button_register;
    EditText edittext_email;
    EditText edittext_password;
    SessionManager sessionManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /**
         * When view is being created, the
         * @see sessionManager is initialized.
         * It then checks if the person has had a previous login session withot logging out.
         * If it has, it will automatically log the person in without the need of
         * him/her logging in once again.
         */
        sessionManager = new SessionManager(getContext());
        if (sessionManager.getLogin() && getPerms()) {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    /**
     * Checks if the necessary perms has been permitted.
     * If not, it will request them and ask then return false.
     * If it has been permitted, the method will return true.
     */
    private boolean getPerms() {
        final int REQUEST_CODE_PERMISSION = 2;
        String[] mPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA};
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), mPermission[0])
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), mPermission[1])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), mPermission[2])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), mPermission[3])
                            != PackageManager.PERMISSION_GRANTED) {

                Log.e("TAGTAG", "DENIED");
                ActivityCompat.requestPermissions(getActivity(),
                        mPermission, REQUEST_CODE_PERMISSION);
                return false;

                // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
            }
            else
            {
                Log.e("TAGTAG", "GRANTED");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * When view has been created, each of the objects in the view is being set to each var.
     * A listener for each button.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Assign Vars
        button_login = getActivity().findViewById(R.id.button_login);
        button_register = getActivity().findViewById(R.id.button_register);
        edittext_email = getActivity().findViewById(R.id.edittext_email);
        edittext_password = getActivity().findViewById(R.id.edittext_password);

        button_login.setOnClickListener(new View.OnClickListener() {
            /**
             * When the button is clicked it gathers the login info and requests a login from the
             * server. See login_user(email, password).
             *
             */
            @Override
            public void onClick(View view) {
                //Get text from EditText
                String email = edittext_email.getText().toString().trim();
                String password = edittext_password.getText().toString().trim();
                login_user(email, password);

            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            /**
             * When register button is clicked, the user is directed to the register fragment
             * through a fragment transaction.
             */
            @Override
            public void onClick(View view) {
                // Create new fragment and transaction
                Fragment newFragment = new RegisterFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.loginFragmentContainer, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });


    }

    /**
     * @param email
     * @param password
     * With the email and password, a JsonObjectRequest with the assinged parameters through a
     * @see JSONObject, a request is sent to the heroku app requesting the login.
     * If accepted it will set the email in the sessionManager and login to true. (Which is used in the MainActivity
     * to keep a prolonged login-session). The access token is also recieved used for future server-requests.
     */
    private void login_user(String email, String password) {
        if(getPerms()) {
            String url = "http://codrrip.herokuapp.com/login";

            Map<String, String> params = new HashMap();
            params.put("password", password);
            params.put("email", email);

            JSONObject jsonParams = new JSONObject(params);

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    sessionManager.setEmail(email);
                    try {
                        sessionManager.setToken(response.getString("access-token"));
                        Log.d(TAG, "onResponse: " + sessionManager.getToken());
                        sessionManager.setLogin(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // TODO: handle fail
                    }
                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mainIntent);
                    getUsername();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            });
            requestQueue.add(postReq);
        }
    }

    /**
     * Used to make a request much similar to the last one, gathering the username of the user, setting it to the sessionManager
     * for future use.
     * By overriding the "getHeaders()" method, one could add custom headers to the request. f.e. the access key.
     */
    private void getUsername() {
        String url = "http://codrrip.herokuapp.com/profile";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    sessionManager.setUsername(response.getString("user_id"));
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
    }
}