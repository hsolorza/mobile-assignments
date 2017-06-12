package com.example.android.weatherwithpreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.weatherwithpreferences.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("In settings Activity!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
