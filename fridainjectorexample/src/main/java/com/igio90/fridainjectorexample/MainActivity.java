package com.igio90.fridainjectorexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.igio90.fridainjector.FridaAgent;
import com.igio90.fridainjector.FridaInjector;
import com.igio90.fridainjector.OnMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnMessage {
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
            FridaAgent fridaAgent = new FridaAgent.Builder(this)
                    .withAgentFromAssets("agent.js")
                    .withOnMessage(this)
                    .build();
            // inject systemUi
            fridaInjector.inject(fridaAgent, "com.android.systemui", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            Log.e("FridaInjector", "SystemUI pid: " + object.getString("pid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
