package com.simplicity.maged.mccobjectdetection.components;

import android.app.Application;

//Don't forget to add it to your manifest by doing
//<application android:name="your.package.MyApplication" ...
public class MyApplication extends Application {
    // http://stackoverflow.com/questions/3667022/checking-if-an-android-application-is-running-in-the-background/13809991#13809991

    @Override
    public void onCreate() {
        // Simply add the handler, and that's it! No need to add any code
        // to every activity. Everything is contained in MyLifecycleHandler
        // with just a few lines of code. Now *that's* nice.
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }
}
