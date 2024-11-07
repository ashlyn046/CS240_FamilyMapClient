package com.bignerdranch.android.client;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find in text (8.12)
        FragmentManager fm = this.getSupportFragmentManager(); //take out this!!!
        Fragment currFragment = fm.findFragmentById(R.id.fragment_container);

        if(currFragment == null){
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setListener(this);

            fm.beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();
        }
        else{
            if(currFragment instanceof LoginFragment){

                //casting it as a login fragment and setting the listener
                //currFragment = (LoginFragment) currFragment;
                ((LoginFragment) currFragment).setListener(this);
            }
        }
    }

    @Override
    public void logUserIn(){

        //find in text (8.12)
        FragmentManager fm = getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();

        fm.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit();
    }
}