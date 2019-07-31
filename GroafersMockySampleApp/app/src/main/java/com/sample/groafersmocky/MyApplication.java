package com.sample.groafersmocky;

import android.content.Context;


import com.sample.groafersmocky.core.dagger.components.DaggerMyAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class MyApplication extends DaggerApplication {

    private static MyApplication myApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication =this;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerMyAppComponent.builder().create(this);
    }

    public static synchronized Context getContext() {
        return myApplication;
    }
}
