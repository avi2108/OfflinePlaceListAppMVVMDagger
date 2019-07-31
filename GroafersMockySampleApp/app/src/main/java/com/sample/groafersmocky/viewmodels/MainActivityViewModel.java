package com.sample.groafersmocky.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sample.groafersmocky.datarepository.offlinerepo.database.DBRepo;
import com.sample.groafersmocky.datarepository.onlinerepo.CloudRepo;
import com.sample.groafersmocky.models.Place;

import java.util.List;

import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel  {

    private LiveData<List<Place>> arrayListLiveData;
    private CloudRepo cloudRepo;
    private DBRepo dbRepo;


    @Inject
    public MainActivityViewModel(CloudRepo cloudRepo, DBRepo dbRepo) {
        this.cloudRepo = cloudRepo;
        this.dbRepo = dbRepo;
    }

    public LiveData<List<Place>> fetchCloudPlaces() {
        return cloudRepo.fetchPlaces();
    }

    public LiveData<List<Place>> fetchPlaces() {
        if (arrayListLiveData == null)
            arrayListLiveData = dbRepo.fetchPlaces();

        return arrayListLiveData;
    }

    public void insertIntoDB(List<Place> placeList)
    {

        dbRepo.deleteAllPlaces();
        dbRepo.insert(placeList);
    }

    public MutableLiveData<Boolean> getIsApiFailure() {
        return cloudRepo.getIsApiFailure();
    }

}
