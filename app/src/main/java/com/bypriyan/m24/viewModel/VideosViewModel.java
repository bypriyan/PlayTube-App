package com.bypriyan.m24.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bypriyan.m24.model.ModelChannel;
import com.bypriyan.m24.model.ModelVideos;

import java.util.ArrayList;
import java.util.List;

public class VideosViewModel extends ViewModel {
    private MutableLiveData<ArrayList<ModelVideos>> data;
    public VideosViewModel() {
        data= new MutableLiveData<>();
        Log.d("TAG", "ChannelViewModel: constructor ");
        ArrayList<ModelVideos> testData = new ArrayList<>();
        setData(testData);
    }



    public LiveData<ArrayList<ModelVideos>> getData() {
        return data;
    }

    public void setData(ArrayList<ModelVideos> newData) {
        data.setValue(newData);
    }


}