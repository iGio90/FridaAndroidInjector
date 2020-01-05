function log(what) {
    Java.performNow(function() {
        Java.use('android.util.Log').e("tweakerinj", what.toString());
    })
}

Java.performNow(function() {
    var TextClock = Java.use("com.android.systemui.statusbar.policy.Clock");
    var Color = Java.use("android.graphics.Color");

    TextClock.setTextColor.overloads[0].implementation = function() {
        if (this.$className === 'com.android.systemui.statusbar.policy.Clock') {
            arguments[0] = Color.BLACK.value;
        }
        return this.setTextColor.apply(this, arguments);
    }
});
