package com.hitherejoe.tabby;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class TabbyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }

    public static TabbyApplication get(Context context) {
        return (TabbyApplication) context.getApplicationContext();
    }
}
