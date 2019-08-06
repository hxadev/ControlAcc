package com.example.controlacc;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.controlacc.model.User;

/**
 * Clase la cual muestra el login de usuario y contraseña del padre de familia en el sistema
 * @author Alfonso Hernandez Xochipa
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.escenario2,new SessionFragment()).commit();
    }
}
