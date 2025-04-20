package com.example.acme_explorer.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.example.acme_explorer.adapter.TripAdapter;
import com.example.acme_explorer.data.Trip;
import com.example.acme_explorer.data.TripData;

import java.util.ArrayList;
import java.util.List;

public class SeleccionadosActivity extends AppCompatActivity {

    RecyclerView recyclerSeleccionados;

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

        List<Trip> todosLosViajes = TripData.getViajes();
        List<Trip> seleccionados = new ArrayList<>();

        for (Trip trip : todosLosViajes) {
            if (trip.isSeleccionado()) {
                seleccionados.add(trip);
            }
        }

        TripAdapter adapter = new TripAdapter(seleccionados);
        recyclerSeleccionados.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}