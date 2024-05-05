package com.example.dineease_sprint1;

    import androidx.annotation.NonNull;

    public class CustomerModel {
        private int id;
        private String name;
        private int age;
        private String email;
        private String password;

        // Constructor
        public CustomerModel(int id, String name, int age, String email, String password) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
            this.password = password;
        }


        // Additional properties for reservation
        private int reservationId;
        private String reservationName;
        private int numberOfGuests;
        private String tableType;
        private String date;
        private String time;
        private Boolean parking;
        private int numberOfParkingSpots;
        private boolean typeOfParkingSpot;

        // Constructor including reservation properties
        public CustomerModel(int id, String name, int age, String email, String password, int reservationId,
                             String reservationName, int numberOfGuests, String tableType, String date, String time,
                             Boolean parking, int numberOfParkingSpots, boolean typeOfParkingSpot) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
            this.password = password;
            this.reservationId = reservationId;
            this.reservationName = reservationName;
            this.numberOfGuests = numberOfGuests;
            this.tableType = tableType;
            this.date = date;
            this.time = time;
            this.parking = parking;
            this.numberOfParkingSpots = numberOfParkingSpots;
            this.typeOfParkingSpot = typeOfParkingSpot;
        }
        public CustomerModel(String reservationName, int numberOfGuests, String date, String tableType) {

            this.reservationName = reservationName;
            this.numberOfGuests = numberOfGuests;
            this.date = date;
            this.tableType = tableType;

        }

        public CustomerModel() {

        }


        @NonNull
        @Override
        public String toString() {
            return "com.example.tableres.MainActivity.CustomerModel{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", reservationid='" + reservationId + '\'' +
                    ", reservationName='" + reservationName + '\'' +
                    ", numberOfGuests=" + numberOfGuests +
                    ", tableType='" + tableType + '\'' +
                    ", date=" + date +
                    ", time=" + time +
                    ", parking=" + parking +
                    ", numberOfParkingSpots=" + numberOfParkingSpots +
                    ", typeOfParkingSpot=" + typeOfParkingSpot +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getReservationName() {
            return reservationName;
        }

        public void setReservationName(String reservationName) {
            this.reservationName = reservationName;
        }

        public int getNumberOfGuests() {
            return numberOfGuests;
        }

        public void setNumberOfGuests(int numberOfGuests) {
            this.numberOfGuests = numberOfGuests;
        }

        public String getTableType() {
            return tableType;
        }

        public void setTableType(String tableType) {
            this.tableType = tableType;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }

        public Boolean getParking() {
            return parking;
        }

        public void setParking(Boolean parking) {
            this.parking = parking;
        }

        public int getNumberOfParkingSpots() {
            return numberOfParkingSpots;
        }

        public void setNumberOfParkingSpots(int numberOfParkingSpots) {
            this.numberOfParkingSpots = numberOfParkingSpots;
        }

        public boolean isTypeOfParkingSpot() {
            return typeOfParkingSpot;
        }

        public void setTypeOfParkingSpot(boolean typeOfParkingSpot) {
            this.typeOfParkingSpot = typeOfParkingSpot;
        }

        public int getReservationId() {
            return reservationId;
        }

        public void setReservationId(int reservationId) {
            this.reservationId = reservationId;
        }
    }
