package com.example.controlacc;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.controlacc.model.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.escenario2,new SessionFragment()).commit();
    }
}
