package com.sample.groafersmocky.datarepository.offlinerepo.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.sample.groafersmocky.models.Place;

import java.util.List;


@Dao
public interface PlacesListDAO {

    @Query("SELECT * from places_table")
    LiveData<List<Place>> fetchPlacesFromDB();

    // We do not need a conflict strategy, because the word is our primary key, and you cannot
    // add two items withRepoContract the same primary key to the database. If the table has more than one
    // column, you can use @Insert(onConflict = OnConflictStrategy.REPLACE) to update a row.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void batchInsert(Place... places);

    @Query("DELETE FROM places_table")
    void deleteAll();
}
