package com.bignerdranch.android.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    public static final String CURR_EVENT_KEY = "CurrEventKey";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        String currEventId = intent.getStringExtra(CURR_EVENT_KEY);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.eventFragmentFrameLayout);

        if(Objects.isNull(fragment))
        {
            fragment = create_map(currEventId);
            fragmentManager.beginTransaction().replace(R.id.eventFragmentFrameLayout, fragment)
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();

        if (!Objects.isNull(actionBar))
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_back_arrow_24);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    private Fragment create_map(String currEventId)
    {
        MapFragment mapFragment = new MapFragment();
        Bundle arguments = new Bundle();
        arguments.putString(CURR_EVENT_KEY, currEventId);
        mapFragment.setArguments(arguments);

        return mapFragment;
    }
}