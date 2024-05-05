package com.example.dineease_sprint1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class parking extends AppCompatActivity {

    private RadioButton disYes, disNo, parking1, parking2, parking3;
    private Button reserveButton;
    private DataBaseHelper dbHelper;
    String option = "new";
    private View backBtn;
    private String selectedParking = "";
    private String disability = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Initialize views
        disYes = findViewById(R.id.disyes);
        disNo = findViewById(R.id.disno);
        parking1 = findViewById(R.id.par1);
        parking2 = findViewById(R.id.par2);
        parking3 = findViewById(R.id.par3);
        reserveButton = findViewById(R.id.parbtn);
        backBtn = findViewById(R.id.backBtn);

        // Initialize database helper
        dbHelper = new DataBaseHelper(this);

        // Ensure that the PARKING_RESERVATIONS_TABLE is created
        dbHelper.getWritableDatabase();

        // Set click listener for reserve button
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveParking();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String reservation_id = getIntent().getStringExtra("reservation_id");
        option = getIntent().getStringExtra("option");
        if(option.equals("update")) {
            int resId = Integer.parseInt(reservation_id);
            Cursor cursor = dbHelper.readParkingReservation(resId);
            String parkingSpace = "";
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        parkingSpace = cursor.getString(1); // Adjust index if needed
                        disability = cursor.getString(4); // Adjust index if needed
                    } while (cursor.moveToNext());
                } else {
                    Toast.makeText(parking.this, "You do not have any parking reservations", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }


            if(parkingSpace.equals("")){
                option = "new";
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parking.this);
                String msg = "Table reservation has no parking reservation";

                builder.setTitle("Notification")
                        .setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }else{
                option = "update";
                reserveButton.setText("Update Reservation");
                if (parkingSpace.equals("1")) {
                    parking1.setChecked(true);
                } else if (parkingSpace.equals("2")) {
                    parking2.setChecked(true);
                } else if (parkingSpace.equals("3")) {
                    parking3.setChecked(true);
                }

                if(disability.equals("Yes")){
                    disYes.setChecked(true);
                }else{
                    disNo.setChecked(true);
                }
            }
        }
    }

    private void reserveParking() {
        if (parking1.isChecked()) {
            selectedParking = "1";
        } else if (parking2.isChecked()) {
            selectedParking = "2";
        } else if (parking3.isChecked()) {
            selectedParking = "3";
        }

        String disabilityStatus = disYes.isChecked() ? "Yes" : "No";
        disability = disabilityStatus;
        String selectedDate = getIntent().getStringExtra("selected_date");
        String selectedTime = getIntent().getStringExtra("selected_time");
        String reservation_id = getIntent().getStringExtra("reservation_id");

        if(checkInputs()) {
            if (option.equals("update")) {
                int resId = Integer.parseInt(reservation_id);
                boolean isUpdated = dbHelper.updateParkingReservation(resId, selectedParking, disabilityStatus, selectedDate, selectedTime);

                if (isUpdated) {
                    showSuccessDialog("update");
                } else {
                    showFailureDialog("update");
                }
            } else {
                int resId = Integer.parseInt(reservation_id);
                boolean isInserted = dbHelper.insertParkingReservation(resId, selectedParking, disabilityStatus, selectedDate, selectedTime);

                if (isInserted) {
                    showSuccessDialog("new");
                } else {
                    showFailureDialog("new");
                }

            }
        }
    }

    private void showSuccessDialog(String opt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = opt.equals("new") ? "added" : "updated";
        builder.setMessage("Parking reservation "+msg+" successful!")
                .setPositiveButton("OK", (dialog, which) -> {
                    redirectToHomepageFragment();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showFailureDialog(String opt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = opt.equals("new") ? "add" : "update";
        builder.setMessage("Failed to "+msg+" parking. Please try again.")
                .setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void redirectToHomepageFragment() {
        startActivity(new Intent(this, FragmentLoaderActivity.class));
        finish();
    }


    private boolean  checkInputs(){

        if(selectedParking.equals("")){
            showErrorMessage("Select parking space");
            return false;
        }
        if(disability.equals("")){
            showErrorMessage("Select parking space");
            return false;
        }

        return true;
    }

    private void showErrorMessage(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parking.this);
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
