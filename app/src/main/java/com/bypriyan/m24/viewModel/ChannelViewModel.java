package com.bypriyan.m24.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bypriyan.m24.model.ModelChannel;

import java.util.ArrayList;
import java.util.List;

public class ChannelViewModel extends ViewModel {
    private MutableLiveData<List<ModelChannel>> data;
    public ChannelViewModel() {
        data= new MutableLiveData<>();
        Log.d("TAG", "ChannelViewModel: constructor ");
        List<ModelChannel> testData = new ArrayList<>();
        setData(testData);
    }



    public LiveData<List<ModelChannel>> getData() {
        return data;
    }

    public void setData(List<ModelChannel> newData) {
        data.setValue(newData);
    }


}