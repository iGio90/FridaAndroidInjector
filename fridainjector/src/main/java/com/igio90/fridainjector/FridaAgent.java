package com.igio90.fridainjector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class FridaAgent {

    private static final String sWrapper = "" +
            "console.log = function() {" +
            "    var args = arguments;" +
            "    Java.performNow(function() {" +
            "        for (var i=0;i<args.length;i++) {" +
            "            Java.use('android.util.Log').e('FridaAndroidInject', args[i].toString());" +
            "        }" +
            "    });" +
            "};" +
            "" +
            "Java['send'] = function(data) {" +
            "    Java.performNow(function () {" +
            "        var Intent = Java.use('android.content.Intent');" +
            "        var ActivityThread = Java.use('android.app.ActivityThread');" +
            "        var Context = Java.use('android.content.Context');" +
            "        var ctx = Java.cast(ActivityThread.currentApplication().getApplicationContext(), Context);" +
            "        var intent = Intent.$new('com.frida.injector.SEND');" +
            "        intent.putExtra('data', JSON.stringify(data));" +
            "        ctx.sendBroadcast(intent);" +
            "    });" +
            "}" +
            "\n";

    static final String sRegisterClassLoaderAgent = "" +
            "Java.performNow(function() {" +
            "    var app = Java.use('android.app.ActivityThread').currentApplication();" +
            "    var context = app.getApplicationContext();" +
            "    var pm = context.getPackageManager();" +
            "    var ai = pm.getApplicationInfo(context.getPackageName(), 0);" +
            "    var apkPath = ai.publicSourceDir.value;" +
            "    apkPath = apkPath.substring(0, apkPath.lastIndexOf('/')) + '/xd.apk';" +
            "    var cl = Java.use('dalvik.system.DexClassLoader').$new(" +
            "            apkPath, context.getCacheDir().getAbsolutePath(), null," +
            "            context.getClass().getClassLoader());" +
            "    Java.classFactory['xd_loader'] = cl;" +
            "});" +
            "\n";

    private final Context mContext;
    private final String mWrappedAgent;
    private final LinkedHashMap<String, Class<? extends FridaInterface>> mInterfaces =
            new LinkedHashMap<>();

    private FridaAgent(Builder builder) {
        mContext = builder.getContext();
        mWrappedAgent = builder.getWrappedAgent();
    }

    String getWrappedAgent() {
        return mWrappedAgent;
    }

    LinkedHashMap<String, Class<? extends FridaInterface>> getInterfaces() {
        return mInterfaces;
    }


    PackageManager getPackageManager() {
        return mContext.getPackageManager();
    }

    String getPackageName() {
        return mContext.getPackageName();
    }

    File getFilesDir() {
        return mContext.getFilesDir();
    }

    public void registerInterface(String cmd, Class<? extends FridaInterface> fridaInterface) {
        mInterfaces.put(cmd, fridaInterface);
    }

    public static class Builder {
        private final Context mContext;

        private String mWrappedAgent;
        private OnMessage mOnMessage;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder withAgentFromAssets(String agentPath) throws IOException {
            String agent = Utils.readFromFile(mContext.getAssets().open(agentPath));
            return withAgentFromString(agent);
        }

        public Builder withAgentFromString(String agent) {
            mWrappedAgent = sWrapper + agent;
            return this;
        }

        public Builder withOnMessage(OnMessage onMessage) {
            mOnMessage = onMessage;
            return this;
        }

        public FridaAgent build() {
            if (mWrappedAgent == null) {
                throw new RuntimeException("no agent specified");
            }

            if (mOnMessage != null) {
                mContext.registerReceiver(new DataBroadcast(mOnMessage),
                        new IntentFilter("com.frida.injector.SEND"));
            }

            return new FridaAgent(this);
        }

        String getWrappedAgent() {
            return mWrappedAgent;
        }

        Context getContext() {
            return mContext;
        }
    }

    private static class DataBroadcast extends BroadcastReceiver {
        private final OnMessage mOnMessage;

        DataBroadcast(OnMessage onMessage) {
            mOnMessage = onMessage;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            mOnMessage.onMessage(data);
        }
    }
}
