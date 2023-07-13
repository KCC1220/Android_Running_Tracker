package com.example.assignment3.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.assignment3.ViewHelper.DataAdapter;
import com.example.assignment3.Database.DataViewModel;
import com.example.assignment3.R;

public class DatabaseView extends AppCompatActivity {
    RecyclerView recyclerView;
    DataViewModel viewModel;
    //Shared preference
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /**
     * Auto generated method that triggered when the activity is created
     * @param savedInstanceState is the saved state of the activity when configuration changed
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        //Initialise shared preference
        sharedPref = this.getSharedPreferences("com.example.assignment3", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        //Find the recycler view widget
        recyclerView = findViewById(R.id.recyclerView);
        //Initialise adapter
        final DataAdapter adapter = new DataAdapter(this);
        //Set adapter to the recycler view
        recyclerView.setAdapter(adapter);
        //Set layout to the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Get the view model of the database
        DataViewModel viewModel = new ViewModelProvider(DatabaseView.this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(DataViewModel.class);
        //Observe when there is changes in the live data
        viewModel.getAllData().observe(this, adapter::setData);
    }

    /**
     * On click method for the floating button that used to delete all data
     * @param v is the view that the button on
     */
    public void onClickDelete(View v){
        showConfirmation();
    }

    /**
     * Show a dialog which ask for confirmation whether the user want to delete all the data
     */
    public void showConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        //Create a yes/no dialog
        builder.setMessage("Are you sure you want to delete all data?");
        //Action on yes button
        builder.setPositiveButton("YES", (dialog, which) -> {
            //Get a reference to the database view model
            viewModel = new ViewModelProvider(this,
                    (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(DataViewModel.class);
            //Delete all the data
            viewModel.delete();
            //Delete shared preference data
            editor.clear();
            editor.apply();
            //Dismiss the dialog
            dialog.dismiss();
        });
        //Action on no button
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false);
    }
}