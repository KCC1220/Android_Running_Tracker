package com.example.assignment3.Database;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {
    /**
     * A method to insert data into the database
     * @param data is the new data object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Data data);

    /**
     * A method to get all the data
     * @return is all the data
     */
    @Query("SELECT * FROM running_table ORDER BY _ID DESC")
    LiveData<List<Data>> getAllData();

    /**
     * A method to delete all the data
     */
    @Query("DELETE FROM running_table")
    void deleteAllData();

    /**
     * A method to get data in the form of cursor
     * @return is all the data in cursor
     */
    @Query("SELECT * FROM running_table")
    Cursor getData();

    /**
     * A method to insert with id as the returning value
     * @param data the new data
     * @return is the id of the data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFromContentProvider(Data data);

}
