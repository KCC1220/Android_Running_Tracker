package com.example.assignment3.ViewHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3.Database.Data;
import com.example.assignment3.R;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {
    private List<Data> data;
    private final LayoutInflater layoutInflater;

    /**
     * A constructor use to initialise all the object
     * @param context is the main activity context
     */
    public DataAdapter(Context context) {
        //Create a new array list object
        this.data = new ArrayList<>();
        //Inflate the layout
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * A method called when the recycler view needs a view holder to represent an item
     * @param parent is the parent of the view which where the item view will be added
     * @param viewType is the view type of the new view
     * @return is the view holder for the given view type
     */
    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the item view
        View itemView = layoutInflater.inflate(R.layout.item_view, parent, false);
        //Return the DataViewHolder with the item view
        return new DataViewHolder(itemView);
    }


    /**
     * This method is to update and display data at specific location
     * @param holder is the view holder whose contents should be updated
     * @param position is the position of the holder with respect to this adapter
     */
    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    /**
     * Get the number of data
     * @return the number of data
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * A method to set all the data into the list
     * @param newData is the new data as a list
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Data> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    /**
     * A DataViewHolder class that control the item view
     */
    static class DataViewHolder extends RecyclerView.ViewHolder {

        //All the widget in the item view
        TextView totalDistanceView;
        TextView totalTimeView;
        TextView avgSpeedView;
        TextView maxSpeedView;
        TextView startAddressView;
        TextView endAddressView;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch goalAchievedView;
        TextView goalView;
        RatingBar ratingBar;
        TextView comment;
        TextView date;
        TextView mode;
        //A constructor to DataViewHolder which will find all the widget using id
        DataViewHolder(View itemView) {
            super(itemView);
            //Find all the widget using id
            totalDistanceView = itemView.findViewById(R.id.dataTotalDistance);
            totalTimeView = itemView.findViewById(R.id.dataTotalTime);
            avgSpeedView = itemView.findViewById(R.id.dataAvgSpeed);
            maxSpeedView = itemView.findViewById(R.id.dataMaxSpeed);
            startAddressView = itemView.findViewById(R.id.dataStartAddress);
            endAddressView = itemView.findViewById(R.id.dataStopAddress);
            goalAchievedView = itemView.findViewById(R.id.goalAchieved);
            goalView = itemView.findViewById(R.id.dataGoal);
            comment = itemView.findViewById(R.id.CommentView);
            ratingBar = itemView.findViewById(R.id.untouchableRatingBar);
            date = itemView.findViewById(R.id.dateView);
            mode = itemView.findViewById(R.id.trackingMode);
        }

        /**
         * A method to bind all the data into the ui
         * @param data is the data
         */
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bind(final Data data) {
            if (data != null) {
                //Update all the UI
                totalDistanceView.setText(String.format("Total Distance\n%.2fm",data.getDistance()));
                totalTimeView.setText("Time Taken\n"+data.getTotalTime());
                avgSpeedView.setText(String.format("Average Speed\n%.2fm/s",data.getAvgSpeed()));
                maxSpeedView.setText(String.format("Max Speed\n%.2fm/s",data.getMaxSpeed()));
                startAddressView.setText("Start Address:\n"+data.getStartAddress());
                endAddressView.setText("Stop Address:\n"+data.getStopAddress());
                goalAchievedView.setChecked(data.getDistance() >= data.getGoal());
                goalView.setText("Goal: "+data.getGoal()+"m");
                comment.setText("Comments:\n"+data.getComments());
                ratingBar.setRating(data.getRating());
                date.setText("Date: "+data.getDate());
                mode.setText("Tracking Mode: "+data.getMode());
            }
        }

    }
}
