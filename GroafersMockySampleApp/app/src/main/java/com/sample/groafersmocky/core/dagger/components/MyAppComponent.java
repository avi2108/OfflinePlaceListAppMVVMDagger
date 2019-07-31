package com.sample.groafersmocky.core.dagger.components;

import com.sample.groafersmocky.MyApplication;
import com.sample.groafersmocky.core.dagger.modules.ActivityBuilder;
import com.sample.groafersmocky.core.dagger.modules.DBModule;
import com.sample.groafersmocky.core.dagger.modules.MainActivityModule;
import com.sample.groafersmocky.core.dagger.modules.MyAppModule;
import com.sample.groafersmocky.core.dagger.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, MyAppModule.class,
        NetworkModule.class, DBModule.class,
        ActivityBuilder.class})
public interface MyAppComponent extends AndroidInjector<MyApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<MyApplication> {
    }

}
