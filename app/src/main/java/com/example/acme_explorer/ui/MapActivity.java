package com.example.acme_explorer.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.acme_explorer.R;

import android.Manifest;
import android.content.pm.PackageManager;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Acme Explorer");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Solicitar permisos si no los tiene
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        // Recibe las coordenadas de inicio y destino
        double latInicio = getIntent().getDoubleExtra("latInicio", 0.0);
        double lonInicio = getIntent().getDoubleExtra("lonInicio", 0.0);
        double latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
        double lonDestino = getIntent().getDoubleExtra("lonDestino", 0.0);

        // Verificar si las coordenadas no son cero antes de mostrar la ruta
        if (latInicio != 0.0 && lonInicio != 0.0 && latDestino != 0.0 && lonDestino != 0.0) {
            mostrarRuta(latInicio, lonInicio, latDestino, lonDestino);
        }

        // Actualizar la latitud y longitud del TextView
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng target = mMap.getCameraPosition().target;
                double lat = target.latitude;
                double lon = target.longitude;
                TextView latLongTextView = findViewById(R.id.latLongTextView);
                latLongTextView.setText("Latitud: " + lat + ", Longitud: " + lon);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Accionar el retroceso
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarRuta(double latInicio, double lonInicio, double latDestino, double lonDestino) {
        LatLng inicio = new LatLng(latInicio, lonInicio);
        LatLng destino = new LatLng(latDestino, lonDestino);

        // Agregar los marcadores de inicio y destino
        mMap.addMarker(new MarkerOptions().position(inicio).title("Inicio"));
        mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));

        // Dibuja una línea entre los dos puntos
        mMap.addPolyline(new PolylineOptions().add(inicio, destino).width(5).color(Color.RED));

        // Centrar la cámara entre los dos puntos
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(inicio);
        builder.include(destino);
        LatLngBounds bounds = builder.build();
        int padding = 50; // Espacio alrededor de la línea
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cameraUpdate);  // Esto mueve la cámara entre los dos puntos
    }
}