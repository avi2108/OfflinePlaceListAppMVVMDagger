package com.sample.groafersmocky.core.dagger.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.sample.groafersmocky.MyApplication;
import com.sample.groafersmocky.core.dagger.components.MyAppComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class MyAppModule {

    @Singleton
    @Provides
    Context provideContext(MyApplication application){
        return application;
    }

//    @Singleton
//    @Provides
//    MainAppStore provideMainAppStore(OnlineStore onlineStore, OfflineStore offlineStore){
//        return new MainAppStore(onlineStore, offlineStore);
//    }

    @Singleton
    @Provides
    Gson provideGson(){
        return new Gson();
    }

//    @Singleton
//    @Provides
//    Utils provideUtils(Context context){
//        return new Utils(context);
//    }

}
