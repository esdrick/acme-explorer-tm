package com.example.acme_explorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acme_explorer.data.Actor;
import com.example.acme_explorer.auth.ActorService;
import com.example.acme_explorer.data.FirebaseDatabaseService;
import com.example.acme_explorer.R;
import com.example.acme_explorer.adapter.TripAdapter;
import com.example.acme_explorer.data.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListadoActivity extends AppCompatActivity {

    private RecyclerView recyclerViajes;
    private TripAdapter tripAdapter;
    private List<Trip> listaViajesOriginal;
    private RecyclerView.LayoutManager layoutManager;
    private boolean vistaEnCuadricula = false;
    private boolean esAdmin = false;

    private final ActivityResultLauncher<Intent> filtroLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        aplicarFiltros(
                                data.getStringExtra("precioMin"),
                                data.getStringExtra("precioMax"),
                                data.getStringExtra("fechaInicio"),
                                data.getStringExtra("fechaFin"),
                                data.getIntExtra("ordenPrecio", 0)
                        );
                    }
                }
            });

    private final ActivityResultLauncher<Intent> detalleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String codigo = result.getData().getStringExtra("codigo");
                    boolean seleccionado = result.getData().getBooleanExtra("seleccionado", false);

                    for (Trip trip : listaViajesOriginal) {
                        if (trip.getCodigo().equals(codigo)) {
                            trip.setSeleccionado(seleccionado);
                            break;
                        }
                    }
                    tripAdapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Viajes Disponibles");
        }


        recyclerViajes = findViewById(R.id.recyclerViajes);
        layoutManager = new LinearLayoutManager(this);
        recyclerViajes.setLayoutManager(layoutManager);

        ImageButton buttonFiltro = findViewById(R.id.buttonFiltro);
        ImageButton buttonBuscarTexto = findViewById(R.id.buttonBuscarTexto);
        EditText editTextBuscar = findViewById(R.id.editTextBuscar);
        ImageButton buttonColumna = findViewById(R.id.buttonColumna);


        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tripAdapter != null) tripAdapter.filtrar(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        editTextBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                tripAdapter.filtrar(editTextBuscar.getText().toString().trim());
                return true;
            }
            return false;
        });

        buttonFiltro.setOnClickListener(v -> {
            Intent intent = new Intent(ListadoActivity.this, FilterActivity.class);
            filtroLauncher.launch(intent);
        });

        buttonBuscarTexto.setOnClickListener(v -> {
            String texto = editTextBuscar.getText().toString().trim();
            tripAdapter.filtrar(texto);
        });

        buttonColumna.setOnClickListener(v -> {
            vistaEnCuadricula = !vistaEnCuadricula;
            layoutManager = vistaEnCuadricula
                    ? new GridLayoutManager(this, 2)
                    : new LinearLayoutManager(this);
            recyclerViajes.setLayoutManager(layoutManager);
            recyclerViajes.setAdapter(tripAdapter);
        });

        cargarViajesDesdeFirebase();
    }

    private void cargarViajesDesdeFirebase() {
        FirebaseDatabaseService.getInstance().getAllTrips()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Trip> trips = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Trip trip = data.getValue(Trip.class);
                            if (trip != null) {
                                trip.setCodigo(data.getKey());
                                trip.setSeleccionado(false);
                                trips.add(trip);
                            }
                        }
                        cargarSeleccionadosYRol(trips);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ListadoActivity.this, "Error al cargar viajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarSeleccionadosYRol(List<Trip> trips) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userTripsRef = FirebaseDatabase.getInstance().getReference("selectedTrips").child(uid);

        userTripsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshotSel) {
                for (DataSnapshot dataSel : snapshotSel.getChildren()) {
                    String selKey = dataSel.getKey();
                    for (Trip trip : trips) {
                        if (trip.getCodigo().equals(selKey)) {
                            trip.setSeleccionado(true);
                            break;
                        }
                    }
                }

                ActorService.getCurrentActorRef().get().addOnSuccessListener(actorSnapshot -> {
                    boolean esAdmin = false;
                    if (actorSnapshot.exists()) {
                        Actor actor = actorSnapshot.toObject(Actor.class);
                        esAdmin = actor != null && "ADMINISTRATOR".equals(actor.getRole());
                    }

                    if (esAdmin) {
                        ImageButton fab = findViewById(R.id.fabCrearViaje);
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(v -> {
                            Intent intent = new Intent(ListadoActivity.this, NewTripActivity.class);
                            startActivity(intent);
                        });
                    }

                    listaViajesOriginal = trips;
                    tripAdapter = new TripAdapter(trips, esAdmin);
                    recyclerViajes.setAdapter(tripAdapter);
                }).addOnFailureListener(e -> {
                    Toast.makeText(ListadoActivity.this, "Error al verificar rol del usuario", Toast.LENGTH_SHORT).show();
                    tripAdapter = new TripAdapter(trips, false);
                    recyclerViajes.setAdapter(tripAdapter);
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListadoActivity.this, "Error al cargar seleccionados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltros(String precioMinStr, String precioMaxStr, String fechaInicio, String fechaFin, int ordenPrecio) {
        List<Trip> filtrados = new ArrayList<>();
        for (Trip trip : listaViajesOriginal) {
            boolean cumple = true;
            if (!precioMinStr.isEmpty() && trip.getPrecio() < Integer.parseInt(precioMinStr)) cumple = false;
            if (!precioMaxStr.isEmpty() && trip.getPrecio() > Integer.parseInt(precioMaxStr)) cumple = false;
            if (fechaInicio != null && !fechaInicio.isEmpty() && trip.getFechaInicio().compareTo(fechaInicio) < 0) cumple = false;
            if (fechaFin != null && !fechaFin.isEmpty() && trip.getFechaFin().compareTo(fechaFin) > 0) cumple = false;
            if (cumple) filtrados.add(trip);
        }

        if (ordenPrecio == 1) Collections.sort(filtrados, Comparator.comparingInt(Trip::getPrecio));
        else if (ordenPrecio == 2) Collections.sort(filtrados, (a, b) -> Integer.compare(b.getPrecio(), a.getPrecio()));

        recyclerViajes.setAdapter(new TripAdapter(filtrados));
    }

    @Override public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void lanzarDetalle(Intent intent) {
        startActivity(intent);
    }
}