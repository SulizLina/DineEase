package com.example.dineease_sprint1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomForManageAdapter extends RecyclerView.Adapter<CustomForManageAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> RESERVATION_NAME;
    private ArrayList<String> RESERVATION_DATE;
    private ArrayList<String> RESERVATION_TIME; // Add this ArrayList for time
    private ArrayList<String> RESERVATION_ID; // Add this ArrayList for time


    CustomForManageAdapter(Context context, ArrayList<String> RESERVATION_ID, ArrayList<String> RESERVATION_NAME,  ArrayList<String> RESERVATION_DATE, ArrayList<String> RESERVATION_TIME) {
        this.context = context;
        this.RESERVATION_NAME = RESERVATION_NAME;
        this.RESERVATION_DATE = RESERVATION_DATE;
        this.RESERVATION_TIME = RESERVATION_TIME;
        this.RESERVATION_ID = RESERVATION_ID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reservation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.RESERVATION_NAME_id.setText( RESERVATION_NAME.get(position));
        holder.RESERVATION_DATE_id.setText("Date: " + RESERVATION_DATE.get(position));
        holder.RESERVATION_TIME_id.setText("Time: " + RESERVATION_TIME.get(position));
        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Notification")
                        .setMessage("Are you sure you want to delete this reservation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataBaseHelper myDB = new DataBaseHelper(context);
                                int resID = Integer.parseInt(RESERVATION_ID.get(position));
                                boolean isDeleted = myDB.DeleteReservation(resID);
                                if (isDeleted) {
                                    Toast.makeText(context, "Reservation deleted successfully", Toast.LENGTH_SHORT).show();
                                    RESERVATION_NAME.remove(position);
                                    RESERVATION_DATE.remove(position);
                                    RESERVATION_TIME.remove(position);
                                    RESERVATION_ID.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "Failed to delete reservation", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, edit.class);
                intent.putExtra("reservation_id", RESERVATION_ID.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return RESERVATION_NAME.size(); // Assuming all ArrayLists have the same size
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView RESERVATION_NAME_id, RESERVATION_DATE_id, RESERVATION_TIME_id;
         Button editbtn, deletebtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            RESERVATION_NAME_id = itemView.findViewById(R.id.resname3); //guests1
            RESERVATION_DATE_id = itemView.findViewById(R.id.date3);
            RESERVATION_TIME_id = itemView.findViewById(R.id.time3); // Set time TextView here
            editbtn = itemView.findViewById(R.id.editbtn);
            deletebtn = itemView.findViewById(R.id.deletebtn);
        }
    }
}
