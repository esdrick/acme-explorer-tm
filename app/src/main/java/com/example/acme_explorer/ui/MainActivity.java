package com.example.acme_explorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.acme_explorer.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    LinearLayout opcionDisponibles, opcionSeleccionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView opcionDisponibles = findViewById(R.id.opcionDisponibles);
        CardView opcionSeleccionados = findViewById(R.id.opcionSeleccionados);

        opcionDisponibles.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListadoActivity.class);
            startActivity(intent);
        });

        opcionSeleccionados.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SeleccionadosActivity.class);
            startActivity(intent);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Acme Explorer");
        }

        CardView opcionPerfil = findViewById(R.id.opcionPerfil);
        opcionPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // 🔐 cerrar sesión
            startActivity(new Intent(this, LoginActivity.class)); // ⬅️ vuelve al login
            finish(); // cierra la actividad actual
        });

    }

}