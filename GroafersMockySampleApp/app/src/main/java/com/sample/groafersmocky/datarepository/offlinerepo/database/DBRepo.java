package com.sample.groafersmocky.datarepository.offlinerepo.database;


import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.sample.groafersmocky.models.Place;

import java.util.List;

import javax.inject.Inject;

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */

public class DBRepo {

    private PlacesListDAO placesListDAO;
    private LiveData<List<Place>> listLiveData;

    @Inject
    public DBRepo(PlacesListDAO placesListDAO) {
        this.placesListDAO = placesListDAO;
        listLiveData = placesListDAO.fetchPlacesFromDB();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Place>> fetchPlaces() {
        return listLiveData;
    }

    public void insert(List<Place> placeList) {
        new InsertAsyncTask(placesListDAO).execute(placeList);
    }

    public void deleteAllPlaces(){
          new DeleteAsycnTask(placesListDAO).execute();
    }

    /**
     * asynctask that handles delete operation in background
     */
    private class DeleteAsycnTask extends AsyncTask<Void,Void,String>{

        private PlacesListDAO mAsyncTaskDao;

        DeleteAsycnTask(PlacesListDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    /**
     * asynctask that inserts batch records in background
     */
    private class InsertAsyncTask extends AsyncTask<List<Place>, Void, List<Place>> {

        private PlacesListDAO mAsyncTaskDao;

        InsertAsyncTask(PlacesListDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Place> doInBackground(final List<Place>... params) {
            List<Place> placeList = params[0];
            Place[] places = new Place[placeList.size()];
            for (int i=0;i<placeList.size();i++)
                places[i]=placeList.get(i);
                mAsyncTaskDao.batchInsert(places);
            return params[0];
        }

    }
}
