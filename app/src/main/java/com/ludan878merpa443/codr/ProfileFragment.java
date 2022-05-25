package com.ludan878merpa443.codr;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    /**
     * Declaring all necessary variables.
     */

    private SessionManager sessionManager;
    private Button button_logout;
    private TextView textview_username;
    private Button setDesc;
    private ImageButton imageButton;
    private String currentPhotoPath;
    private StorageReference storageReference;
    private Uri profUri;
    private EditText description;
    private ActivityResultLauncher<Intent> resultLauncher; // Creates a List of Intents

    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * When view is created, all variables is initialized.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDesc = getActivity().findViewById(R.id.button_desc);
        sessionManager = new SessionManager(getContext());
        button_logout = getActivity().findViewById(R.id.button_logout);
        imageButton = getActivity().findViewById(R.id.imageButton);
        description = getActivity().findViewById(R.id.tv_desc);
        storageReference = FirebaseStorage.getInstance().getReference(); // Connection to the firebase database for image use (In this application.)
        textview_username = getActivity().findViewById(R.id.textview_username);
        String sEmail = sessionManager.getEmail();
        textview_username.setText(sEmail);
        fetchDescription();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            /**
             * Handles the result from the intent.
             * First sets the profilepicture uri (profUri)
             * then sets the image of the button to the desired image.
             * @param result
             * The data is contained in the results var.
             */
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    profUri = data.getData();
                    imageButton.setImageURI(profUri);
                    uploadFile(profUri);
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When the button is pressed, an intent to choose the image is started through
             * dispatchTakePictureIntent.
             */
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        setDesc.setOnClickListener(new View.OnClickListener() {
            /**
             * When setDesc is pressed, the image from the sessionManagers profuri is uploaed
             * to the FireBase database, and the descriptiontext is uploaded to the heroku
             * database.
             * @param view
             */
            @Override
            public void onClick(View view) {
                try {
                    updateProfile(sessionManager.getImage(), description.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button_logout.setOnClickListener(new View.OnClickListener() {
            /**
             * When the logout button is pressed, a question to wether the user wants to log out
             * appears through the AlertDialog builder seen bellow. If yes, the person is logged out
             * redirected to the login page, aswell as setting the sessionManagers login setting to
             * false, which will prevent the user from automatically loging in next session.
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Init alert dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                // Set title
                builder.setTitle("Log out");
                // Message
                builder.setMessage("Are you sure you want to Log out?");
                // Set positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Set login false
                        sessionManager.setLogin(false);
                        // Set username empty
                        sessionManager.setEmail("");
                        // Redirect to login activity
                        sessionManager.reset();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                // init Alert
                AlertDialog alertDialog = builder.create();
                // Show alert
                alertDialog.show();
            }
        });
    }

    /**
     * Fetches the description of the user and sets it to the EditText view. (Done through ways
     * explained before.)
     */
    private void fetchDescription() {
        String url = "http://codrrip.herokuapp.com/profile";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    description.setText(response.getString("desc"));
                    setImage(response.getString("pfp"));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
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
     * Fetches the image from the users profile on the database and sets it to
     * image button with glide (Glide downloads the profilepic temporarily and
     * sets the uri of the imagebutton to that uri.)
     */
    private void setImage(String filename) throws IOException {
        StorageReference imgReference = storageReference.child("images/"+filename);
        imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                if(getActivity() != null){
                    Glide.with(getContext()).load(u).into(imageButton);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Makes and queues an intent to pic an image from the gallery app of the user.
     */
    private void dispatchTakePictureIntent() {
        Intent picIntent = new Intent();
        picIntent.setType("image/*");
        picIntent.setAction(Intent.ACTION_GET_CONTENT);
        picIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (picIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                resultLauncher.launch(picIntent); // Some code here might be unnecessary but is included if we wanted to change from this to taking a picture with the camera.
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Updates the profile with the desired profilepic and description.
     * @param filename
     * @param desc
     * @throws IOException
     */
    private void updateProfile(String filename, String desc) throws IOException {
        StorageReference imgReference = storageReference.child("images/"+filename);
        imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(getContext()).load(u).into(imageButton);
                setProfile(filename, desc);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Uploads the filename and description to the heroku database through a JsonObjectRequest seen
     * bellow.
     * @param filename
     * @param desc
     */
    private void setProfile(String filename, String desc) {
        String url = "http://codrrip.herokuapp.com/user/setprof";
        Map<String, String> params = new HashMap();
        params.put("pfp", filename);
        params.put("desc", desc);

        JSONObject jsonParams = new JSONObject(params);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest postReq = new JsonObjectRequest(Request.Method.POST, url, jsonParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sessionManager.setImage(filename);
                Toast.makeText(getContext(), "Successfully set profile description and image.", Toast.LENGTH_SHORT).show();
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
     * Uploads the image from the uri to the FireBase database
     * @param uri
     * Makes a toast telling the user if successfully or not.
     */
    private void uploadFile(Uri uri) {
        String fileName = sessionManager.getEmail();
        sessionManager.setImage(fileName);
        StorageReference imgReference = storageReference.child("images/" + fileName);
        imgReference.delete();
        imgReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(),"Succesfully uploaded profile picture", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

}