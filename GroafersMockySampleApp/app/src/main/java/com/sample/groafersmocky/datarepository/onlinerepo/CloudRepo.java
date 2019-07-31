package com.sample.groafersmocky.datarepository.onlinerepo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.sample.groafersmocky.models.Place;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CloudRepo {


    public Context context;
    private ApiService apiService;
    private MutableLiveData<List<com.sample.groafersmocky.models.Place>> placesLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isApiFailure = new MutableLiveData<>();

    @Inject
    public CloudRepo(Context context, ApiService apiService) {
        this.apiService = apiService;
        this.context = context;
    }

    public LiveData<List<Place>> fetchPlaces() {
        if (!isNetWorkConnected()) {
            return placesLiveData;
        }
        apiService.fetchPlaces().enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (response.isSuccessful()) {
                    placesLiveData.postValue(response.body());
                    Log.d("CloudRepo", "items received " + response.body().size());
                }
                isApiFailure.postValue(response.isSuccessful() ? false : true);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                call.cancel();
                isApiFailure.postValue(true);
            }
        });


        return placesLiveData;
    }


    public MutableLiveData<Boolean> getIsApiFailure() {
        return isApiFailure;
    }



    boolean isNetWorkConnected() {
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
