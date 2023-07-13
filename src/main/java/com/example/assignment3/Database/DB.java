package com.example.assignment3.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Data.class}, version = 5)
public abstract class DB extends RoomDatabase {
    private static volatile DB instance;
    public abstract DataDao dataDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Create the database
     * @param context is the activity context
     * @return is the database
     */
    public static DB getDatabase(final Context context) {
        //If there is no database
        if (instance == null) {
            synchronized (DB.class) {
                if (instance == null) {
                    //Create a new database
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    DB.class, "track_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(createCallback)
                            .build();
                }
            }
        }
        //Return the database
        return instance;
    }

    /**
     * A callback method to perform when the database is first created
     */
    private static final RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // create database and clear all the data
            databaseWriteExecutor.execute(() -> {
                DataDao trackDao = instance.dataDao();
                trackDao.deleteAllData();
            });
        }
    };

}

