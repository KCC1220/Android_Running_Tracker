package com.example.assignment3.DataProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment3.Database.DB;
import com.example.assignment3.Database.DataDao;
import com.example.assignment3.Database.Data;

public class DataProvider extends ContentProvider {
    private DataDao dataDao;
    //An uri matcher
    private static final UriMatcher uriMatcher;
    //Element of uri matcher
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataProviderContract.AUTHORITY,"track",1);
    }

    /**
     * Method called when the content provider is created
     * @return is the status of the content provider
     */
    @Override
    public boolean onCreate() {
        //Get the context of the application
        final Context context = getContext();
        Log.e("Hello","Created");
        //Get the database using the context
        DB db = DB.getDatabase(context);
        //Get the DAO of the database
        dataDao = db.dataDao();
        return true;
    }

    /**
     * Query the data provider for a Cursor containing data
     *
     * @param uri is the URI to query
     * @param strings is the list of columns that is needed
     * @param s is the selection criteria for the rows
     * @param strings1 is the arguments for the selection criteria.
     * @param s1 is the order in which the rows should be sorted.
     * @return A Cursor containing the requested data or null if the URI is invalid.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        //If the URI match
        if (uriMatcher.match(uri) == 1) {
            //Return all the data
            return dataDao.getData();
        }
        return null;

    }

    /**
     * A method to get the returning type
     * @param uri the uri passed by the other application
     * @return is the content type whether it is multiple cursor or one row of data
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String contentType;

        if(uri.getLastPathSegment()==null){
            contentType = DataProviderContract.CONTENT_TYPE_MULTIPLE;
        }
        else{
            contentType = DataProviderContract.CONTENT_TYPE_SINGLE;
        }
        return contentType;
    }

    /**
     * A method when user want to other user want to insert data into the database
     * @param uri is the uri that the user specified
     * @param contentValues is the data that the user want to insert
     * @return is a uri with the new data inserted id or null if the uri is not match
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //An variable to catch the user inserted data id
        long id;
        //Check the user uri
        switch(uriMatcher.match(uri)){
            //If the uri matcher return 1 which is insert to the track table
            case 1:
                //Get the id after the insertion is completed
                id = dataDao.insertFromContentProvider(Data.fromOtherApplication(contentValues));
                //Get the returning URI
                Uri nu = ContentUris.withAppendedId(uri,id);
                //Notify changes to all the subscriber
                getContext().getContentResolver().notifyChange(nu,null);
                //Return the URI
                return nu;
            case 2:
            default:
                throw new UnsupportedOperationException("No such directory");
        }
    }

    /**
     * A query when user want to delete data in the content provider database
     * NOT YET IMPLEMENTED
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("NOT SUPPORTED");
    }

    /**
     * A method called when the other application want to update the data
     * NOT YET IMPLEMENTED
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("NOT SUPPORTED");
    }
}
