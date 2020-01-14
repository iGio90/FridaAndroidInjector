package com.igio90.fridainjectorexample;

import android.util.Log;

import com.igio90.fridainjector.FridaInterface;

import java.util.Arrays;

public class Interfaces {
    static final class ActivityInterface implements FridaInterface {
        @Override
        public Object call(Object[] args) {
            Log.e("FridaAndroidInject", Arrays.toString(args));
            return null;
        }
    }
}
