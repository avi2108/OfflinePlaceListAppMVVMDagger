package com.sample.groafersmocky.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "places_table")
public class Place {

    @PrimaryKey
    @NonNull
    String name;

    @SerializedName("description")
    @ColumnInfo(name = "place")
    String place;

    String image;

    public Place(@NonNull String name, @NonNull String place, String image) {
        this.name = name;
        this.place = place;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getImage() {
        return image;
    }
}
