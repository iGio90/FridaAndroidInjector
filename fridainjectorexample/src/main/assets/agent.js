function log(what) {
    Java.performNow(function() {
        Java.use('android.util.Log').e("FridaAndroidInject", what.toString());
    });
}

Java.performNow(function() {
    var TextView = Java.use("android.widget.TextView");
    TextView.setText.overloads[0].implementation = function() {
        arguments[0] = Java.use("java.lang.String").$new("It works!");
        return this.setText.apply(this, arguments);
    }
});

setTimeout(function() {
    Java.perform(function() {
        var app = Java.use("android.app.Activity");
        app.onResume.overloads[0].implementation = function() {
            this.onResume.apply(this, arguments);
            Java.activityInterface(Java.cast(this, app), "otherArg1", "otherArg2");
        };
    });
}, 2000);

setTimeout(function() {
    Java.send({'pid': Process.id});
}, 5 * 1000);
