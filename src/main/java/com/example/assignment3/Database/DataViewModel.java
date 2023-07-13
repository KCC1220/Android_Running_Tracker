package com.example.assignment3.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    MyRepository repository;
    LiveData<List<Data>> allData;

    /**
     * A constructor to create all the necessary object
     * @param application is the main application
     */
    public DataViewModel(@NonNull Application application) {
        super(application);
        //The repository object
        repository = new MyRepository(application);
        //Get all the data
        allData = repository.getAllData();
    }

    /**
     * A method to get all the data
     * @return is all the data
     */
    public LiveData<List<Data>> getAllData(){return allData;}

    /**
     * A method to insert data into the database
     * @param data new data object
     */
    public void insert(Data data){
        repository.insert(data);
    }

    /**
     * A method to delete all the data
     */
    public void delete(){
        repository.delete();
    }
}
