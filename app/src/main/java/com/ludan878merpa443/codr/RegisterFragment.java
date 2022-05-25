package com.ludan878merpa443.codr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterFragment extends Fragment {
    /**
     * Declares necessary vars.
     */
    private EditText edt_name;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_rpt_password;
    private SessionManager sessionManager;
    private Button btnReg;

    public RegisterFragment() {
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    /**
     * When view is created, all vars are initialized.
     * and Listeners for the button is made.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnReg = getActivity().findViewById(R.id.button_reg);
        edt_email = getActivity().findViewById(R.id.edtEmail);
        edt_name = getActivity().findViewById(R.id.edtUsername);
        edt_password = getActivity().findViewById(R.id.editTextTextPassword);
        edt_rpt_password = getActivity().findViewById(R.id.edtRepeatPassword);
        btnReg.setOnClickListener(new View.OnClickListener() {
            /**
             * When button is pressed, the info from the EditText views are gathered and sent to the
             * createAccount method seen bellow.
             * Also checks if the passwords match alerting the user if not.
             * @param view
             */
            @Override
            public void onClick(View view) {
                String usr = edt_name.getText().toString();
                String pass1 = edt_password.getText().toString();
                String pass2 = edt_rpt_password.getText().toString();
                String email = edt_email.getText().toString();

                if(pass1.equals(pass2)) {
                    createAccount(email, usr, pass1);
                } else {
                    Toast.makeText(getContext(), "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /**
     * Sends a request to make an account to the heroku server with a JsonObjectRequest.
     * If successful it will redirect the user to the login page.
     * @param email
     * @param user
     * @param password
     */
    private void createAccount(String email, String user, String password){
        String url = "http://codrrip.herokuapp.com/register";
        Map<String, String> params = new HashMap();
        params.put("username", user);
        params.put("password", password);
        params.put("email", email);

        JSONObject jsonParams = new JSONObject(params);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sessionManager.setEmail(email);
                sessionManager.setUsername(user);



                // Create new fragment and transaction
                Fragment newFragment = new LoginFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.loginFragmentContainer, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(postReq);


    }








}