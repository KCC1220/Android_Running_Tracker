package com.example.assignment3.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyRepository {
    private final DataDao dataDao;
    private final LiveData<List<Data>> allData;

    /**
     * A constructor to get the database of the application
     * @param application is the main application
     */
    MyRepository(Application application){
        //Get database of the application
        DB db = DB.getDatabase(application);
        //Get the DAO for the database
        dataDao = db.dataDao();
        //Get all the data
        allData = dataDao.getAllData();
    }

    /**
     * A method to return all the data
     * @return all the data
     */
    LiveData<List<Data>> getAllData(){return allData;}

    /**
     * A method to insert data into the database
     * @param data is the new data object
     */
    void insert(Data data){
        dataDao.insert(data);
    }

    /**
     * A method to delete all the data
     */
    void delete(){
        dataDao.deleteAllData();
    }
}
