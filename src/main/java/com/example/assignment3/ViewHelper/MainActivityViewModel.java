package com.example.assignment3.ViewHelper;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    SavedStateHandle savedState;
    private float totalDistances;
    private int goalValue;
    private String time;
    private float maxSpeed;
    private float avgSpeed;
    private String startAddress;
    private String stopAddress;
    private Boolean tracking;
    private String mode;
    /**
     * A constructor to check for any saved state when configuration change
     * @param savedStateHandle is the saved state
     */
    public MainActivityViewModel(SavedStateHandle savedStateHandle){
        this.savedState = savedStateHandle;
        //If the saved state contain the specific value then set to the variable
        if(savedState.contains("tracking")){
            setTracking(savedState.get("tracking"));
        }
        if(savedState.contains("distances")){
            setTotalDistances(savedState.get("distances"));
        }
        if(savedState.contains("goal")){
            setGoalValue(savedState.get("goal"));
        }
        if(savedState.contains("avg")){
            setAvgSpeed(savedState.get("avg"));
        }
        if(savedState.contains("max")){
            setMaxSpeed(savedState.get("max"));
        }
        if(savedState.contains("start")){
            setStartAddress(savedState.get("start"));
        }
        if(savedState.contains("end")){
            setStopAddress(savedState.get("end"));
        }
        if(savedState.contains("mode")){
            setMode(savedState.get("mode"));
        }
        if(savedState.contains("time")){
            setTime(savedState.get("time"));
        }
    }

    /**
     * A method to get the total distance travelled
     * @return is the total distance travelled
     */
    public float getTotalDistances() {
        return totalDistances;
    }

    /**
     * A method to set the tracking progress to true or false
     * @param tracking is the tracking progress
     */
    public void setTracking(Boolean tracking) {
        this.tracking = tracking;
        //Save the value into the saved state
        this.savedState.set("tracking",this.tracking);
    }

    /**
     * A method to get whether the tracking progress is running or not
     * @return is the boolean for tracking progress
     */
    public Boolean getTracking() {
        return tracking;
    }

    /**
     * A method to set the total distance travelled
     * @param distances is the total distance travelled
     */
    public void setTotalDistances(float distances) {
            this.totalDistances = distances;
            this.savedState.set("distances", totalDistances);
    }

    /**
     * A method to set the goal value
     * @param goalValue is the goal value
     */
    public void setGoalValue(int goalValue) {
        this.goalValue = goalValue;
        this.savedState.set("goal",this.goalValue);

    }

    /**
     * A method to set the time as a string
     * @param time is time format in string
     */
    public void setTime(String  time) {
        this.time = time;
        this.savedState.set("time",this.time);
    }

    /**
     * A method to set the average speed
     * @param avgSpeed is the average speed
     */
    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
        this.savedState.set("avg",this.avgSpeed);

    }

    /**
     * A method to set the max speed
     * @param maxSpeed is the max speed
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.savedState.set("max",this.maxSpeed);
    }

    /**
     * A method to set the start address
     * @param address is the start address
     */
    public void setStartAddress(String address) {
        this.startAddress = address;
        this.savedState.set("start",this.startAddress);
    }

    /**
     * A method to set the stop address
     * @param address is the stop address
     */
    public void setStopAddress(String address){
        this.stopAddress = address;
        this.savedState.set("end",this.stopAddress);
    }

    /**
     * A method to set the current mode
     * @param mode is the current mode of the tracking progress
     */
    public void setMode(String mode){
        this.mode = mode;
        this.savedState.set("state",this.mode);
    }

    /**
     * A method to get the current mode
     * @return is the mode set
     */
    public String getMode(){
        return String.valueOf(this.mode);
    }

    /**
     * A method to get the goal value
     * @return is the goal value
     */
    public int getGoalValue() {
        return goalValue;
    }

    /**
     * A method to get time in string
     * @return is time in string
     */

    public String getTime() {
        return time;
    }

    /**
     * A method to get the maximum speed
     * @return is the maximum speed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * A method to get the average speed
     * @return is the average speed
     */
    public float getAvgSpeed() {
        return avgSpeed;
    }

    /**
     * A method to get the start address
     * @return is the start address
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * A method to get the stop address
     * @return is the stop address
     */
    public String getStopAddress() {
        return stopAddress;
    }
}
