package com.example.dineease_sprint1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class login extends AppCompatActivity {
    EditText Email1, Password1;
    Button Login,signup,back;
    DataBaseHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Email1 = findViewById(R.id.Email1);
        Password1 = findViewById(R.id.Password1);
        Login = findViewById(R.id.Loginbutton);
        signup=findViewById(R.id.reg);
        back=findViewById(R.id.back2);
        DB = new DataBaseHelper(this);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = Email1.getText().toString();
                String pass = Password1.getText().toString();
                if (em.equals("") || pass.equals(""))
                    Toast.makeText(login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    int userId = DB.checkUsernamePassword(em, pass);
                    if (userId>0) {
                        Toast.makeText(login.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", em);
                        editor.putInt("userId", userId);
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), FragmentLoaderActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), registration.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}