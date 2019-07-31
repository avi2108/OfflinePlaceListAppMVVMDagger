package com.sample.groafersmocky.datarepository.offlinerepo.database;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance withRepoContract the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sample.groafersmocky.models.Place;



@Database(entities = {Place.class}, version = 1)
public abstract class PlacesListDatabase extends RoomDatabase {

    public abstract PlacesListDAO placesListDao();

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile PlacesListDatabase INSTANCE;

    public static PlacesListDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PlacesListDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PlacesListDatabase.class, "placeslist db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }




}
