package com.sample.groafersmocky.core.dagger.modules;

import android.content.Context;

import com.sample.groafersmocky.datarepository.offlinerepo.database.DBRepo;
import com.sample.groafersmocky.datarepository.offlinerepo.database.PlacesListDAO;
import com.sample.groafersmocky.datarepository.offlinerepo.database.PlacesListDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DBModule {

    @Singleton
    @Provides
    PlacesListDatabase providerPlaceListDataBase(Context context){
        return PlacesListDatabase.getDatabase(context);
    }

    @Singleton
    @Provides
    PlacesListDAO providesPlacesListDao(PlacesListDatabase placesListDatabase){
        return placesListDatabase.placesListDao();
    }

    @Singleton
    @Provides
    DBRepo providesDBRepo(PlacesListDAO placesListDAO){
        return new DBRepo(placesListDAO);
    }
}
