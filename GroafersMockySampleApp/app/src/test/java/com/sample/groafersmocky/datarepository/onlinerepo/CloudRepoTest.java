package com.sample.groafersmocky.datarepository.onlinerepo;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class CloudRepoTest {

    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    Context context;
    @Mock
    ApiService apiService;
    @InjectMocks
    CloudRepo cloudRepo;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void fetchPlaces() {

    }

    @Test
    public void getIsApiFailure() {
    }

    @Test
    public void isNetWorkConnected() {
    }
}