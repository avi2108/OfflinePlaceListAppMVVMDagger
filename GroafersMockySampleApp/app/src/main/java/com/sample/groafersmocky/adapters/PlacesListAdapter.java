package com.sample.groafersmocky.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sample.groafersmocky.R;
import com.sample.groafersmocky.databinding.LayoutPlaceItemBinding;
import com.sample.groafersmocky.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.ImageItemHolder> {

    private ArrayList<Place> imageModels;

    public PlacesListAdapter(ArrayList<Place> imageModelList){
        this.imageModels = imageModelList;
    }

    public void setImageModels(List<Place> imageModels) {
        this.imageModels = (ArrayList<Place>) imageModels;
    }

    @NonNull
    @Override
    public ImageItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ImageItemHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.layout_place_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemHolder imageItemHolder, int position) {
        imageItemHolder.layoutPlaceItemBinding.setItem(imageModels.get(position));
        imageItemHolder.layoutPlaceItemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }


    public class ImageItemHolder extends RecyclerView.ViewHolder {

        LayoutPlaceItemBinding layoutPlaceItemBinding;

        public ImageItemHolder(LayoutPlaceItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.layoutPlaceItemBinding = itemBinding;
        }
    }
}
