package com.igio90.fridainjector;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class FridaAgent {

    private final File mAgent;

    public static FridaAgent fromAsset(Context context, String assetPath) throws IOException {
        FridaAgent fridaAgent = new FridaAgent(context);
        Utils.extractAsset(context, assetPath, fridaAgent.getAgent());
        return fridaAgent;
    }

    private FridaAgent(Context context) {
        mAgent = new File(context.getFilesDir(), "agent.js");
    }

    File getAgent() {
        return mAgent;
    }
}
