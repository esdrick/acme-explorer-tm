package com.example.acme_explorer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.Collections;
import java.util.Comparator;
import android.widget.ImageButton;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.acme_explorer.R;
import com.example.acme_explorer.adapter.TripAdapter;
import com.example.acme_explorer.data.Trip;
import com.example.acme_explorer.data.TripData;

import java.util.ArrayList;
import java.util.List;

public class ListadoActivity extends AppCompatActivity {

    RecyclerView recyclerViajes;
    private List<Trip> listaViajesOriginal;

    private TripAdapter tripAdapter;

    private boolean vistaEnCuadricula = false;
    private RecyclerView.LayoutManager layoutManager;

    private final ActivityResultLauncher<Intent> filtroLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String precioMinStr = data.getStringExtra("precioMin");
                        String precioMaxStr = data.getStringExtra("precioMax");
                        String fechaInicio = data.getStringExtra("fechaInicio");
                        String fechaFin = data.getStringExtra("fechaFin");

                        int ordenPrecio = data.getIntExtra("ordenPrecio", 0);
                        aplicarFiltros(precioMinStr, precioMaxStr, fechaInicio, fechaFin, ordenPrecio);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> detalleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String codigo = result.getData().getStringExtra("codigo");
                    boolean seleccionado = result.getData().getBooleanExtra("seleccionado", false);

                    for (Trip trip : TripData.getViajes()) {
                        if (trip.getCodigo().equals(codigo)) {
                            trip.setSeleccionado(seleccionado);
                            break;
                        }
                    }

                    tripAdapter.notifyDataSetChanged(); // refresca la vista
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        TripData.generarViajesAleatorios(10); // ✅ Gene

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Viajes Disponibles");
        }

        recyclerViajes = findViewById(R.id.recyclerViajes);
        layoutManager = new LinearLayoutManager(this); // vista lista
        recyclerViajes.setLayoutManager(layoutManager);

        ImageButton buttonFiltro = findViewById(R.id.buttonFiltro); // engranaje
        ImageButton buttonBuscarTexto = findViewById(R.id.buttonBuscarTexto); // lupa de búsqueda
        EditText editTextBuscar = findViewById(R.id.editTextBuscar);
        ImageButton buttonColumna = findViewById(R.id.buttonColumna);

        editTextBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tripAdapter.filtrar(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

        tripAdapter = new TripAdapter(TripData.getViajes());
        recyclerViajes.setAdapter(tripAdapter);

        // Botón que abre el filtro (FilterActivity)
        buttonFiltro.setOnClickListener(v -> {
            Intent intent = new Intent(ListadoActivity.this, FilterActivity.class);
            filtroLauncher.launch(intent);
        });


// Botón de búsqueda (icono lupa)
        buttonBuscarTexto.setOnClickListener(v -> {
            String texto = editTextBuscar.getText().toString().trim();
            tripAdapter.filtrar(texto);
        });

// ENTER en el teclado también filtra
        editTextBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String texto = editTextBuscar.getText().toString().trim();
                tripAdapter.filtrar(texto);
                return true;
            }
            return false;
        });

        buttonColumna.setOnClickListener(v -> {
            vistaEnCuadricula = !vistaEnCuadricula;

            if (vistaEnCuadricula) {
                layoutManager = new androidx.recyclerview.widget.GridLayoutManager(this, 2); // 2 columnas
                buttonColumna.setImageResource(android.R.drawable.ic_menu_sort_by_size); // cambia ícono si querés
            } else {
                layoutManager = new LinearLayoutManager(this);
                buttonColumna.setImageResource(android.R.drawable.ic_dialog_dialer); // ícono original
            }

            recyclerViajes.setLayoutManager(layoutManager);
            recyclerViajes.setAdapter(tripAdapter); // opcional si no se refresca bien
        });

    }


    private void aplicarFiltros(String precioMinStr, String precioMaxStr, String fechaInicio, String fechaFin, int ordenPrecio) {
        List<Trip> filtrados = new ArrayList<>();

        for (Trip trip : TripData.getViajes()) {
            boolean cumple = true;

            if (!precioMinStr.isEmpty()) {
                int min = Integer.parseInt(precioMinStr);
                if (trip.getPrecio() < min) cumple = false;
            }

            if (!precioMaxStr.isEmpty()) {
                int max = Integer.parseInt(precioMaxStr);
                if (trip.getPrecio() > max) cumple = false;
            }

            if (fechaInicio != null && !fechaInicio.isEmpty()) {
                if (trip.getFechaInicio().compareTo(fechaInicio) < 0) cumple = false;
            }

            if (fechaFin != null && !fechaFin.isEmpty()) {
                if (trip.getFechaFin().compareTo(fechaFin) > 0) cumple = false;
            }

            if (cumple) {
                filtrados.add(trip);
            }
        }

        if (ordenPrecio == 1) {
            Collections.sort(filtrados, Comparator.comparingInt(Trip::getPrecio));
        } else if (ordenPrecio == 2) {
            Collections.sort(filtrados, (a, b) -> Integer.compare(b.getPrecio(), a.getPrecio()));
        }

        recyclerViajes.setAdapter(new TripAdapter(filtrados));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // retrocede
        return true;
    }


    public void lanzarDetalle(Intent intent) {
        detalleLauncher.launch(intent);
    }
}