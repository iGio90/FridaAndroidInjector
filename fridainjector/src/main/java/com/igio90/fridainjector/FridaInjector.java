package com.igio90.fridainjector;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

import com.chrisplus.rootmanager.RootManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class FridaInjector {
    private final Context mContext;

    private File mInjector;

    public FridaInjector(Context context, String injectorBinaryAssetPath) throws IOException {
        mContext = context;

        if (!RootManager.getInstance().hasRooted()) {
            throw new RuntimeException("must run on a rooted device");
        }
        if (!RootManager.getInstance().obtainPermission()) {
            throw new RuntimeException("failed to obtain root permissions");
        }

        extractInjectorIfNeeded(injectorBinaryAssetPath);
    }

    public void inject(final String packageName, String agentAssetPath, boolean spawn) throws IOException {
        if (mInjector == null) {
            throw new RuntimeException("did you forget to call init()?");
        }

        final File agent = new File(mContext.getFilesDir(), "agent.js");
        extractAsset(agentAssetPath, agent);
        RootManager.getInstance().runCommand("chmod 777 " + agent.getPath());

        if (!RootManager.getInstance().isProcessRunning(packageName)) {
            spawn = true;
        }

        if (spawn) {
            Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
            RootManager.getInstance().killProcessByName(packageName);
            new Thread(() -> {
                long start = System.currentTimeMillis();
                while (!RootManager.getInstance().isProcessRunning(packageName)) {
                    try {
                        Thread.sleep(250);

                        if (System.currentTimeMillis() - start >
                                TimeUnit.SECONDS.toMillis(5)) {
                            throw new RuntimeException("wait timeout for process spawn");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                inject(packageName, agent.getPath());
            }).start();

            if (launchIntent != null) {
                mContext.startActivity(launchIntent);
            } else {
                // are we targeting a system app?
                // systemui does auto-respawn. Let's see further cases
                // todo: handle cases here
            }
        } else {
            inject(packageName, agent.getPath());
        }
    }

    private void inject(String packageName, String agentPath) {
        RootManager.getInstance().runCommand(mInjector.getPath() + " -n " + packageName +
                " -s " + agentPath + " -e");
    }

    private void extractInjectorIfNeeded(String name) throws IOException {
        File injectorPath = new File(mContext.getFilesDir(), "injector");
        mInjector = new File(injectorPath, name);

        if (!injectorPath.exists()) {
            injectorPath.mkdir();
        } else {
            File[] files = injectorPath.listFiles();
            if (files != null && files.length > 0) {
                if (files[0].getName().equals(name)) {
                    return;
                }
                files[0].delete();
            }
        }

        extractAsset(name, mInjector);
        RootManager.getInstance().runCommand("chmod 777 " + mInjector.getPath());
    }

    private void extractAsset(String assetName, File dest) throws IOException {
        AssetManager assetManager = mContext.getAssets();
        InputStream in = assetManager.open(assetName);
        OutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }
}