package com.example.dineease_sprint1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashMap;

public class reservation extends Fragment {

    private EditText reservationNameEditText;
    private EditText numberOfGuestsEditText;
    private Button dateButton;
    private Button timeButton;
    private Button reserveButton;
    private RadioButton indoorRadioButton;
    private RadioButton outdoorRadioButton;
    private RadioGroup seatingRadioGroup;
    private DataBaseHelper dbHelper;

    private HashMap<String, Integer> bookingCountMap;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int userId;
    private int lastID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reservation_page, container, false);

        // Initialize views
        reservationNameEditText = view.findViewById(R.id.resname);
        numberOfGuestsEditText = view.findViewById(R.id.gnum);
        dateButton = view.findViewById(R.id.date_button);
        timeButton = view.findViewById(R.id.time_button);
        reserveButton = view.findViewById(R.id.resbtn);
        indoorRadioButton = view.findViewById(R.id.indoor);
        outdoorRadioButton = view.findViewById(R.id.outdoor);
        seatingRadioGroup = view.findViewById(R.id.seatingRadioGroup);
        
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String targetEmail = sharedPreferences.getString("email","email@hotmail.com");
        userId = sharedPreferences.getInt("userId",0);

        // Initialize database helper
        dbHelper = new DataBaseHelper(requireContext());

        // Initialize booking count map
        bookingCountMap = new HashMap<>();

        // Set click listener for date button
        dateButton.setOnClickListener(v -> {
            // Get current date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year, monthOfYear, dayOfMonth) ->
                            dateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year),
                    mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        // Set click listener for time button
        timeButton.setOnClickListener(v -> {
            // Get current time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view12, hourOfDay, minute) -> {
                        String timePeriod = hourOfDay < 12 ? "AM" : "PM";
                        if (hourOfDay > 12) hourOfDay -= 12;
                        if (hourOfDay == 0) hourOfDay = 12;
                        timeButton.setText(String.format("%02d:%02d %s", hourOfDay, minute, timePeriod));
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        // Set click listener for reserve button
        reserveButton.setOnClickListener(v -> {

            if(checkInputs()) {
                // Retrieve reservation details from EditText fields
                String reservationName = reservationNameEditText.getText().toString();
                int numberOfGuests = Integer.parseInt(numberOfGuestsEditText.getText().toString());
                String date = dateButton.getText().toString();
                String time = timeButton.getText().toString();

                // Retrieve selected seating preference
                String seatingPreference = "";
                int selectedId = seatingRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.indoor) {
                    seatingPreference = "Indoor";
                } else if (selectedId == R.id.outdoor) {
                    seatingPreference = "Outdoor";
                }

                // Check if reservation limit for the hour is exceeded
                if (!isReservationAllowed(time, date)) {
                    // Reservation limit exceeded for this hour
                    showReservationLimitDialog();
                    return; // Exit method
                }
                // Insert reservation into database
                boolean isInserted = dbHelper.addReservation(userId, reservationName, numberOfGuests, date, time, seatingPreference);

                if (isInserted) {
                    lastID = dbHelper.getLastReservationID();
                    // Reservation successfully inserted
                    // Prompt the user to reserve parking
                    showParkingReservationDialog(date, time);
                    // Update reservation count for the hour
                    updateReservationCount(time);
                } else {
                    // Failed to insert reservation
                    // You can handle the failure, such as displaying an error message
                    Toast.makeText(requireContext(), "Failed to Reserve the Table", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean  checkInputs(){

        String reservationName = reservationNameEditText.getText().toString();
        if(reservationName.equals("")){
            showErrorMessage("Set reservation name");
            return false;
        }
        try{
            int numberOfGuests = Integer.parseInt(numberOfGuestsEditText.getText().toString());
            if(numberOfGuestsEditText.getText().toString().equals("")){
                showErrorMessage("set number of guests");
                return false;
            }
        }catch (Exception e){
            showErrorMessage("set number of guests");
            return false;
        }
        String date = dateButton.getText().toString();
        if(date.toUpperCase().equals("SELECT DATE")){
            showErrorMessage("set date");
            return false;
        }
        String time = timeButton.getText().toString();
        if(time.toUpperCase().equals("SELECT TIME")){
            showErrorMessage("set time");
            return false;
        }
        // Retrieve selected seating preference
        String seatingPreference = "";
        int selectedId = seatingRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.indoor) {
            seatingPreference = "Indoor";
        } else if (selectedId == R.id.outdoor) {
            seatingPreference = "Outdoor";
        }else{
            showErrorMessage("set seating preference");
            return false;
        }

        return true;
    }
    // Method to check if reservation is allowed for the given time
    private boolean isReservationAllowed(String time, String date) {
        // Get the count of bookings for the given time from the database
        int count = dbHelper.getBookingCountForTime(time, date);

        // Check if count exceeds the limit (e.g., 2 reservations per time slot)
        //you can change this no to get only that no of reservation per hour
        return count < 4;
    }


    // Method to update reservation count for the given time
    private void updateReservationCount(String time) {
        // Increment the count for the given time
        int count = bookingCountMap.containsKey(time) ? bookingCountMap.get(time) : 0;
        bookingCountMap.put(time, count + 1);
    }

    // Method to show parking reservation dialog
    private void showParkingReservationDialog(String selectedDate, String selectedTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Parking Reservation")
                .setMessage("Do you want to reserve a parking lot?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle parking reservation
                        // You can navigate to a parking reservation fragment or perform any other action
                        Intent intent = new Intent(requireContext(), parking.class);
                        intent.putExtra("selected_date", selectedDate);
                        intent.putExtra("selected_time", selectedTime);
                        intent.putExtra("reservation_id", ""+lastID);
                        intent.putExtra("option", "new");
                        startActivity(intent);
                        requireActivity().finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle if the user doesn't want to reserve parking
                        //requireActivity().finish();
                        bookingComplete();
                    }
                })
                .show();
    }

    // Method to show dialog when reservation limit is reached
    private void showReservationLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reservation Limit Exceeded")
                .setMessage("Sorry, the maximum number of reservations for this hour has been reached. Please select another time.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                    }
                })
                .show();
    }

    private void bookingComplete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reservation done successfully")
                .setMessage("You've made table reservation without parking reservation.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        Fragment selectedFragment = new manage_reservation();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

                    }
                })
                .show();
    }
    private void showErrorMessage(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reservation Note:")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                    }
                })
                .show();
    }
}
