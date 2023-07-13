package com.example.assignment3.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment3.DataProvider.DataProviderContract;
import com.example.assignment3.Database.Data;
import com.example.assignment3.Database.DataViewModel;
import com.example.assignment3.ViewHelper.MainActivityViewModel;
import com.example.assignment3.R;
import com.example.assignment3.Tracker.Tracker;
import com.example.assignment3.Tracker.TrackerService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {
    //Permission IDs
    private static final int FINE_COARSE_PERMISSIONS = 99;
    private static final int BACKGROUND_PERMISSION = 100;
    //Array or permission needed
    String[] Fine_Coarse_permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    String[] background_permission = {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    //View needed in the main activity
    TextView distance;
    TextView startLocation;
    TextView stopLocation;
    TextView avgSpeed;
    TextView maxSpeed;
    ImageView status;
    TextView chronometer;
    Context mainContext;
    Activity mainActivity;
    ProgressBar progress;
    TextView goalView;
    TextView title;
    //Need to change to view model
    int goalValue;
    //View model for database
    DataViewModel viewModel;
    //View model for main activity
    private MainActivityViewModel model;
    //Handler that connect to the main looper
    final Handler handler = new Handler();
    //Bound service
    private TrackerService.MyBinder myService;
    //Intent to start service
    Intent serviceIntent;
    //Shared preference
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /**
     * Method called when the activity is created
     * @param savedInstanceState is the saved instance when configuration changes
     */
    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Disable night mode theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        //Initialise shared preference
        sharedPref = this.getSharedPreferences("com.example.assignment3", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        setContentView(R.layout.activity_main);
        //Initialise service intent
        serviceIntent = new Intent(MainActivity.this,TrackerService.class);
        //Bind service to the activity
        this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        //Get main activity view model
        model  = new ViewModelProvider(this).get(MainActivityViewModel.class);
        //Get database view model
        viewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(DataViewModel.class);
        //Finding view widget id
        distance = findViewById(R.id.distance);
        status = findViewById(R.id.mode);
        startLocation = findViewById(R.id.startLocation);
        stopLocation = findViewById(R.id.stopLocation);
        maxSpeed = findViewById(R.id.maxSpeed);
        goalView = findViewById(R.id.setGoal);
        avgSpeed = findViewById(R.id.avgSpeed);
        chronometer = findViewById(R.id.time);
        progress = findViewById(R.id.progress);
        title = findViewById(R.id.Title);
        //Get a reference of main activity
        mainActivity = this;
        //Get a reference to main activity context
        mainContext = this;
        //Set the progress bar to progress 0
        progress.setProgress(0);
        //Show all the widget to the user
        display();
        //Disable the title text view
        title.setVisibility(View.INVISIBLE);
        //Check if there is data for state
        if(sharedPref.contains("mode")){
            //If there is data for state then set the state to view model
            model.setMode((sharedPref.getString("mode",null)));
            //Check what is the previous tracking mode and set the state image respectively
            switch(model.getMode()){
                case "WALKING":
                    status.setBackgroundResource(R.drawable.walk);
                    break;
                case "RUNNING":
                    status.setBackgroundResource(R.drawable.run);
                    break;
                case "CYCLING":
                    status.setBackgroundResource(R.drawable.cycle);
                    break;
                case "DRIVING":
                    status.setBackgroundResource(R.drawable.drive);
                    break;
                case "DYNAMIC":
                    status.setBackgroundResource(R.drawable.dynamic);
                    break;
            }
        }
        //Testing content provider
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(DataProviderContract.TRACK_URI, new String[] {DataProviderContract.DISTANCE}, null, null, null);
        Log.e("Content Provider",""+cursor.getCount());
    }


    /**
     * A method to trigger when user press the start button
     * @param v is the view that the button in
     */
    public void onStart(View v) {
        //Check whether the permission are granted
        if (hasPermissions(mainContext, Fine_Coarse_permissions)) {
            //Check background location permission is granted or not
            if(hasPermissions(mainContext, background_permission)) {
                //If the tracking progress is not started yet then show the setup dialog
                if (!model.getTracking()) {
                    start();
                }
                //If there is a current tracking progress running then ask for confirmation to forfeit the current progress
                else
                    //Show the confirmation dialog
                    showConfirmation(1);
            }else{
                //Ask for background location access
                showConfirmation(0);
            }
        }else{
            //Request the fine and coarse location permission
            ActivityCompat.requestPermissions(mainActivity, Fine_Coarse_permissions, FINE_COARSE_PERMISSIONS);
            }

        }

    /**
     * A method to start the service and show setup menu
      */
    public void start(){
        if (!model.getTracking()) {
            //Show setup dialog
            showSetup();
        }
    }


    /**
     * A method called when user press on the data icon button
     * @param v is the view the button in
     */
    public void onDataView(View v){
        //Intent to start the data view activity
        Intent intent = new Intent(MainActivity.this,DatabaseView.class);
        //Start the data activity
        startActivity(intent);
    }

    /**
     * A method that the user press on the finish button
     * @param v is the view the button in
     */
    public void onFinish(View v){
        //Check if the tracking progress is running
        if(model.getTracking()) {
            Log.e("BUG","BUG TRACING");
            //Set tracking to false as tracking progress is stopped
            model.setTracking(false);
            //Telling service that user clicked the finish button
            myService.onFinish();
            //Set the time taken from the service to the model
            model.setTime(myService.getTime());
            //Show summary data
            showSummary();
            //Save data to shared preference
            editor.putFloat("distance",model.getTotalDistances());
            editor.putInt("goal", model.getGoalValue());
            editor.putString("time", model.getTime());
            editor.putFloat("maxSpeed", model.getMaxSpeed());
            editor.putFloat("avgSpeed", model.getAvgSpeed());
            editor.putString("startAddress", model.getStartAddress());
            editor.putString("endAddress", model.getStopAddress());
            editor.putString("mode",model.getMode());
            editor.apply();
            //Show finish dialog to the user
            showFinish();
            //Stop the service
            stopService(serviceIntent);
            //Stop foreground service
            myService.stopService();
        }
    }

    /**
     * A method to display all the information
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void display(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(myService!=null) {
                    //Check if the tracking progress is started
                    if (!model.getTracking()) {
                        //If not started then show the summary
                        showSummary();
                    } else
                        //If the tracking progress is running then hide the title which show for previous run
                        title.setVisibility(View.INVISIBLE);
                    //Set the current tracking status to the view model
                    model.setTracking(myService.getStatus());
                    //Check if there is distance data in the shared preference
                    if (sharedPref.contains("distance")) {
                        //If there is then set the saved data to the view model
                        model.setTotalDistances(sharedPref.getFloat("distance", 0));
                    } else {
                        //If there is not then set the view model with data from the service
                        model.setTotalDistances(myService.getDistance());
                    }
                    if (sharedPref.contains("time")) {
                        //If there is then set the saved data to the view model
                        model.setTime(sharedPref.getString("time", null));
                    } else {
                        //If there is not then set the view model with data from the service
                        model.setTime(myService.getTime());
                    }
                    if (sharedPref.contains("goal"))
                        model.setGoalValue(sharedPref.getInt("goal", 0));
                    else
                        model.setGoalValue(myService.getGoal());
                    //Update goal view UI
                    goalView.setText(model.getGoalValue() + "m");
                    //Update UI about the distance travelled
                    distance.setText(String.format("%.2fm", model.getTotalDistances()));
                    chronometer.setText(model.getTime());
                    //Set the current progress to the progress bar
                    progress.setProgress((int) (model.getTotalDistances() / model.getGoalValue() * 100));
                    //Check if there is state set in the service
                    if (myService.getMode() != null) {
                        //Check if the mode is set to dynamic
                        if (myService.getMode() == Tracker.Mode.DYNAMIC) {
                            //Check what is the current state and set the correct one to the image view
                            if (myService.getState() == Tracker.Mode.STANDING) {
                                status.setBackgroundResource(R.drawable.stand);
                            } else if (myService.getState() == Tracker.Mode.WALKING) {
                                status.setBackgroundResource(R.drawable.walk);
                            } else if (myService.getState() == Tracker.Mode.RUNNING) {
                                status.setBackgroundResource(R.drawable.run);
                            } else if (myService.getState() == Tracker.Mode.CYCLING) {
                                status.setBackgroundResource(R.drawable.cycle);
                            } else if (myService.getState() == Tracker.Mode.DRIVING) {
                                status.setBackgroundResource(R.drawable.drive);
                            }
                        }
                        //Check if the mode is set to walking
                        else if (myService.getMode() == Tracker.Mode.WALKING) {
                            status.setBackgroundResource(R.drawable.walk);
                        }
                        //Check if the mode is set to running
                        else if (myService.getMode() == Tracker.Mode.RUNNING) {
                            status.setBackgroundResource(R.drawable.run);
                        }
                        //Check if the mode is set to cycling
                        else if (myService.getMode() == Tracker.Mode.CYCLING) {
                            status.setBackgroundResource(R.drawable.cycle);
                        }
                        //Check if the mode is set to driving
                        else if (myService.getMode() == Tracker.Mode.DRIVING) {
                            status.setBackgroundResource(R.drawable.drive);
                        }
                    }
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    /**
     * A method to show all the summary information when the tracking progress is stopped
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void showSummary(){
        //Show all the summary related widget to the UI
        startLocation.setVisibility(View.VISIBLE);
        stopLocation.setVisibility(View.VISIBLE);
        maxSpeed.setVisibility(View.VISIBLE);
        avgSpeed.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        //Check if there is data in shared preference then use the data else use data from service
        if(sharedPref.contains("startAddress"))
            model.setStartAddress(sharedPref.getString("startAddress",null));
        else
            model.setStartAddress(myService.getStartAddress());
        if(sharedPref.contains("endAddress"))
            model.setStopAddress(sharedPref.getString("endAddress",null));
        else
            model.setStopAddress(myService.getStopAddress());
        if(sharedPref.contains("maxSpeed"))
            model.setMaxSpeed(sharedPref.getFloat("maxSpeed",0));
        else
            model.setMaxSpeed(myService.getMaxSpeed());
        if(sharedPref.contains("distance"))
            model.setTotalDistances(sharedPref.getFloat("distance",0));
        else
            model.setTotalDistances(myService.getDistance());
        if(sharedPref.contains("mode"))
            model.setMode(sharedPref.getString("mode",null));
        else
            model.setMode(String.valueOf(myService.getMode()));
        if(sharedPref.contains("time"))
            model.setTime(sharedPref.getString("time",null));
        else
            model.setTime(myService.getTime());
        if(sharedPref.contains("avgSpeed"))
            model.setAvgSpeed(sharedPref.getFloat("avgSpeed",0));
        else
            model.setAvgSpeed(myService.getAvgSpeed());
        //Set all the data to the correspond widgets
        chronometer.setText(model.getTime());
        startLocation.setText("Start Address: " + model.getStartAddress());
        stopLocation.setText("End Address: " + model.getStopAddress());
        maxSpeed.setText(String.format("Max Speed:\n%.2f m/s",model.getMaxSpeed()));
        avgSpeed.setText(String.format("Avg Speed:\n%.2f m/s",model.getAvgSpeed()));
    }

    /**
     * A method to show a pop up menu for user to set the goal and the tracking mode
     */
    public void showSetup(){
        AlertDialog setupDialog;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainContext);
        //Inflate the custom layout
        @SuppressLint("InflateParams") final View popUp = getLayoutInflater().inflate(R.layout.pop_up_menu,null);
        //Set the custom layout to the view
        dialogBuilder.setView(popUp);
        setupDialog = dialogBuilder.create();
        //Show the pop up menu
        setupDialog.show();
        //Make the user not able to quit the pop up menu by touching outside the pop up menu
        setupDialog.setCanceledOnTouchOutside(false);
        //Widget in the pop up menu
        EditText goal;
        Button run;
        Button walk;
        Button dynamic;
        Button cycle;
        Button drive;
        //Find all the widget in the pop up menu
        goal = popUp.findViewById(R.id.Goal);
        run = popUp.findViewById(R.id.Running);
        walk = popUp.findViewById(R.id.Walking);
        dynamic = popUp.findViewById(R.id.Dynamic);
        cycle = popUp.findViewById(R.id.Cycling);
        drive = popUp.findViewById(R.id.Driving);
        //Set click method for buttons in pop up menu
        run.setOnClickListener(view -> {
            //Get the value key in by user
            goalValue = Integer.parseInt(goal.getText().toString());
            //Set as a new progress
            setNewProgress("RUNNING",goalValue);
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
        walk.setOnClickListener(view -> {
            //Get the value key in by user
            goalValue = Integer.parseInt(goal.getText().toString());
            //Set as a new progress
            setNewProgress("WALKING",goalValue);
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
        dynamic.setOnClickListener(view -> {
            //Get the value key in by user
            goalValue = Integer.parseInt(goal.getText().toString());
            //Set as a new progress
            setNewProgress("DYNAMIC",goalValue);
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
        cycle.setOnClickListener(view -> {
            //Get the value key in by user
            goalValue = Integer.parseInt(goal.getText().toString());
            //Set as a new progress
            setNewProgress("CYCLING",goalValue);
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
        drive.setOnClickListener(view -> {
            //Get the value key in by user
            goalValue = Integer.parseInt(goal.getText().toString());
            //Set as a new progress
            setNewProgress("DRIVING",goalValue);
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
    }

    /**
     * A method to set a new progress
     * @param mode is the mode chosen by the user
     * @param goalValue is the goal value of the user
     */
    public void setNewProgress(String mode,int goalValue){
        model.setGoalValue(goalValue);
        //Show the setup menu
        setUp(Tracker.Mode.valueOf(mode));
        //Clear data in the shared preference
        editor.clear();
        editor.apply();
        //Save the current state to the shared preference
        editor.putString("mode",mode);
        editor.apply();
        //Set the current state to the view model
        model.setMode(mode);
    }
    /**
     * A method to show a pop up menu when the tracking progress is stopped and ask user about rating and comment
     */
    public void showFinish(){
        AlertDialog setupDialog;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainContext);
        //inflate the custom layout with finish pop up
        @SuppressLint("InflateParams") final View popUp = getLayoutInflater().inflate(R.layout.finish_pop_up,null);
        //Set the custom layout to view
        dialogBuilder.setView(popUp);
        setupDialog = dialogBuilder.create();
        //Show the pop up menu
        setupDialog.show();
        //Set the user unable to cancel the pop up menu by clicking outside the pop up menu
        setupDialog.setCanceledOnTouchOutside(false);
        //Widgets in the pop up menu
        Button saveButton;
        RatingBar ratingBar;
        EditText comments;
        //Find all the pop up menu widget
        saveButton=popUp.findViewById(R.id.save);
        ratingBar = popUp.findViewById(R.id.ratingBar);
        comments = popUp.findViewById(R.id.comment);
        //Set on click method for button in the pop up menu
        saveButton.setOnClickListener(view -> {
            //Get comment that entered by the user
            String comment = comments.getText().toString();
            //Get the number of ratings that the user had input
            float rating = ratingBar.getRating();
            //Insert the data that need to be save to the database via view model
            viewModel.insert(new Data(model.getTotalDistances(), model.getAvgSpeed(), model.getTime(), model.getMaxSpeed(), model.getStartAddress(), model.getStopAddress(),comment,rating,model.getGoalValue(),
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()),model.getMode()));
            //Toast a text to tell the user data is being saved
            Toast.makeText(this,"Data Saved", Toast.LENGTH_SHORT).show();
            //Dismiss the pop up menu
            setupDialog.dismiss();
        });
    }

    /**
     * A method to show for confirmation pop up menu
     * @param flag is to show which confirmation menu either the one to request permission or the one to stop
     *             the current tracking progress
     */
    public void showConfirmation(int flag){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(flag==1) {
            //Set a title for the a normal pop up menu
            builder.setTitle("Confirm");
            //Set message of the pop up menu
            builder.setMessage("There is a current running tracker. Do you want to forfeit the current?");
        }else{
            //Set a title for the a normal pop up menu
            builder.setTitle("Permission Request");
            //Set message of the pop up menu
            builder.setMessage("In order to track your progress more precisely, we will need to access your background location. " +
                    "Please grant the permission to start the tracking progress.");
        }
        //Set on click method for clicking the yes button which user want to start a new tracking progress
        builder.setPositiveButton("YES", (dialog, which) -> {
            if(flag==1) {
                //Set tracking to false
                model.setTracking(false);
                //Inform service that tracking progress is finished
                myService.onFinish();
                //Stop the handler in updating the UI
                handler.removeMessages(0);
                //Stop the service
                stopService(serviceIntent);
                //Stop foreground service
                myService.stopService();
                start();
            }else{
                ActivityCompat.requestPermissions(mainActivity, background_permission, BACKGROUND_PERMISSION);
            }
            //Dismiss the dialog
            dialog.dismiss();
        });
        //On click method for clicking the no button
        builder.setNegativeButton("NO", (dialog, which) -> {
            if(flag==1) {
                //Tell the user that tracking progress is resumed
                Toast.makeText(this, "Resumed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
            //Dismiss the pop up menu
            dialog.dismiss();
        });
        //Create the dialog box
        AlertDialog alert = builder.create();
        //Show the dialog box to the user
        alert.show();
        //Set not to let the user exit the pop up menu by clicking outside the pop up menu
        alert.setCanceledOnTouchOutside(false);
    }

    /**
     * A method to setup the tracking progress
     * @param mode is the mode chosen by the user
     */
    public void setUp(Tracker.Mode mode){
        //Check if the tracking progress had started
        if (!model.getTracking()) {
            //Start the service
            startService(serviceIntent);
            //Start the service in foreground and create notification
            myService.createNotification();
            //Hide the summary related widget
            startLocation.setVisibility(View.INVISIBLE);
            stopLocation.setVisibility(View.INVISIBLE);
            maxSpeed.setVisibility(View.INVISIBLE);
            avgSpeed.setVisibility(View.INVISIBLE);
            //Pass the selected mode to the service
            myService.onStart(mode);
            //Inform service about the goal value
            myService.setGoal(goalValue);
            //Start the tracking progress
            model.setTracking(true);
            //If the tracking progress is running then stop the handler
            handler.removeMessages(0);
            //Show all the widget to the user
            display();
        }
    }

    /**
     * A method to catch user response of the permission requested
     * @param requestCode is the request code
     * @param permissions is the type of permission needed
     * @param grantResults is the result from the user
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check if the requested permission is granted
        if(requestCode== FINE_COARSE_PERMISSIONS) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showConfirmation(0);
            }else{
                //Make a toast to tell the user that permission is needed in order to start
                Toast.makeText(this,"Location permission is needed to track progress", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * A method to check whether the needed permission is granted
     * @param context is the application context
     * @param permissions is the permission needed
     * @return is a boolean to show whether all the permission is granted or not
     */
    //Reference: https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        //Check whether the permission that is needed is granted or not
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A service connection method
     */
    private final ServiceConnection serviceConnection = new ServiceConnection()
    {
        @SuppressLint("SetTextI18n")
        @Override
        //When the service is connected
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Get the binder
            myService = (TrackerService.MyBinder) service;
            //Get the status of the tracking progress
            model.setTracking(myService.getStatus());
            //Initialise all the necessary component
            myService.init(mainContext,mainActivity,viewModel);
        }
        @Override
        //When the service is disconnected
        public void onServiceDisconnected(ComponentName componentName) {
            myService = null;
        }
    };

    /**
     * A method to check whether there is running service
     * @return true when the service is running and false when the service is not running
     */
    //Reference: https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    //Check whether there is running services
    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(TrackerService.class.getName().equals(service.service.getClassName())) {
                Log.e("Foreground",""+service.service.getClassName());
                return true;
            }
        }
        return false;
    }

    /**
     * A method to called when activity get destroyed
     */
    @Override
    protected void onDestroy() {
        //If there is no active tracking progress and the service is connected
        if(!model.getTracking()&&myService!=null) {
            //Disconnect the service when the activity get destroyed
            unbindService(serviceConnection);
            //Stop the handler
            handler.removeMessages(0);
        }
        Log.e("service",""+foregroundServiceRunning());
        Log.e("Destroy","I get destroyed");
        super.onDestroy();
    }


}