package com.igio90.fridainjectorexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.igio90.fridainjector.FridaInjector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            FridaInjector fridaInjector = new FridaInjector(this, "frida-inject-12.8.2-android-arm64");
            fridaInjector.inject("com.android.systemui", "agent.js", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
