package com.example.assignment3.Tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Tracker {
    //An enum for type status
    public enum Mode {
        DYNAMIC,
        STANDING,
        WALKING,
        RUNNING,
        CYCLING,
        DRIVING
    }
    //A variable that hold the current status
    Mode currentMode;
    Mode currentState;
    //A variable use to request location update
    FusedLocationProviderClient fusedLocationClient;
    //A variable to specified the parameter for location update being requested
    LocationRequest locationRequest;
    //A variable tp hold the current location
    Location lastLocation;
    //A callback use to receive location update
    LocationCallback locationCallback;
    //A variable that hold the total distance travelled
    float totalDistanceTravel;
    //A variable that hold the maximum speed
    float maxSpeed = 0;
    //A variable that hold the goal value
    int goalValue = 0;
    //A variable to hold the start address
    String startAddress = "N/A";
    //A variable to hold the stop address
    String stopAddress = "N/A";
    //A variable to get the average speed
    float avgSpeed;
    //A variable that hold the activity context
    Context context;
    //A variable that hold the activity
    Activity activity;
    //A variable that indicate whether the tracking progress is running
    Boolean running = false;
    //A variable that indicate whether the tracking progress is stopped
    Boolean stop = false;
    //A variable that catch how many seconds had run in total
    int seconds;
    //A string to represent the time in a proper format
    String time="0:00:00";
    //A variable that hold hours taken after conversion
    int hours;
    //A variable that hold minutes taken after conversion
    int minutes;
    //A variable that hold seconds taken after conversion
    int secs;
    //A handler to schedule the timer
    Handler handler = new Handler();
    //Geocoder to get the address
    Geocoder geocoder;
    //A variable to indicate whether the progress is finished
    Boolean finish = false;

    /**
     * A method to initialise qll the class needed
     *
     * @param context  is the activity context
     * @param activity is the main activity
     *
     */
    public void init(Context context, Activity activity) {
        //Get the fused location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        //Get a location request every 1 seconds
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();
        //Set the main context reference
        this.context = context;
        //Set the main activity reference
        this.activity = activity;
        //Initialise a geocoder object
        geocoder = new Geocoder(context);
    }

    /**
     * A method to start the tracking progress
     *
     * @param mode is the status chosen by user
     */
    public void onStart(Mode mode) {
        //If the user is starting a new progress
        if (finish) {
            //If it is a new progress reset all the variable
            onReset();
        }
        //If the progress is not running
        if (!running) {
            //Set running to true which indicate it is running
            running = true;
            //Set finish to false
            finish = false;
            //Start the tracking progress
            startTracking(mode);
            //Run the timer
            runTimer();
        }
    }

    /**
     * Method call when the tracking progress is stopped
     */
    public void onStop() {
        //Only allow to perform when the tracking progress is running
        if (running) {
            //Set running to false which indicate the progress stopped
            running = false;
            //Set stop to true which indicate it is stopped
            stop = true;
            //Stop the timer
            stopTimer();
            //Remove the location update request
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * A method called when the user ask to finish the progress
     */
    public void onFinish() {
        //Get the total seconds taken
        int totalSecondsTaken = seconds;
        //Set the progress to finished
        finish = true;
        //Calculate the average speed
        avgSpeed = totalDistanceTravel / totalSecondsTaken;
        //Stop the progress
        onStop();
    }

    /**
     * A method to reset all the variable
     */
    public void onReset() {
        //Reset total distance travelled
        totalDistanceTravel = 0;
        //Reset the seconds taken
        seconds = 0;
        //Reset the hours taken
        hours = 0;
        //Reset the minutes taken
        minutes = 0;
        //Reset the seconds taken
        secs = 0;
        //Reset the average speed
        avgSpeed = 0;
        //Reset teh max speed
        maxSpeed = 0;
        //Set the last location to null
        lastLocation = null;
        //Set the time to 0
        time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * A method to start the tracking progress
     *
     * @param mode is the tracking mode chosen by the user
     */
    public void startTracking(Mode mode) {
        //Location callback
        locationCallback = new LocationCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            //Action perform when receive the location result
            public void onLocationResult(@NonNull LocationResult locationResult) {
                //Loop a the location get
                for (Location location : locationResult.getLocations()) {
                    //If the progress is running
                    if (running) {
                        //If the last location is not null
                        if (lastLocation != null)
                            //Calculate the distance between the last location and the new current location
                            totalDistanceTravel += lastLocation.distanceTo(location);
                            //If the last location is null which indicate that is the starting location
                        else {
                            //Initialise a variable to catch the address
                            List<Address> addresses;
                            try {
                                //Try to get the address using the current location
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                //Get the current address in a string
                                startAddress = addresses.get(0).getAddressLine(0);
                            } catch (IOException e) {
                                //If there is error getting the address set the startAddress to ERROR
                                startAddress = "ERROR";
                            }
                        }
                        //Check if the mode chosen is dynamic or not
                        if (mode == Mode.DYNAMIC) {
                            currentMode = Mode.DYNAMIC;
                            //Track the speed of the moving location between the last location and current location
                            //The speed is a typical speed get from https://www.bbc.co.uk/bitesize/guides/zq4mfcw/revision/1
                            if (location.getSpeed() >= 0 && location.getSpeed() < 1.5) {
                                //Speed in the range shows the user is standing
                                //Set the current state
                                currentState = Mode.STANDING;
                                Log.e("Status", "Standing");
                            } else if (location.getSpeed() >= 1.5 && location.getSpeed() < 5) {
                                //Speed in the range shows the user is walking
                                //Set the current state
                                currentState = Mode.WALKING;
                                Log.e("Status", "Walking");
                            } else if (location.getSpeed() >= 5 && location.getSpeed() < 7) {
                                //Speed in the range shows the user is running
                                //Set the current state
                                currentState = Mode.RUNNING;
                                Log.e("Status", "Running");
                            } else if (location.getSpeed() >= 7 && location.getSpeed() < 13) {
                                //Speed in the range shows the user is cycling
                                //Set the current state
                                currentState = Mode.CYCLING;
                                Log.e("Status", "Cycling");
                            } else if (location.getSpeed() >= 13) {
                                //Speed in the range shows the user is driving
                                //Set the current state
                                currentState = Mode.DRIVING;
                                Log.e("status", "Driving");
                            }
                            Log.e("Speed",""+location.getSpeed());
                        }
                        //If the mode is standing
                        else if (mode == Mode.STANDING)
                            currentMode = Mode.STANDING;
                            //If the mode is walking set the current mode to walking
                        else if (mode == Mode.WALKING)
                            currentMode = Mode.WALKING;
                            //If the mode is running set teh current mode to running
                        else if (mode == Mode.RUNNING)
                            currentMode = Mode.RUNNING;
                            //If the mode is running set teh current mode to cycling
                        else if (mode == Mode.CYCLING)
                            currentMode = Mode.CYCLING;
                            //If the mode is running set teh current mode to driving
                        else if (mode == Mode.DRIVING)
                            currentMode = Mode.DRIVING;
                        //Set the last location to the current location
                        lastLocation = location;
                        try {
                            //Try to get the current address
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            //Catch the current address as the stop address
                            stopAddress = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            //If there is an error in getting the stop address then set to error
                            stopAddress = "ERROR";
                        }
                        //Check the max speed travelled
                        maxSpeed(location.getSpeed());
                    }
                }
            }
        };
        try {
            //Try to get the location updates
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e("Tracker", "Error");
            e.printStackTrace();
        }
    }

    /**
     * A method to get distance travelled
     *
     * @return is the distance travelled
     */
    public float getDistance() {
        return totalDistanceTravel;
    }

    /**
     * A method to get the current tracking progress is running or not
     *
     * @return is the running variable that show whether the tracking progress is running or not
     */
    public Boolean getStatus() {
        return running;
    }

    /**
     * A method to get the time in string
     *
     * @return time in string format
     */
    public String getTime() {
        return time;
    }

    /**
     * A method to run the timer
     */
    void runTimer() {

        //Handler to send a runnable to the main looper with a delay of 1 seconds
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Increment the seconds
                seconds++;
                //Calculate the hours taken
                hours = seconds / 3600;
                //Calculate the minutes taken
                minutes = (seconds % 3600) / 60;
                //Calculate the seconds taken
                secs = seconds % 60;
                //Set the time format
                time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * A method to stop the timer
     */
    void stopTimer() {
        handler.removeMessages(0);
    }

    /**
     * A method to get the start address
     *
     * @return is the start address
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * A method to get the stop address
     *
     * @return is the stop address
     */
    public String getStopAddress() {
        return stopAddress;
    }

    /**
     * A method to get the max speed
     *
     * @return is the max speed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * A method to get the average speed
     *
     * @return is the average speed
     */
    public float getAvgSpeed() {
        return avgSpeed;
    }

    /**
     * A method to check the max speed
     *
     * @param speed the current speed
     */
    public void maxSpeed(float speed) {
        //If the maxSpeed is 0 which means it is not yet started
        if (maxSpeed == 0) {
            //Set the max speed to the current speed
            maxSpeed = speed;
        }
        //If the current speed is faster than the max speed
        if (speed > maxSpeed) {
            //Set the max speed to the current speed
            maxSpeed = speed;
        }
    }

    /**
     * A method to set the goal value
     *
     * @param value is the goal value
     */
    public void setGoal(int value) {
        goalValue = value;
    }

    /**
     * A method to get the goal value
     *
     * @return is the goal value
     */
    public int getGoal() {
        return goalValue;
    }

    /**
     * A method to get the current mode
     *
     * @return is the current mode
     */
    public Mode getCurrentMode() {
        return currentMode;
    }

    /**
     * A method to get the current state for dynamic tracking
     *
     * @return is the current state
     */
    public Mode getCurrentState() {
        return currentState;
    }

    /**
     * A method that used to test the time format and time calculation
     * @param testSeconds is the total seconds
     * @return is the time in string with a proper format
     */
    String runTimerTest(int testSeconds) {
        int testHours;
        int testMinutes;
        int testSecs;
        String testTime;
        //Calculate the hours taken
        testHours = testSeconds / 3600;
        //Calculate the minutes taken
        testMinutes = (testSeconds % 3600) / 60;
        //Calculate the seconds taken
        testSecs = testSeconds % 60;
        //Set the time format
        testTime = String.format(Locale.getDefault(), "%d:%02d:%02d", testHours, testMinutes, testSecs);
        return testTime;
    }

    /**
     * A method that used to test the logic of dynamic mode
     * @param speed is the current travel speed
     * @return is the current state or null id speed is lesser than 0
     */
    public Mode testDynamicLogic(int speed) {
        if (speed > 0 && speed < 1.5) {
            return Mode.STANDING;
        } else if (speed >= 1.5 && speed < 5) {
            return Mode.WALKING;
        } else if (speed >= 5 && speed < 7) {
            return Mode.RUNNING;
        } else if (speed >= 7 && speed < 13) {
            return Mode.CYCLING;
        } else if (speed >= 13) {
            return Mode.DRIVING;
        }
        return null;
    }
}

