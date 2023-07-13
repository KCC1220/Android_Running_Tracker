package com.example.assignment3.Tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.assignment3.Activity.MainActivity;
import com.example.assignment3.Database.Data;
import com.example.assignment3.Database.DataViewModel;
import com.example.assignment3.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TrackerService extends android.app.Service {
    //A variable to hold the tracker object
    Tracker tracker;
    //A notification builder to build a notification
    NotificationCompat.Builder mBuilder;
    //A notification id
    final int notificationID=1;
    //A notification manager that manage the notification
    NotificationManager notificationManager;
    //A handler object that to post message to the main looper
    Handler handler;
    //A reference to the data view model
    DataViewModel viewModel;
    //A reference to the activity context
    Context mainContext;
    //A string to get the current mode
    String mode;
    //A reference to the shared preference
    SharedPreferences sharedPref;
    //An editor for the shared preference
    SharedPreferences.Editor editor;
    /**
     * On create method that is called when the service is created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //Create a new tracker object
        tracker = new Tracker();
    }

    /**
     * On bind method which is called when the user is calling bind service
     * @param intent is the intent that pass from the user to the service
     * @return is the binder object of the service
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Return a new binder object
        return new MyBinder();
    }


    /**
     * On start command which is called when user start the service
     * @param intent is the intent that passed from the user to the service
     * @param flags is additional data about the start request
     * @param startId is the id that the specific user that request to start the service
     * @return is the semantics the system to use for the service's current started state
     */
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //A variable that get a flag whether the intent is getting from the activity of notification
        int flag = 0;
        //If the intent is not null
        if(intent!=null) {
            //Get the flag value from the intent
            flag = intent.getIntExtra("Stop", 0);
        }
        //If the flag is having value of 1 which means it is starting from the notification
        if(flag==1) {
            //Call onFinish method that indicate tracking progress is finished
            onFinish();
            //Save the current data to the database
            viewModel.insert(new Data(getDistance(), getAvgSpeed(), getTime(), getMaxSpeed(), getStartAddress(), getStopAddress(),"Stop From Notification",5,getGoal(),
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()),mode));
            //Make a toast showing the progress is saved
            Toast.makeText(mainContext,"Saved", Toast.LENGTH_LONG).show();
            //Save all the data into the shared preference
            editor.putFloat("distance",getDistance());
            editor.putInt("goal", getGoal());
            Log.e("Service","goal");
            editor.putString("time", getTime());
            editor.putFloat("maxSpeed", getMaxSpeed());
            editor.putFloat("avgSpeed", getAvgSpeed());
            editor.putString("startAddress", getStartAddress());
            editor.putString("endAddress", getStopAddress());
            editor.putString("mode",mode);
            editor.apply();
            //Stop the service
            stopSelf();
            //Stop the foreground service
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(notificationID);
            }
        }
        //The system should restart the service once it is stopped by the system
        return START_STICKY;
    }


    /**
     * A method to stop the service from running foregrounding
     */
    @SuppressLint("WrongConstant")
    public void onStopService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Stop the service running foreground
            stopForeground(notificationID);
        }
    }

    /**
     * An initialise method which initialise all the necessary object
     * @param context is a reference to the main activity context
     * @param activity is a reference to the main activity
     * @param viewModel is the DataViewModel that use to save into the database
     */
    public void init(Context context, Activity activity, DataViewModel viewModel){
        //Initialise the local viewModel variable
        this.viewModel = viewModel;
        //Initialise the local context variable
        this.mainContext = context;
        //Get a reference of the main activity shared preference
        sharedPref = mainContext.getSharedPreferences("com.example.assignment3", Context.MODE_PRIVATE);
        //Editor for the shared preference
        editor = sharedPref.edit();
        //Pass all the necessary reference to the object created
        tracker.init(context,activity);
    }

    /**
     * On start method which is called when the user ask to start the tracking progress
     * @param mode is the mode chosen by the user
     */
    public void onStart(Tracker.Mode mode){
        //Initialise the mode as a string which used for saving into the database
        this.mode = String.valueOf(mode);
        //Ask the tracker object to start the progress
        tracker.onStart(mode);
    }

    /**
     *On finish method which is called when the user end the tracking progress
     */
    public void onFinish(){
        //Stop the notification from being updating
        stopNotificationUpdate();
        //Ask the tracker to stop the progress
        tracker.onFinish();
    }

    /**
     * A method to get the total distance travelled
     * @return is the total distance travelled
     */
    public float getDistance(){
        return tracker.getDistance();
    }

    /**
     * A method to get the progress running status
     * @return is the status showing whether the progress is running or not
     */
    public Boolean getStatus(){
        return tracker.getStatus();
    }

    /**
     * A method to get the time taken as a string
     * @return is the time taken as a string
     */
    public String getTime(){
        return tracker.getTime();
    }

    /**
     * A method to get the starting address
     * @return is the start address
     */
    public String getStartAddress(){
        return tracker.getStartAddress();
    }

    /**
     * A method to get the stop address
     * @return is the stop address
     */
    public String getStopAddress(){
        return tracker.getStopAddress();
    }

    /**
     * A method to get the max speed
     * @return is the max speed
     */
    public float getMaxSpeed(){
        return tracker.getMaxSpeed();
    }

    /**
     * A method to get the average speed
     * @return is the average speed
     */
    public float getAvgSpeed(){
        return tracker.getAvgSpeed();
    }

    /**
     * A method to set the goal value
     * @param value is the goal value
     */
    public void setGoal(int value){
        tracker.setGoal(value);
    }

    /**
     * A method to get the goal value
     * @return is the goal value
     */
    public int getGoal(){return tracker.getGoal();}

    /**
     * A method to get the selected mode
     * @return the selected mode
     */
    public Tracker.Mode getMode(){
        return tracker.getCurrentMode();
    }

    /**
     * A method to get the current state
     * @return is the current state
     */
    public Tracker.Mode getState(){return tracker.getCurrentState();}
    /**
     * A method to create the notification
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createNotification(){
        //A notification manager of system service
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        //A notification channel id
        String CHANNEL_ID = "100";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Notification name
            CharSequence name = "channel name";
            //Notification description
            String description = "channel description";
            //Notification importance
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //Notification channel object
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            //Setting description to the notification channel
            channel.setDescription(description);
            //Ask the notification manager to create the notification channel
            notificationManager.createNotificationChannel(channel);
        }
        //Declaring the notification intent
        Intent notificationIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //Initialise the intent that point towards the main activity
                notificationIntent = new Intent(TrackerService.this, MainActivity.class);
            }
        }
        //Create a stack builder object
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //Add next intent to the stack builder
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        assert notificationIntent != null;
        //Set flags to the notification intent
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Creating a pending intent
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //Creating an intent for the action from notification
        Intent actionIntent = new Intent(TrackerService.this,TrackerService.class);
        //Add a flag into the action intent
        actionIntent.putExtra("Stop",1);
        //Initialise the action intent as a pending intent
        PendingIntent pendingActionIntent = PendingIntent.getService(TrackerService.this,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //Set the notification
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Running Tracker")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.running_notification)
                //Set the notification not removable
                .setOngoing(true)
                //The notification will only alert with noise once
                .setOnlyAlertOnce(true)
                //Set an action to the notification
                .addAction(R.drawable.ic_launcher_foreground,"STOP",pendingActionIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //Notify the user
        notificationManager.notify(notificationID,mBuilder.build());
        //Start the service as foreground
        startForeground(notificationID,mBuilder.build());
        //Call a method that keep updating the notification
        updateNotification();
    }

    /**
     * A method to keep the notification updated
     */
    public void updateNotification(){
        //Creating a new handler object
        handler = new Handler();
        //Post the runnable message to the looper
        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run()
            {
                //Set the current total distance travelled and the time taken
                mBuilder.setContentText(String.format("Distance Travelled: %.2f",getDistance())+"\nTime: "+getTime());
                //Update the notification
                notificationManager.notify(notificationID,mBuilder.build());
                //Update once in a second
                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * A method to stop the notification update
     */
    public void stopNotificationUpdate(){
        //Let user able to remove the notification
        mBuilder.setOngoing(false);
        //Set the content of the notification
        mBuilder.setContentText("Tracking Finished");
        //Alert the user
        mBuilder.setOnlyAlertOnce(false);
        //Notify the user
        notificationManager.notify(notificationID,mBuilder.build());
        //Stop the handler
        handler.removeMessages(0);
    }

    /**
     * A binder class. All the method inside is the same as the service
     * All of them is making use of the service method
     */
    public class MyBinder extends Binder implements IInterface {
        /**
         * A method to return the binder
         * @return is the binder
         */
        @Override
        public IBinder asBinder(){
            return this;
        }

        /**
         * A method to initialise all the necessary object in the service
         * @param context is the main activity context
         * @param activity is the main activity
         * @param viewModel is the Database view model
         */
        public void init(Context context, Activity activity, DataViewModel viewModel){
            TrackerService.this.init(context,activity,viewModel);
        }

        /**
         * A method to call when the user start the progress
         * @param mode is the mode selected
         */
        public void onStart(Tracker.Mode mode){
            TrackerService.this.onStart(mode);
        }

        /**
         * A method to stop the service
         */
        public void stopService(){
            TrackerService.this.onStopService();
        }

        /**
         * A method to create the notification
         */
        public void createNotification(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TrackerService.this.createNotification();
            }
        }

        /**
         * A method to call when the tracking is finished
         */
        public void onFinish(){
            TrackerService.this.stopNotificationUpdate();
            TrackerService.this.onFinish();
        }

        /**
         * A method to get the total distance
         * @return is the total distance travelled
         */
        public float getDistance(){
            return TrackerService.this.getDistance();
        }

        /**
         * A method to get the tracking progress status
         * @return is a status on whether the progress is running
         */
        public Boolean getStatus(){
            return TrackerService.this.getStatus();
        }

        /**
         * A method to get the time as a string
         * @return is the time as string
         */
        public String getTime(){
            return TrackerService.this.getTime();
        }

        /**
         * A method to get the start address
         * @return is the starting address
         */
        public String getStartAddress(){
            return TrackerService.this.getStartAddress();
        }

        /**
         * A method to get the stop address
         * @return is the sop address
         */
        public String getStopAddress(){
            return TrackerService.this.getStopAddress();
        }

        /**
         * A method to get the maximum speed
         * @return is the maximum speed
         */
        public float getMaxSpeed(){
            return TrackerService.this.getMaxSpeed();
        }

        /**
         * A method to get the average speed
         * @return is the average speed
         */
        public float getAvgSpeed(){
            return TrackerService.this.getAvgSpeed();
        }

        /**
         * A method to set the goal value
         * @param value is the goal value
         */
        public void setGoal(int value){
            TrackerService.this.setGoal(value);
        }

        /**
         * A method to get the goal value
         * @return is the goal value
         */
        public int getGoal(){return TrackerService.this.getGoal();}

        /**
         * A method to get the chosen mode
         * @return is the chosen mode
         */
        public Tracker.Mode getMode(){
            return TrackerService.this.getMode();
        }

        /**
         * A method to get the current state
         * @return is the current state
         */
        public Tracker.Mode getState(){return TrackerService.this.getState();}
    }

}
