package com.nlpl.model.MapsModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListLocationModel {

    @SerializedName("data")
    private List<LocationModel> mData;

    public List<LocationModel> getmData() {
        return mData;
    }
}
