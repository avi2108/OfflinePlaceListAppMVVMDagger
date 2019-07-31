package com.sample.groafersmocky.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.sample.groafersmocky.datarepository.offlinerepo.database.DBRepo;
import com.sample.groafersmocky.datarepository.onlinerepo.CloudRepo;
import com.sample.groafersmocky.models.Place;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MainActivityViewModelTest {

    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    CloudRepo cloudRepo;
    @Mock
    DBRepo dbRepo;
    @InjectMocks
    MainActivityViewModel mainActivityViewModel;
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void fetchCloudPlaces() {
        //Arrange
        List<Place> placeList = new ArrayList<>();
        placeList.add(new Place("name1", "place1", "imageUrl1"));
        MutableLiveData<List<Place>> listLiveData = new MutableLiveData<List<Place>>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listLiveData.postValue(placeList);
            }
        }).start();

        Mockito.when(cloudRepo.fetchPlaces()).thenReturn(listLiveData);

        //Act
        LiveData<List<Place>> liveData = mainActivityViewModel.fetchCloudPlaces();

        //Assert
        Assert.assertTrue("list data is empty on cloud call",liveData.getValue().size()==listLiveData.getValue().size());

    }

}