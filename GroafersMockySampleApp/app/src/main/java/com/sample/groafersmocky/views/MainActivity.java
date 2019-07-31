package com.sample.groafersmocky.views;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.sample.groafersmocky.R;
import com.sample.groafersmocky.adapters.PlacesListAdapter;
import com.sample.groafersmocky.databinding.ActivityMainBinding;
import com.sample.groafersmocky.viewmodels.MainActivityViewModel;

import javax.inject.Inject;

public class MainActivity extends BaseActivity<MainActivityViewModel> {

    ActivityMainBinding activityMainBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    PlacesListAdapter placesListAdapter;
    @Inject
    LinearLayoutManager linearLayoutManager;
    MainActivityViewModel mainActivityViewModel;


    @Override
    public MainActivityViewModel getViewModel() {
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        return mainActivityViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(activityMainBinding.toolbar);
        initRecycler();
        subscribeObservers();
    }

    void initRecycler() {
        activityMainBinding.contentMain.recyclerforImageList.setHasFixedSize(true);
        activityMainBinding.contentMain.recyclerforImageList.setLayoutManager(linearLayoutManager);
        activityMainBinding.contentMain.recyclerforImageList.setAdapter(placesListAdapter);
    }

    void subscribeObservers() {
        mainActivityViewModel.fetchPlaces().observe(this, placeList -> {
            if (!placeList.isEmpty()) {
                placesListAdapter.setImageModels(placeList);
                placesListAdapter.notifyDataSetChanged();
            }
        });
        mainActivityViewModel.getIsApiFailure().observe(this, aBoolean -> {
            if (aBoolean)
                Snackbar.make(activityMainBinding.cordinatorLayoutParent, R.string.api_failure, Snackbar.LENGTH_LONG)
                        .show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @Override
    void onNetworkConnected() {
        mainActivityViewModel.fetchCloudPlaces().observe(this,placeList -> {
            if (!placeList.isEmpty())
                mainActivityViewModel.insertIntoDB(placeList);
        });
    }

    @Override
    void onNetworkDisconnected() {
        Snackbar.make(activityMainBinding.cordinatorLayoutParent, R.string.internet_error, Snackbar.LENGTH_LONG)
                .show();
    }
}
