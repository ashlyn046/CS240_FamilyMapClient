package com.bignerdranch.android.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private Line line;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();

        if (!Objects.isNull(actionBar))
        {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_back_arrow_24);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }

        Switch lifeStoryLines = findViewById(R.id.lifeStoryLineSwitch);
        lifeStoryLines.setChecked(line.getLifeStoryLines());
        lifeStoryLines.setOnClickListener(v ->
                line.setLifeStoryLines(!line.getLifeStoryLines()));

        Switch familyTreeLines = findViewById(R.id.familyTreeLineSwitch);
        familyTreeLines.setChecked(line.getFamilyTreeLines());
        familyTreeLines.setOnClickListener(v ->
                line.setFamilyTreeLines(!line.getFamilyTreeLines()));

        Switch spouseLines = findViewById(R.id.spouseLineSwitch);
        spouseLines.setChecked(line.getSpouseLines());
        spouseLines.setOnClickListener(v ->
                line.setSpouseLines(!line.getSpouseLines()));

        Switch fatherSide = findViewById(R.id.fatherSideSwitch);
        fatherSide.setChecked(DataCache.getFatherValues());
        fatherSide.setOnClickListener(v ->
        {
            DataCache.setFatherValues(!DataCache.getFatherValues());
            DataCache.determineCurrentEvents();
        });

        Switch motherSide = findViewById(R.id.motherSideSwitch);
        motherSide.setChecked(DataCache.getMotherValues());
        motherSide.setOnClickListener(v ->
        {
            DataCache.setMotherValues(!DataCache.getMotherValues());
            DataCache.determineCurrentEvents();
        });

        Switch maleEvents = findViewById(R.id.maleEventsSwitch);
        maleEvents.setChecked(DataCache.getMaleEvents());
        maleEvents.setOnClickListener(v ->
        {
            DataCache.setMaleEvents(!DataCache.getMaleEvents());
            DataCache.determineCurrentEvents();
        });

        Switch femaleEvents = findViewById(R.id.femaleEventsSwitch);
        femaleEvents.setChecked(DataCache.getFemaleEvents());
        femaleEvents.setOnClickListener(v -> {
            DataCache.setFemaleEvents(!DataCache.getFemaleEvents());
            DataCache.determineCurrentEvents();
        });

        LinearLayout logout = findViewById(R.id.logout);
        logout.setOnClickListener(v ->
        {
            DataCache.clear();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
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
}