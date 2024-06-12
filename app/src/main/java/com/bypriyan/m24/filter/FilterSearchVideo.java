package com.bypriyan.m24.filter;

import android.widget.Filter;

import com.bypriyan.m24.adapter.AdapterVideos;
import com.bypriyan.m24.model.ModelVideos;

import java.util.ArrayList;

public class FilterSearchVideo extends Filter {
    private AdapterVideos adapter;
    private ArrayList<ModelVideos> filterList;


    public FilterSearchVideo(AdapterVideos adapter, ArrayList<ModelVideos> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelVideos> filteredModel = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getDescription().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getTitle().toUpperCase().contains(charSequence)){

                    filteredModel.add(filterList.get(i));
                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;

        }else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.videosList = (ArrayList<ModelVideos>)results.values;
        adapter.notifyDataSetChanged();
    }
}
