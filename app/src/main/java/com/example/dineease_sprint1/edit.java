package com.example.dineease_sprint1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashMap;

public class edit  extends AppCompatActivity {

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
    private String reservation_id;

    String RESERVATION_NAME ;
    String NUMBER_OF_GUESTS ;
    String RESERVATION_DATE ;
    String RESERVATION_TIME ;
    String TABLE_TYPE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_page);
        // Initialize views
        reservationNameEditText = findViewById(R.id.resname);
        numberOfGuestsEditText = findViewById(R.id.gnum);
        dateButton = findViewById(R.id.date_button);
        timeButton = findViewById(R.id.time_button);
        reserveButton = findViewById(R.id.resbtn);
        indoorRadioButton = findViewById(R.id.indoor);
        outdoorRadioButton = findViewById(R.id.outdoor);
        seatingRadioGroup = findViewById(R.id.seatingRadioGroup);

        SharedPreferences sharedPreferences= edit.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String targetEmail = sharedPreferences.getString("email","email@hotmail.com");
        userId = sharedPreferences.getInt("userId",0);

        reservation_id = getIntent().getStringExtra("reservation_id");
        // Initialize database helper
        dbHelper = new DataBaseHelper(edit.this);
        int resId = Integer.parseInt(reservation_id);
        Cursor cursor = dbHelper.readReservationByID(resId);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    RESERVATION_NAME = cursor.getString(2); // Adjust index if needed
                    NUMBER_OF_GUESTS = cursor.getString(3); // Adjust index if needed
                    RESERVATION_DATE = cursor.getString(5); // Adjust index if needed
                    RESERVATION_TIME = cursor.getString(6); // Add the time to the ArrayList
                    TABLE_TYPE = cursor.getString(4); // Adjust index if needed
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(edit.this, "You do not have any parking reservations", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if(RESERVATION_NAME==null || RESERVATION_NAME.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(edit.this);
            builder.setTitle("Notification")
                    .setMessage("No data found")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }else {

            // Initialize booking count map
            bookingCountMap = new HashMap<>();
            dateButton.setText(RESERVATION_DATE);
            // Set click listener for date button
            dateButton.setOnClickListener(v -> {
                // Get current date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(edit.this,
                        (view1, year, monthOfYear, dayOfMonth) ->
                                dateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year),
                        mYear, mMonth, mDay);
                datePickerDialog.show();
            });

            timeButton.setText(RESERVATION_TIME);
            // Set click listener for time button
            timeButton.setOnClickListener(v -> {
                // Get current time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(edit.this,
                        (view12, hourOfDay, minute) -> {
                            String timePeriod = hourOfDay < 12 ? "AM" : "PM";
                            if (hourOfDay > 12) hourOfDay -= 12;
                            if (hourOfDay == 0) hourOfDay = 12;
                            timeButton.setText(String.format("%02d:%02d %s", hourOfDay, minute, timePeriod));
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            });

            reservationNameEditText.setText(RESERVATION_NAME);
            numberOfGuestsEditText.setText(NUMBER_OF_GUESTS);
            if(TABLE_TYPE.equals("Indoor")){
                indoorRadioButton.setChecked(true);
            }else{
                outdoorRadioButton.setChecked(true);
            }
            // Set click listener for reserve button
            reserveButton.setText("Update Reservation");
            reserveButton.setOnClickListener(v -> {
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
                boolean isUpdated = dbHelper.updateReservation(resId, reservationName, numberOfGuests, date, time, seatingPreference);

                if (isUpdated) {
                    // Reservation successfully inserted
                    // Prompt the user to reserve parking
                    showParkingReservationDialog(date, time);
                    // Update reservation count for the hour
                    updateReservationCount(time);
                } else {
                    // Failed to insert reservation
                    // You can handle the failure, such as displaying an error message
                    Toast.makeText(edit.this, "Failed to Reserve the Table", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(edit.this);
        builder.setTitle("Parking Reservation")
                .setMessage("Do you want to update parking lot?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle parking reservation
                        // You can navigate to a parking reservation fragment or perform any other action
                        Intent intent = new Intent(edit.this, parking.class);
                        intent.putExtra("selected_date", selectedDate);
                        intent.putExtra("selected_time", selectedTime);
                        intent.putExtra("reservation_id", reservation_id);
                        intent.putExtra("option", "update");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle if the user doesn't want to reserve parking
                        finish();
                    }
                })
                .show();
    }

    // Method to show dialog when reservation limit is reached
    private void showReservationLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(edit.this);
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
}
