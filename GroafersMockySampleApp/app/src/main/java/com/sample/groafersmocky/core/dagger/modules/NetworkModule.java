package com.sample.groafersmocky.core.dagger.modules;


import android.content.Context;

import com.sample.groafersmocky.datarepository.onlinerepo.ApiService;
import com.sample.groafersmocky.datarepository.onlinerepo.CloudRepo;
import com.sample.groafersmocky.utils.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static int REQUEST_TIMEOUT = 10;
    private static OkHttpClient okHttpClient;

    @Singleton
    @Provides
    final OkHttpClient providesClient(){
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(providesClient())
                .build();
        return retrofit;
    }

    @Singleton
    @Provides
    ApiService provideApiService(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }


    @Singleton
    @Provides
    CloudRepo provideRepository(Context context,ApiService apiService){
        return new CloudRepo(context,apiService);
    }
}
