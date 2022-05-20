package com.ludan878merpa443.codr;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Assign Vars
        button_login = getActivity().findViewById(R.id.button_login);
        button_register = getActivity().findViewById(R.id.button_register);
        edittext_email = getActivity().findViewById(R.id.edittext_email);
        edittext_password = getActivity().findViewById(R.id.edittext_password);

        //Init SessionManager
        sessionManager = new SessionManager(getContext());

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get text from EditText
                String email = edittext_email.getText().toString().trim();
                String password = edittext_password.getText().toString().trim();

                if(password.equals("")){
                    edittext_password.setError("Please enter password");
                }
                if(password.equals("root")){ // CHECK PASSWORD HERE instead of \\password.equals
                    //Store login in session
                    sessionManager.setLogin(true);
                    //Store username in session
                    sessionManager.setEmail(email);
                    // Start mainactivity
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
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
}