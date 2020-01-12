package com.igio90.fridainjector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.File;
import java.io.IOException;

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

    private final File mWrappedAgent;

    private FridaAgent(Builder builder) {
        mWrappedAgent = builder.getWrappedAgent();
    }

    File getWrappedAgent() {
        return mWrappedAgent;
    }

    public static class Builder {
        private final Context mContext;

        private File mWrappedAgent;
        private OnMessage mOnMessage;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder withAgentFromAssets(String agentPath) throws IOException {
            String agent = Utils.readFromFile(mContext.getAssets().open(agentPath));
            return withAgentFromString(agent);
        }

        public Builder withAgentFromString(String agent) {
            mWrappedAgent = new File(mContext.getFilesDir(), "wrapped_agent.js");
            String wrappedAgent = sWrapper + agent;
            Utils.writeToFile(mWrappedAgent, wrappedAgent);
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

        File getWrappedAgent() {
            return mWrappedAgent;
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
