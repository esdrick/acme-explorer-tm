package com.example.acme_explorer.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.example.acme_explorer.adapter.TripAdapter;
import com.example.acme_explorer.data.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SeleccionadosActivity extends AppCompatActivity {

    private RecyclerView recyclerSeleccionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionados);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Viajes seleccionados");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerSeleccionados = findViewById(R.id.recyclerSeleccionados);
        recyclerSeleccionados.setLayoutManager(new LinearLayoutManager(this));

        cargarSeleccionadosDesdeFirebase();
    }

    private void cargarSeleccionadosDesdeFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refSeleccionados = FirebaseDatabase.getInstance()
                .getReference("selectedTrips")
                .child(uid);

        refSeleccionados.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> codigosSeleccionados = new ArrayList<>();
                for (DataSnapshot sel : snapshot.getChildren()) {
                    codigosSeleccionados.add(sel.getKey());
                }

                // Ahora buscamos los viajes reales
                cargarViajesFiltrados(codigosSeleccionados);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeleccionadosActivity.this, "Error al cargar IDs seleccionados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarViajesFiltrados(List<String> codigosSeleccionados) {
        FirebaseDatabase.getInstance()
                .getReference("trips") // Asegúrate que esta sea la ruta donde están todos los viajes
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Trip> seleccionados = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Trip trip = data.getValue(Trip.class);
                            String key = data.getKey();
                            if (trip != null && codigosSeleccionados.contains(key)) {
                                trip.setCodigo(key);
                                trip.setSeleccionado(true);
                                seleccionados.add(trip);
                            }
                        }

                        recyclerSeleccionados.setAdapter(new TripAdapter(seleccionados));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SeleccionadosActivity.this, "Error al cargar viajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}