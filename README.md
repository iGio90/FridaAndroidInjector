## Frida Injector for Android

is an Android library usable for production applications that inject your agent into apps and processes.
It obviously requires root on the devices running our final product.
I have some apps in the Google Play Store shipped with this, injecting js using frida in the Android audio-server and systemui,
obviously in-line with the Google policy, aka, don't touch copyrighted/3rd party code.

The things are very very easy:

#### Setup

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
dependencies {
        implementation 'com.github.iGio90:FridaAndroidInjector:+'
}
```

#### How to:

- create assets folder in your project
- put your updated frida injector binaries (I got further plans to kill this step and handle releases with binary updater inside the library) in the assets folder
    -> You can safely push a new updated binary. The library will remove the old one and extract the binary updated only once
- at this point you will need a similar switch case to identify the right arch to use:

```java
    String fridaInjectorBinaryAssetName = "";
    switch (getArch()) {
        case "arm":
            fridaInjectorBinaryAssetName = "arm_frida_injector"
            break;
        case "arm64":
            fridaInjectorBinaryAssetName = "arm64_frida_injector"
            break;
        case "x86":
            fridaInjectorBinaryAssetName = "x86_frida_injector"
            break;
        case "x86_64":
            fridaInjectorBinaryAssetName = "x86_64_frida_injector"
            break;
        default:
            return;
    }

    ...


    public static String getArch() {
        for (String androidArch : Build.SUPPORTED_ABIS) {
            switch (androidArch) {
                case "arm64-v8a": return "arm64";
                case "armeabi-v7a": return "arm";
                case "x86_64": return "x86_64";
                case "x86": return "x86";
            }
        }

        throw new RuntimeException("Unable to determine arch from Build.SUPPORTED_ABIS =  " +
                Arrays.toString(Build.SUPPORTED_ABIS));
    }
```

- add your agent.js in the assets folder as well and then just:

```java
try {
    FridaInjector fridaInjector = new FridaInjector(this, "fridaInjectorBinaryAssetName");
    fridaInjector.inject("com.android.systemui", "agent.js", true);
} catch (IOException e) {
    e.printStackTrace();
}
```

The example apk [here](https://github.com/igio90/FridaAndroidInjector/tree/master/example.apk) will inject into the systemui and turn you clock black. Because black is black.