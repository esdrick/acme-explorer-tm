package com.example.acme_explorer.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acme_explorer.network.ApiClient;
import com.example.acme_explorer.network.GooglePlacesApiService;
import com.example.acme_explorer.data.PlaceResponse;
import com.example.acme_explorer.adapter.PlacesAdapter;
import com.example.acme_explorer.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class PlacesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlacesAdapter placesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        // Configurar el Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar); // Usar el Toolbar de tu layout
        setSupportActionBar(toolbar); // Configura el toolbar como ActionBar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Activar el botón de retroceso
        getSupportActionBar().setHomeButtonEnabled(true);

        // Configurar el título del Toolbar
        getSupportActionBar().setTitle("Lugares de interes");


        double latitude = getIntent().getDoubleExtra("latDestino", 0.0);
        double longitude = getIntent().getDoubleExtra("lonDestino", 0.0);

        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Coordenadas no disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        String location = latitude + "," + longitude;
        int radius = 1000;
        String type = "restaurant";
        String apiKey = getString(R.string.google_maps_key); // ✅ SÍ funciona

        recyclerView = findViewById(R.id.recyclerView); //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiClient.getRetrofitInstance().create(GooglePlacesApiService.class)
                .getNearbyPlaces(location, radius, type, apiKey)
                .enqueue(new Callback<PlaceResponse>() {
                    @Override
                    public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                        Log.d("PLACES_API", "Respuesta recibida");

                        if (response.isSuccessful()) {
                            PlaceResponse placeResponse = response.body();

                            if (placeResponse != null && placeResponse.getPlaces() != null) {
                                List<PlaceResponse.Place> placesList = placeResponse.getPlaces();
                                Log.d("PLACES_API", "Cantidad de lugares: " + placesList.size());

                                if (placesList.isEmpty()) {
                                    Toast.makeText(PlacesActivity.this, "No se encontraron lugares", Toast.LENGTH_SHORT).show();
                                }

                                placesAdapter = new PlacesAdapter(placesList, latitude, longitude, apiKey);
                                recyclerView.setAdapter(placesAdapter);
                            } else {
                                Log.d("PLACES_API", "Respuesta vacía o nula");
                                Toast.makeText(PlacesActivity.this, "No se encontraron lugares cercanos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("PLACES_API", "Error en la respuesta: " + response.message());
                            Toast.makeText(PlacesActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceResponse> call, Throwable t) {
                        Log.e("PLACES_API", "Error de conexión", t);
                        Toast.makeText(PlacesActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private String getMetaDataApiKey() {
        try {
            ApplicationInfo ai = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String apiKey = bundle.getString("com.google.android.geo.API_KEY");
            Log.d("API_KEY", "La API KEY es: " + apiKey); // 🔍 LOG
            return apiKey;
        } catch (Exception e) {
            Log.e("API_KEY", "No se pudo obtener la API Key del manifest", e);
            return "";
        }
    }

        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed(); // Esto hace que vuelva atrás
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
