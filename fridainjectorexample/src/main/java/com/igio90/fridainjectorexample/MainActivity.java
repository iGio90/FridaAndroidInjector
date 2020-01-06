package com.igio90.fridainjectorexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.igio90.fridainjector.FridaAgent;
import com.igio90.fridainjector.FridaInjector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // build an instance of FridaInjector providing binaries for arm/arm64/x86/x86_64 as needed
            // assets/frida-inject-12.8.2-android-arm64
            FridaInjector fridaInjector = new FridaInjector.Builder(this)
                    .withArm64Injector("frida-inject-12.8.2-android-arm64")
                    .build();

            // build an instance of FridaAgent
            FridaAgent fridaAgent = FridaAgent.fromAsset(this, "agent.js");

            // inject systemUi
            fridaInjector.inject(fridaAgent, "com.android.systemui", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
