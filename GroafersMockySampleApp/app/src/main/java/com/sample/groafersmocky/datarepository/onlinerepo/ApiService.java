package com.sample.groafersmocky.datarepository.onlinerepo;


import com.sample.groafersmocky.models.Place;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiService {


    @GET("getFoodItems")
    Call<List<Place>> fetchPlaces();
}
