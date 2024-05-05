package com.example.dineease_sprint1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Customer.db";
    public static final int DATABASE_VERSION = 4;

    // Customer table
    public static final String CUSTOMER_TABLE = "CUSTOMER_TABLE";
    public static final String COLUMN_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String COLUMN_CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String COLUMN_CUSTOMER_AGE = "CUSTOMER_AGE";
    public static final String COLUMN_CUSTOMER_EMAIL = "CUSTOMER_EMAIL";
    public static final String COLUMN_CUSTOMER_PASSWORD = "CUSTOMER_PASSWORD";

    // Reservation table
    public static final String RESERVATION_TABLE = "RESERVATION_TABLE";
    public static final String PARKING_RESERVATIONS_TABLE = "PARKING_RESERVATIONS_TABLE";
    public static final String COLUMN_RESERVATION_ID = "RESERVATION_ID";
    public static final String COLUMN_CUSTOMER_ID_FK = "CUSTOMER_ID";
    public static final String COLUMN_RESERVATION_NAME = "RESERVATION_NAME";
    public static final String COLUMN_NUMBER_OF_GUESTS = "NUMBER_OF_GUESTS";
    public static final String COLUMN_TABLE_TYPE = "TABLE_TYPE";
    public static final String COLUMN_DATE = "RESERVATION_DATE";
    public static final String COLUMN_TIME = "RESERVATION_TIME";
    public static final String COLUMN_PARKING = "RESERVATION_PARKING";
    public static final String COLUMN_NUMBER_OF_PARKING_SPOTS = "NUMBER_OF_PARKING_SPOTS";
    public static final String COLUMN_TYPE_OF_PARKING_SPOT = "TYPE_OF_PARKING_SPOT";
    public static final String COLUMN_DISABILITY = "COLUMN_DISABILITY";
    public static final String COLUMN_PARKING_SPACES = "COLUMN_PARKING_SPACES";
    public static final String COLUMN_SELECTED_DATE = "COLUMN_SELECTED_DATE";
    public static final String COLUMN_SELECTED_TIME = "COLUMN_SELECTED_TIME";
    public static final String RESERVATION_ID_FK = "RESERVATION_ID";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the customer table
        String createCustomerTableStatement = "CREATE TABLE IF NOT EXISTS " + CUSTOMER_TABLE + " (" +
                COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CUSTOMER_NAME + " TEXT, " +
                COLUMN_CUSTOMER_AGE + " INTEGER, " +
                COLUMN_CUSTOMER_EMAIL + " TEXT, " +
                COLUMN_CUSTOMER_PASSWORD + " TEXT)";

        db.execSQL(createCustomerTableStatement);

        // Create the reservation table
        String createReservationTableStatement = "CREATE TABLE IF NOT EXISTS " + RESERVATION_TABLE + " (" +
                COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CUSTOMER_ID_FK + " INTEGER, " +
                COLUMN_RESERVATION_NAME + " TEXT, " +
                COLUMN_NUMBER_OF_GUESTS + " INTEGER, " +
                COLUMN_TABLE_TYPE + " TEXT, " +
                COLUMN_DATE + " DATE, " +
                COLUMN_TIME + " TIME, " +
                COLUMN_PARKING + " INTEGER, " +
                COLUMN_NUMBER_OF_PARKING_SPOTS + " INTEGER, " +
                COLUMN_TYPE_OF_PARKING_SPOT + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_CUSTOMER_ID_FK + ") REFERENCES " + CUSTOMER_TABLE + "(" + COLUMN_CUSTOMER_EMAIL + ") ON DELETE CASCADE)";

        db.execSQL(createReservationTableStatement);

        // Create parking reservations table
        String createParkingReservationsTableStatement = "CREATE TABLE IF NOT EXISTS " + PARKING_RESERVATIONS_TABLE + " (" +
                RESERVATION_ID_FK + " INTEGER, " +
                COLUMN_PARKING_SPACES + " TEXT, " +
                COLUMN_SELECTED_DATE + " TEXT, " +
                COLUMN_SELECTED_TIME + " TEXT, " +
                COLUMN_DISABILITY + " TEXT)";
        db.execSQL(createParkingReservationsTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RESERVATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PARKING_RESERVATIONS_TABLE);
        onCreate(db);
    }
    public boolean addCustomer(CustomerModel customerModel){
        SQLiteDatabase db = this.getWritableDatabase(); //get the db
        ContentValues cv = new ContentValues(); //associative array
        cv.put(COLUMN_RESERVATION_ID,customerModel.getReservationId());
        cv.put(COLUMN_CUSTOMER_ID_FK,customerModel.getId());
        cv.put(COLUMN_RESERVATION_NAME,customerModel.getReservationName());
        cv.put(COLUMN_NUMBER_OF_GUESTS,customerModel.getNumberOfGuests());
        cv.put(COLUMN_TABLE_TYPE,customerModel.getTableType());
        cv.put(COLUMN_DATE,customerModel.getDate());
        cv.put(COLUMN_TIME,customerModel.getTime());
        cv.put(COLUMN_PARKING,customerModel.getParking());
        cv.put(COLUMN_NUMBER_OF_PARKING_SPOTS,customerModel.getNumberOfParkingSpots());
        cv.put(COLUMN_TYPE_OF_PARKING_SPOT,customerModel.isTypeOfParkingSpot());
        long insert = db.insert(RESERVATION_TABLE, null, cv);
        if(insert == -1){
            return false;
        }
        else {
            return true;
        }
    }
    public Boolean checkUserEmail(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + CUSTOMER_TABLE + " where " + COLUMN_CUSTOMER_EMAIL+ " = ?", new
                String[]{email});
        if (cursor.getCount() > 0) return true;
        return false;
    }
    public int checkUsernamePassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from " + CUSTOMER_TABLE + " where " + COLUMN_CUSTOMER_EMAIL + " = ? and " + COLUMN_CUSTOMER_PASSWORD +
                " = ?", new String[] {username,password});

        SQLiteDatabase db = this.getReadableDatabase();
        int userId = 0;
        if(cursor.moveToFirst()){
            // loop through cursor results
            do{
                userId = cursor.getInt(0);
            }while (cursor.moveToNext());
        }else{
            // nothing happens. no one is added.
        }
        return  userId;
    }


    public int getLastReservationID(){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select max("+COLUMN_RESERVATION_ID+") from " + RESERVATION_TABLE , new String[] {});

        SQLiteDatabase db = this.getReadableDatabase();
        int lastID = 0;
        if(cursor.moveToFirst()){
            // loop through cursor results
            do{
                lastID = cursor.getInt(0);
            }while (cursor.moveToNext());
        }else{
            // nothing happens. no one is added.
        }
        return  lastID;
    }
    public Boolean insertData(String username,String age,String email ,String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COLUMN_CUSTOMER_NAME, username);
        contentValues.put(COLUMN_CUSTOMER_AGE, age);
        contentValues.put(COLUMN_CUSTOMER_EMAIL, email);
        contentValues.put(COLUMN_CUSTOMER_PASSWORD, password);
        long result = MyDB.insert(CUSTOMER_TABLE, null, contentValues);
        if(result==-1) return false;
        return true;
    }
    public boolean addReservation(int cust_id, String reservationName, int numberOfGuests, String date, String time, String seatingPreference){
        SQLiteDatabase db = this.getWritableDatabase(); // Get the database
        ContentValues cv = new ContentValues(); // Initialize ContentValues

        // Put reservation details into ContentValues
        cv.put(COLUMN_CUSTOMER_ID_FK, cust_id);
        cv.put(COLUMN_RESERVATION_NAME, reservationName);
        cv.put(COLUMN_NUMBER_OF_GUESTS, numberOfGuests);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_TABLE_TYPE, seatingPreference);

        // Insert the reservation into the database
        long insert = db.insert(RESERVATION_TABLE, null, cv);

        // Check if insertion was successful
        if(insert == -1){
            return false; // Return false if insertion failed
        } else {
            return true; // Return true if insertion was successful
        }
    }

    public boolean updateReservation(int reservation_id, String reservationName, int numberOfGuests, String date, String time, String seatingPreference){
        SQLiteDatabase db = this.getWritableDatabase(); // Get the database
        ContentValues cv = new ContentValues(); // Initialize ContentValues

        // Put reservation details into ContentValues
        cv.put(COLUMN_RESERVATION_NAME, reservationName);
        cv.put(COLUMN_NUMBER_OF_GUESTS, numberOfGuests);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_TABLE_TYPE, seatingPreference);

        // Insert the reservation into the database
        long insert = db.update(RESERVATION_TABLE,  cv, COLUMN_RESERVATION_ID+" = ? ",
                new String[]{""+reservation_id});

        // Check if insertion was successful
        if(insert == -1){
            return false; // Return false if insertion failed
        } else {
            return true; // Return true if insertion was successful
        }
    }


    // if a user is deleted then all his/her reservations are deleted
    public boolean DeleteCustomerWithReservation(CustomerModel customerModel){
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete reservations first
        String deleteReservationQuery = "DELETE FROM " + RESERVATION_TABLE +
                " WHERE " + COLUMN_CUSTOMER_ID_FK + " = " + customerModel.getId();
        db.execSQL(deleteReservationQuery);

        // Delete customer
        String deleteCustomerQuery = "DELETE FROM " + CUSTOMER_TABLE +
                " WHERE " + COLUMN_CUSTOMER_ID + " = " + customerModel.getId();
        db.execSQL(deleteCustomerQuery);

        // Check if any rows were affected
        String checkQuery = "SELECT changes()";
        Cursor cursor = db.rawQuery(checkQuery, null);
        int rowsAffected = 0;
        if (cursor != null && cursor.moveToFirst()) {
            rowsAffected = cursor.getInt(0);
            cursor.close();
        }

        // Close the database connection
        db.close();

        return rowsAffected > 0;
    }

    public boolean DeleteReservation(int resv_id){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteReservationQuery = "DELETE FROM " + RESERVATION_TABLE +
                " WHERE " + COLUMN_RESERVATION_ID + " = " + resv_id;
        db.execSQL(deleteReservationQuery);
        // Check if any rows were affected
        String checkQuery = "SELECT changes()";
        Cursor cursor = db.rawQuery(checkQuery, null);
        int rowsAffected = 0;
        if (cursor != null && cursor.moveToFirst()) {
            rowsAffected = cursor.getInt(0);
            cursor.close();
        }

        // Close the database connection
        db.close();

        return rowsAffected > 0;
    }

    public List<CustomerModel> getEveryone(){
        List<CustomerModel> returnList = new ArrayList<>();
        // get data from database
        String queryString = "Select * from "+ RESERVATION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null); //result set
        if(cursor.moveToFirst()){
            // loop through cursor results
            do{
                String reservationName = cursor.getString(0);
                int number_of_guests = cursor.getInt(1);
                String date= cursor.getString(2);
                String table_type= cursor.getString(3);

                CustomerModel newCustomer = new CustomerModel(reservationName, number_of_guests,date,table_type);
                returnList.add(newCustomer);
            }while (cursor.moveToNext());
        }else{
            // nothing happens. no one is added.
        }
        //close
        cursor.close();
        db.close();
        return returnList;
    }

   /* Cursor readAllData(){
        String query = "Select * from "+RESERVATION_TABLE;
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor=null;
        if(db !=null){
            cursor=db.rawQuery(query,null);

        }
        return cursor;
    }*/

    public Cursor readAllData(int userId) {
        String query = "SELECT * from " + RESERVATION_TABLE + " WHERE " + COLUMN_CUSTOMER_ID_FK + "= ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{""+userId});
        return cursor;
    }

    public Cursor searchReservationsByID(int userId, String reservation_id) {
        String query = "SELECT * from " + RESERVATION_TABLE + " WHERE " + COLUMN_CUSTOMER_ID_FK + "= ? and "+COLUMN_RESERVATION_ID +" = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{""+userId, reservation_id});
        return cursor;
    }
    public Cursor searchReservationsByName(int userId, String name) {
        String query = "SELECT * from " + RESERVATION_TABLE + " WHERE " + COLUMN_CUSTOMER_ID_FK + "= ? and "+COLUMN_RESERVATION_NAME +" like ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{""+userId, "%"+name+"%"});
        return cursor;
    }

    public boolean insertParkingReservation(int reservation_id, String parkingSpaces, String disabilityStatus, String selectedDate, String selectedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(RESERVATION_ID_FK, reservation_id);
        cv.put(COLUMN_SELECTED_TIME, selectedTime);
        cv.put(COLUMN_PARKING_SPACES, parkingSpaces);
        cv.put(COLUMN_DISABILITY, disabilityStatus);
        cv.put(COLUMN_SELECTED_DATE, selectedDate);

        long result = db.insertWithOnConflict(PARKING_RESERVATIONS_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        return result != -1;
    }

    public boolean updateParkingReservation(int reservation_id, String parkingSpaces, String disabilityStatus, String selectedDate, String selectedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SELECTED_TIME, selectedTime);
        cv.put(COLUMN_PARKING_SPACES, parkingSpaces);
        cv.put(COLUMN_DISABILITY, disabilityStatus);
        cv.put(COLUMN_SELECTED_DATE, selectedDate);

        long result = db.update(PARKING_RESERVATIONS_TABLE, cv, RESERVATION_ID_FK+" = ?",
                new String[]{""+reservation_id});

        return result != -1;
    }

    public int getBookingCountForTime(String time, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        // Query the database to get the count of bookings for the given date and time
        String query = "SELECT COUNT(*) FROM " + RESERVATION_TABLE + " WHERE " + COLUMN_DATE + " = ? AND " + COLUMN_TIME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date, time});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        return count;
    }
    public void updateCustomer(CustomerModel customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", customer.getDate());
        values.put("time", customer.getTime());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(customer.getId())};

        db.update("CUSTOMER_TABLE", values, whereClause, whereArgs);

        db.close();
    }

    // Method to update reservation information
    public void updateReservation(CustomerModel Res) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, Res.getDate());
        values.put(COLUMN_TIME, Res.getTime());

        try {
            db.update(RESERVATION_TABLE, values, COLUMN_RESERVATION_ID + " = ?", new String[]{String.valueOf(Res.getReservationId())});
        } catch (Exception e) {
            Log.e("DB_UPDATE_ERROR", "Error updating reservation: " + e.getMessage());
        } finally {
            db.close();
        }
    }

    public CustomerModel getReservationById(int reservationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        CustomerModel reservation = null;

        Cursor cursor = db.query(
                RESERVATION_TABLE,
                new String[]{COLUMN_RESERVATION_ID, COLUMN_CUSTOMER_ID, COLUMN_RESERVATION_NAME,COLUMN_DATE,
                        COLUMN_TIME},
                COLUMN_RESERVATION_ID+ "=?",
                new String[]{String.valueOf(reservationId)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex(COLUMN_CUSTOMER_ID);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int timeIndex = cursor.getColumnIndex(COLUMN_TIME);
            int reservationIndex = cursor.getColumnIndex(COLUMN_RESERVATION_NAME);


            int userId = cursor.getInt(userIdIndex);
            String date = cursor.getString(dateIndex);
            String time = cursor.getString(timeIndex);
            String reaervationName = cursor.getString(reservationIndex);

            // Create the reservation model object
            reservation = new CustomerModel(reservationId,reaervationName,userId, date, time);

            cursor.close();
        }

        return reservation;
    }



    public Cursor readParkingReservation(int reservationID) {
        String query = "SELECT * from " + PARKING_RESERVATIONS_TABLE + " WHERE " + RESERVATION_ID_FK + "= ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{""+reservationID});
        return cursor;
    }


    public Cursor readReservationByID(int reservationID) {
        String query = "SELECT * from " + RESERVATION_TABLE + " WHERE " + COLUMN_RESERVATION_ID + "= ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{""+reservationID});
        return cursor;
    }
}