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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class manage_reservation extends Fragment {
    private DataBaseHelper dataBaseHelper;
    private CustomerModel customerModel;
    private CustomForManageAdapter adapter;

    private ArrayList<String> RESERVATION_ID;
    private ArrayList<String> RESERVATION_NAME;
    private ArrayList<String> NUMBER_OF_GUESTS;
    private ArrayList<String> TABLE_TYPE;
    private ArrayList<String> RESERVATION_DATE;
    private ArrayList<String> RESERVATION_TIME;
    private int userId;
    private RecyclerView recycler;

    private DataBaseHelper myDB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_activity_manage_reservation, container, false);
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String targetEmail = sharedPreferences.getString("email","email@hotmail.com");
        userId = sharedPreferences.getInt("userId",0);

        // Initialize database helper
        myDB = new DataBaseHelper(requireContext());

        recycler = rootView.findViewById(R.id.recyclerview);

        // Initialize ArrayLists
        RESERVATION_ID = new ArrayList<>();
        RESERVATION_NAME = new ArrayList<>();
        RESERVATION_DATE = new ArrayList<>();
        RESERVATION_TIME = new ArrayList<>();

        // Retrieve data from the database and populate ArrayLists
        storeDataInArrays(userId);

        // Initialize and set up RecyclerView with GridLayoutManager
        adapter = new CustomForManageAdapter(requireContext(), RESERVATION_ID, RESERVATION_NAME, RESERVATION_DATE, RESERVATION_TIME);
        recycler.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1); // 2 columns in the grid
        recycler.setLayoutManager(gridLayoutManager);

        dataBaseHelper = new DataBaseHelper(requireContext());
        customerModel = new CustomerModel();

        /*
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.deletebtn1||view.getId() == R.id.deletebtn2 ||view.getId() == R.id.deletebtn3) {
                    showConfirmationDialog();
                } else if (view.getId() == R.id.editbtn1 || view.getId() == R.id.editbtn2 ||view.getId() == R.id.editbtn3) {
                    int editButtonIndex = getEditButtonIndex(view.getId());
                    if (editButtonIndex != -1) {
                        editReservation(editButtonIndex);
                    }
                }
            }
        };*/

        SearchView searchView = rootView.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                RESERVATION_ID.clear();
                RESERVATION_NAME.clear();
                RESERVATION_DATE.clear();
                RESERVATION_TIME.clear();
                adapter.notifyDataSetChanged();
                try{
                    int q = Integer.parseInt(query);
                    searchReservationsById(userId, query); ;
                }catch (Exception e){
                    searchReservationsByName(userId, query); ;
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return rootView;
    }
/*
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.delete, null);
        builder.setView(dialogView);

        Button del_yes = dialogView.findViewById(R.id.del_yes);
        Button del_no = dialogView.findViewById(R.id.del_no);

        final AlertDialog dialog = builder.create();

        del_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleted = false;//deleteReservation(customerModel);

                if (isDeleted) {
                    Toast.makeText(requireContext(), "Reservation deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to delete reservation", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        del_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void editReservation(int editButtonIndex) {
        int reservationId = getReservationId(editButtonIndex);

        Intent intent = new Intent(requireContext(), edit.class);
        intent.putExtra("RESERVATION_ID", reservationId);
        startActivity(intent);
    }

    private int getEditButtonIndex(int buttonId) {
        int[] editButtonIds = {R.id.editbtn1, R.id.editbtn2, R.id.editbtn3};
        for (int i = 0; i < editButtonIds.length; i++) {
            if (buttonId == editButtonIds[i]) {
                return i;
            }
        }
        return -1; // Edit button not found
    }
    private int getReservationId(int editButtonIndex) {
        // Logic to retrieve the reservation ID based on the edit button index
        // Replace with your implementation
        return 0;
    }
*/

    private void storeDataInArrays(int userId) {
        Cursor cursor = myDB.readAllData(userId);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    RESERVATION_ID.add(cursor.getString(0)); // Adjust index if needed
                    RESERVATION_NAME.add(cursor.getString(2)); // Adjust index if needed
                    RESERVATION_DATE.add(cursor.getString(5)); // Adjust index if needed
                    RESERVATION_TIME.add(cursor.getString(6)); // Add the time to the ArrayList

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
    private void searchReservationsById(int userId, String reservation_id) {
        Cursor cursor = myDB.searchReservationsByID(userId,reservation_id);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    RESERVATION_ID.add(cursor.getString(0)); // Adjust index if needed
                    RESERVATION_NAME.add(cursor.getString(2)); // Adjust index if needed
                    RESERVATION_DATE.add(cursor.getString(5)); // Adjust index if needed
                    RESERVATION_TIME.add(cursor.getString(6)); // Add the time to the ArrayList

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
    private void searchReservationsByName(int userId, String name) {
        Cursor cursor = myDB.searchReservationsByName(userId,name);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    RESERVATION_ID.add(cursor.getString(0)); // Adjust index if needed
                    RESERVATION_NAME.add(cursor.getString(2)); // Adjust index if needed
                    RESERVATION_DATE.add(cursor.getString(5)); // Adjust index if needed
                    RESERVATION_TIME.add(cursor.getString(6)); // Add the time to the ArrayList

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