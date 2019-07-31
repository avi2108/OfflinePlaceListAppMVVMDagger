package com.sample.groafersmocky.helpers;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sample.groafersmocky.viewmodels.MainActivityViewModel;

public class BindingAdapterFactory {


    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        CachedImageViewLoader.with(view.getContext()).loadImage(imageUrl,view);
        Log.e("urls", imageUrl + "");
    }
}
