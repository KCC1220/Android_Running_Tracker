package com.example.assignment3.Database;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName="running_table")
public class Data {
    @PrimaryKey(autoGenerate = true)
            @ColumnInfo(name = "_ID")
    int id;
    @ColumnInfo(name = "avg_speed")
    float avgSpeed;
    @ColumnInfo(name = "distance")
    float distance;
    @ColumnInfo(name = "time")
    String totalTime;
    @ColumnInfo(name = "max_speed")
    float maxSpeed;
    @ColumnInfo(name = "start_address")
    String startAddress;
    @ColumnInfo(name = "stop_address")
    String stopAddress;
    @ColumnInfo(name = "goal")
    int goal;
    @ColumnInfo(name = "comments")
    String comments;
    @ColumnInfo(name="rating")
    float  rating;
    @ColumnInfo(name="data")
    String date;
    @ColumnInfo(name="mode")
    String mode;

    /**
     * A constructor to create a new data
     * @param distance is the total distance travelled
     * @param avgSpeed is the average speed
     * @param totalTime is the total time taken
     * @param maxSpeed is the maximum speed
     * @param startAddress is the starting address
     * @param stopAddress is the stop address
     * @param comments is the comment by the user
     * @param rating is the rating by the user
     * @param goal is the goal value
     * @param date is the date as a string
     * @param mode is the mode chosen
     */
    public Data(float distance, float avgSpeed, String totalTime, float maxSpeed,String startAddress,String stopAddress,String comments,float rating,int goal,String date,String mode) {
        this.avgSpeed = avgSpeed;
        this.distance = distance;
        this.totalTime = totalTime;
        this.maxSpeed = maxSpeed;
        this.startAddress = startAddress;
        this.stopAddress = stopAddress;
        this.comments = comments;
        this.rating = rating;
        this.goal = goal;
        this.date = date;
        this.mode=mode;
    }

    /**
     * A method to get id of the data
     * @return is the id of the data
     */
    public int getId() {
        return id;
    }

    /**
     * A method to get the average speed of the data
     * @return is teh average speed of the data
     */
    public float getAvgSpeed() {
        return avgSpeed;
    }

    /**
     * A method to get the distance travelled
     * @return is the total distance travelled
     */
    public float getDistance() {
        return distance;
    }

    /**
     * A method to get the total time taken as a string
     * @return is the time string
     */
    public String getTotalTime() {
        return totalTime;
    }

    /**
     * A method to get the max speed
     * @return is the max speed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * A method to get the starting address
     * @return is the starting address
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * A method to get the end address
     * @return is the ending address
     */
    public String getStopAddress() {
        return stopAddress;
    }

    /**
     * A method to get the data saved
     * @return is the date saved
     */
    public String getDate(){return this.date;}

    /**
     * A method to get the goal value
     * @return is the goal value
     */
    public int getGoal() {
        return goal;
    }

    /**
     * A method to get comment set by user
     * @return is the comment
     */
    public String getComments(){return comments;}

    /**
     * A method to get rating number
     * @return is the rating number
     */
    public float getRating(){return rating;}

    /**
     * A method to get the mode
     * @return is the tracking mode
     */
    public String getMode(){return mode;}


    /**
     * A method that convert the values from other application user to the correct style
     * @param values is the content value from other application
     * @return is the data object of the correct for,m
     */
    //Tutorial Video: https://youtu.be/RePFTYd7t3w
    public static Data fromOtherApplication(ContentValues values){
        float avgSpeed =0;
        float distance=0;
        String totalTime=null;
        float maxSpeed=0;
        String startLocation=null;
        String endLocation=null;
        int goal=0;
        float rating = 0;
        String comments = null;
        String mode=null;
        //Check if there is the related value in the content values
        //If there is to the local variable and if not then set as 0 or null
        if(values!=null && values.containsKey("avgSpeed")){
            avgSpeed = values.getAsInteger("avgSpeed");
        }
        if(values!=null && values.containsKey("distance")){
            distance = values.getAsFloat("distance");
        }
        if(values!=null && values.containsKey("totalTime")){
            totalTime = values.getAsString("totalTime");
        }
        if(values!=null && values.containsKey("maxSpeed")){
            maxSpeed = values.getAsFloat("maxSpeed");
        }
        if(values!=null && values.containsKey("startAddress")){
            startLocation = values.getAsString("startAddress");
        }
        if(values!=null && values.containsKey("endAddress")){
            endLocation = values.getAsString("endAddress");
        }
        if(values!=null && values.containsKey("goal")){
            goal = values.getAsInteger("goal");
        }
        if(values!=null && values.containsKey("comments")){
            comments = values.getAsString("comments");
        }
        if(values!=null && values.containsKey("rating")){
            rating = values.getAsFloat("rating");
        }
        if(values!=null && values.containsKey("mode")){
            mode = values.getAsString("mode");
        }
        //Return the new data object
        return new Data(distance,avgSpeed,totalTime,maxSpeed,startLocation,endLocation,comments,rating,goal,
                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()),mode);

    }
}
