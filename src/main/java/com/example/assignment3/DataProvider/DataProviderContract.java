package com.example.assignment3.DataProvider;

import android.net.Uri;

public class DataProviderContract {

    //Authority for the content provider
    public static final String AUTHORITY = "com.example.assignment3.DataProvider.DataProvider";
    //URI for content provider
    public static final Uri TRACK_URI = Uri.parse("content://"+AUTHORITY+"/track");
    //Some useful information that other application can use
    public static final String _ID = "_id";
    public static final String DISTANCE = "distance";
    public static final String TIME = "time";
    public static final String START_ADDRESS = "start_address";
    public static final String STOP_ADDRESS = "stop_address";
    public static final String AVG_SPEED = "avg_speed";
    public static final String MAX_SPEED = "max_speed";
    //Return type for content provider
    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/DataProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/DataProvider.data.text";

}
