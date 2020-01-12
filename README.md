## Frida Injector for Android

is a library allowing you to inject frida agents from an Android application.

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

```java
    try {
        // build an instance of FridaInjector providing binaries for arm/arm64/x86/x86_64 as needed
        // assets/frida-inject-12.8.2-android-arm64
        FridaInjector fridaInjector = new FridaInjector.Builder(this)
                .withArm64Injector("frida-inject-12.8.2-android-arm64")
                .build();

        // build an instance of FridaAgent
        FridaAgent fridaAgent = new FridaAgent.Builder(this)
                .withAgentFromAssets("agent.js")
                .build();

        // inject systemUi
        fridaInjector.inject(fridaAgent, "com.android.systemui", true);
    } catch (IOException e) {
        e.printStackTrace();
    }
````

#### Implementing "on('message')"

```java
    public class MainActivity extends AppCompatActivity implements OnMessage {
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
```

```java
    FridaAgent fridaAgent = new FridaAgent.Builder(this)
            .withAgentFromAssets("agent.js")
            .withOnMessage(this)
            .build();
```

and from your agent

```javascript
    Java.send({'pid': Process.id});
```

The example apk [here](https://github.com/igio90/FridaAndroidInjector/tree/master/example.apk) is built and ready to try. You will see it works! (only arm64).