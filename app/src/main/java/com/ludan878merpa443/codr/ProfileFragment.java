package com.ludan878merpa443.codr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    SessionManager sessionManager;
    Button button_logout;
    TextView textview_username;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        button_logout = getActivity().findViewById(R.id.button_logout);
        textview_username = getActivity().findViewById(R.id.textview_username);
        String sEmail = sessionManager.getEmail();
        textview_username.setText(sEmail);

        button_logout.setOnClickListener(new View.OnClickListener() {
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
}