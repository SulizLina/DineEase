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

public class registration extends AppCompatActivity {
    EditText email,password,name,age ;
    Button signup,login,back;
    DataBaseHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = findViewById(R.id.Fname);
        password = findViewById(R.id.Password1);
        email = findViewById(R.id.Email1);
        age = findViewById(R.id.Age);
        signup = findViewById(R.id.Regbutton);
        login = findViewById(R.id.login11);
        back=findViewById(R.id.back);
        DB = new DataBaseHelper(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ema = email.getText().toString();
                String user = name.getText().toString();
                String pass = password.getText().toString();
                String ag=age.getText().toString();

                if (user.equals("") || pass.equals("")||ema.equals("")||ag.equals(""))
                    Toast.makeText(registration.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else {
                    Boolean checkUserEmail = DB.checkUserEmail(ema);
                    if (!checkUserEmail) {
                        Boolean insert = DB.insertData(user,ag ,ema, pass);
                        if (insert) {
                            Toast.makeText(registration.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", ema);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), FragmentLoaderActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(registration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(registration.this, "Already exists! please sign in", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
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