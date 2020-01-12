console.log = function() {
    var args = arguments;
    Java.performNow(function() {
        for (var i=0;i<args.length;i++) {
            Java.use('android.util.Log').e('FridaAndroidInject', args[i].toString());
        }
    });
}

Java['send'] = function(data) {
    Java.performNow(function () {
        var Intent = Java.use('android.content.Intent');
        var ActivityThread = Java.use('android.app.ActivityThread');
        var Context = Java.use('android.content.Context');
        var ctx = Java.cast(ActivityThread.currentApplication().getApplicationContext(), Context);
        var intent = Intent.$new('com.frida.injector.SEND');
        intent.putExtra('data', JSON.stringify(data));
        ctx.sendBroadcast(intent);
    });
}