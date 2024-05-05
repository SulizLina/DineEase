package com.example.dineease_sprint1;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> RESERVATION_NAME;
    private ArrayList<String> NUMBER_OF_GUESTS;
    private ArrayList<String> TABLE_TYPE;
    private ArrayList<String> RESERVATION_DATE;
    private ArrayList<String> RESERVATION_TIME; // Add this ArrayList for time
    private ArrayList<String> RESERVATION_ID; // Add this ArrayList for time


    CustomAdapter(Context context, ArrayList<String> RESERVATION_ID, ArrayList<String> RESERVATION_NAME, ArrayList<String> NUMBER_OF_GUESTS, ArrayList<String> TABLE_TYPE, ArrayList<String> RESERVATION_DATE, ArrayList<String> RESERVATION_TIME) {
        this.context = context;
        this.RESERVATION_NAME = RESERVATION_NAME;
        this.NUMBER_OF_GUESTS = NUMBER_OF_GUESTS;
        this.TABLE_TYPE = TABLE_TYPE;
        this.RESERVATION_DATE = RESERVATION_DATE;
        this.RESERVATION_TIME = RESERVATION_TIME;
        this.RESERVATION_ID = RESERVATION_ID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.RESERVATION_NAME_id.setText("Reservation name: " + RESERVATION_NAME.get(position));
        holder.NUMBER_OF_GUESTS_id.setText("Guests: " + NUMBER_OF_GUESTS.get(position));
        holder.RESERVATION_DATE_id.setText("Date: " + RESERVATION_DATE.get(position));
        holder.RESERVATION_TIME_id.setText("Time: " + RESERVATION_TIME.get(position)); // Set time here
        holder.TABLE_TYPE_id.setText("Seating preference: " + TABLE_TYPE.get(position));
        holder.rev_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataBaseHelper myDB = new DataBaseHelper(context);
                int resID = Integer.parseInt(RESERVATION_ID.get(position));
                Cursor cursor = myDB.readParkingReservation(resID);
                String parkingSpace = "";
                String disability = "";
                try {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            parkingSpace = cursor.getString(1); // Adjust index if needed
                            disability = cursor.getString(4); // Adjust index if needed
                        } while (cursor.moveToNext());
                    } else {
                        Toast.makeText(context, "You do not have any parking reservations", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String msg = "Parking Space: "+parkingSpace+" \n Has Disability: "+disability;
                if(parkingSpace.equals("")){
                    msg = "No parking reserved for this reservation";
                }
                builder.setTitle("Parking Reservation")
                        .setMessage(msg)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return RESERVATION_NAME.size(); // Assuming all ArrayLists have the same size
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView RESERVATION_NAME_id, NUMBER_OF_GUESTS_id, TABLE_TYPE_id, RESERVATION_DATE_id, RESERVATION_TIME_id;
        RelativeLayout rev_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            RESERVATION_NAME_id = itemView.findViewById(R.id.title1); //guests1
            NUMBER_OF_GUESTS_id = itemView.findViewById(R.id.guests1);
            RESERVATION_DATE_id = itemView.findViewById(R.id.date1);
            RESERVATION_TIME_id = itemView.findViewById(R.id.time1); // Set time TextView here
            TABLE_TYPE_id = itemView.findViewById(R.id.seating1);
            rev_layout = itemView.findViewById(R.id.rev_layout);
        }
    }
}
