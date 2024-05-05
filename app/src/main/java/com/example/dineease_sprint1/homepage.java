package com.example.dineease_sprint1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class homepage extends Fragment {

    private Button logout;
    private RecyclerView recycler;
    private CustomAdapter adapter;
    private DataBaseHelper myDB;

    private ArrayList<String> RESERVATION_ID;
    private ArrayList<String> RESERVATION_NAME;
    private ArrayList<String> NUMBER_OF_GUESTS;
    private ArrayList<String> TABLE_TYPE;
    private ArrayList<String> RESERVATION_DATE;
    private ArrayList<String> RESERVATION_TIME;
    private int userId;

    public homepage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_homepage, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String targetEmail = sharedPreferences.getString("email","email@hotmail.com");
        userId = sharedPreferences.getInt("userId",0);

        // Initialize views
        logout = view.findViewById(R.id.logout);
        recycler = view.findViewById(R.id.recyclerview);

        // Initialize database helper
        myDB = new DataBaseHelper(requireContext());

        // Initialize ArrayLists
        RESERVATION_ID = new ArrayList<>();
        RESERVATION_NAME = new ArrayList<>();
        NUMBER_OF_GUESTS = new ArrayList<>();
        TABLE_TYPE = new ArrayList<>();
        RESERVATION_DATE = new ArrayList<>();
        RESERVATION_TIME = new ArrayList<>();

        // Set click listener for logout button
        logout.setOnClickListener(v -> {
            // Handle logout button click
            // You might want to handle logout logic here
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Logout?")
                    .setMessage("Hi! "+targetEmail+", \n Do you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle parking reservation
                            // You can navigate to a parking reservation fragment or perform any other action
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle if the user doesn't want to reserve parking
                        }
                    })
                    .show();
        });

        // Retrieve data from the database and populate ArrayLists
        storeDataInArrays(userId);

        // Initialize and set up RecyclerView with GridLayoutManager
        adapter = new CustomAdapter(requireContext(), RESERVATION_ID, RESERVATION_NAME, NUMBER_OF_GUESTS, TABLE_TYPE, RESERVATION_DATE, RESERVATION_TIME);
        recycler.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2); // 2 columns in the grid
        recycler.setLayoutManager(gridLayoutManager);
    }

    /*private void storeDataInArrays(String userEmail) {
        Cursor cursor = myDB.readAllData(userEmail); // Pass the user email here

        if (cursor.getCount() == 0) {
            Toast.makeText(requireContext(), "You do not have any reservations", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                RESERVATION_NAME.add(cursor.getString(2)); // Adjust index if needed
                NUMBER_OF_GUESTS.add(cursor.getString(3)); // Adjust index if needed
                RESERVATION_DATE.add(cursor.getString(5)); // Adjust index if needed
                RESERVATION_TIME.add(cursor.getString(6)); // Add the time to the ArrayList
                TABLE_TYPE.add(cursor.getString(4)); // Adjust index if needed
            }
        }
    }*/
    private void storeDataInArrays(int userEmail) {
        Cursor cursor = myDB.readAllData(userEmail);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
               do {
                   RESERVATION_ID.add(cursor.getString(0)); // Adjust index if needed
                    RESERVATION_NAME.add(cursor.getString(2)); // Adjust index if needed
                    NUMBER_OF_GUESTS.add(cursor.getString(3)); // Adjust index if needed
                    RESERVATION_DATE.add(cursor.getString(5)); // Adjust index if needed
                    RESERVATION_TIME.add(cursor.getString(6)); // Add the time to the ArrayList
                    TABLE_TYPE.add(cursor.getString(4)); // Adjust index if needed
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(requireContext(), "You do not have any reservations", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
