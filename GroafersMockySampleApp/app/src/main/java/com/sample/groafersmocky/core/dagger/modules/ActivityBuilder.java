package com.sample.groafersmocky.core.dagger.modules;

import com.sample.groafersmocky.views.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {
            MainActivityModule.class})
    abstract MainActivity contributeMainActivity();

}
