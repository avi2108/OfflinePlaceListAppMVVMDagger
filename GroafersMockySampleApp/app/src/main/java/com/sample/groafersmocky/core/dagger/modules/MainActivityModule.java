package com.sample.groafersmocky.core.dagger.modules;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.sample.groafersmocky.adapters.PlacesListAdapter;
import com.sample.groafersmocky.datarepository.offlinerepo.database.DBRepo;
import com.sample.groafersmocky.datarepository.onlinerepo.CloudRepo;
import com.sample.groafersmocky.utils.ViewModelProviderFactory;
import com.sample.groafersmocky.viewmodels.MainActivityViewModel;

import java.util.ArrayList;


import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {


    @Provides
    MainActivityViewModel mainActivityViewModel(CloudRepo repository, DBRepo dbRepo) {
        return new MainActivityViewModel(repository,dbRepo);
    }


    @Provides
    PlacesListAdapter providePlacesListAdapter(){
        return new PlacesListAdapter( new ArrayList<>());
    }

    @Provides
    ViewModelProvider.Factory provideViewModelProvider(MainActivityViewModel viewModel){
        return new ViewModelProviderFactory(viewModel) {
        };
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(Context context){
        return new LinearLayoutManager(context);
    }
}
