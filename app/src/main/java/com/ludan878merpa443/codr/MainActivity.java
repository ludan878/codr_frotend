package com.ludan878merpa443.codr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * When created, the actionbar is replaced with the icon of "Codr" and it's own background.
         * Then a bottom menu is created
         * Through the case's below, each of the buttons will do a fragmenttransaction, replacing
         * The current fragment in the "Fragmentcontainerview".
         * @see #replaceFragment(Fragment)
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.codr_logo_background));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.codrlogo, null);
        actionBar.setCustomView(v);
        findViewById(R.id.ChatlistFragment).setClickable(false);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.ChatlistFragment:
                    findViewById(R.id.ChatlistFragment).setClickable(false);
                    findViewById(R.id.SwipeFragment).setClickable(true);
                    findViewById(R.id.ProfileFragment).setClickable(true);
                    replaceFragment(new ChatlistFragment());
                    break;
                case R.id.SwipeFragment:
                    findViewById(R.id.ChatlistFragment).setClickable(true);
                    findViewById(R.id.SwipeFragment).setClickable(false);
                    findViewById(R.id.ProfileFragment).setClickable(true);
                    replaceFragment(new SwipeFragment());
                    break;
                case R.id.ProfileFragment:
                findViewById(R.id.ChatlistFragment).setClickable(true);
                    findViewById(R.id.SwipeFragment).setClickable(true);
                    findViewById(R.id.ProfileFragment).setClickable(false);
                    replaceFragment(new ProfileFragment());
                    break;
            };
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        /**
         * Will replace the current fragment in the fragmentcontainerview with the assigned fragment
         * @param fragment is the fragment assigned.
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }
}