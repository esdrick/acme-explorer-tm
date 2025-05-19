package com.example.acme_explorer.ui;

import android.app.DatePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.acme_explorer.R;
import com.example.acme_explorer.data.Trip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class NewTripActivity extends AppCompatActivity {

    private EditText editTitulo, editCiudad, editPrecio, editDescripcion, editImagenUrl;
    private EditText editFechaInicio, editFechaFin, editLugarSalida;
    private Button buttonCrear;
    private String fechaInicio = "";
    private String fechaFin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nuevo Viaje");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitulo = findViewById(R.id.editTextTitulo);
        editCiudad = findViewById(R.id.editTextCiudad);
        editPrecio = findViewById(R.id.editTextPrecio);
        editDescripcion = findViewById(R.id.editTextDescripcion);
        editImagenUrl = findViewById(R.id.editTextImagenUrl);
        editLugarSalida = findViewById(R.id.editTextLugarSalida);
        editFechaInicio = findViewById(R.id.editTextFechaInicio);
        editFechaFin = findViewById(R.id.editTextFechaFin);
        buttonCrear = findViewById(R.id.buttonGuardarViaje);

        editFechaInicio.setOnClickListener(v -> showDatePicker(true));
        editFechaFin.setOnClickListener(v -> showDatePicker(false));

        buttonCrear.setOnClickListener(v -> guardarViaje());
    }

    private void guardarViaje() {
        if (!validarCampos()) return;

        String titulo = editTitulo.getText().toString();
        String ciudad = editCiudad.getText().toString();
        String descripcion = editDescripcion.getText().toString();
        String imagenUrl = editImagenUrl.getText().toString();
        int precio = Integer.parseInt(editPrecio.getText().toString());

        String lugarSalida = editLugarSalida.getText().toString();
        String ciudadDestino = editCiudad.getText().toString();

        double[] coordInicio = obtenerCoordenadasDesdeDireccion(lugarSalida);
        double[] coordDestino = obtenerCoordenadasDesdeDireccion(ciudadDestino);

        if (coordInicio == null || coordDestino == null) {
            Toast.makeText(this, "No se pudieron obtener las coordenadas", Toast.LENGTH_SHORT).show();
            return;
        }

        String codigo = UUID.randomUUID().toString();

        Trip trip = new Trip(titulo, ciudad, codigo, lugarSalida, imagenUrl, descripcion, precio, fechaInicio, fechaFin);
        trip.setLatInicio(coordInicio[0]);
        trip.setLonInicio(coordInicio[1]);
        trip.setLatDestino(coordDestino[0]);
        trip.setLonDestino(coordDestino[1]);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("trips").child(codigo);
        ref.setValue(trip).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Viaje creado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos() {
        if (editTitulo.getText().toString().trim().isEmpty() ||
                editCiudad.getText().toString().trim().isEmpty() ||
                editPrecio.getText().toString().trim().isEmpty() ||
                editDescripcion.getText().toString().trim().isEmpty() ||
                editImagenUrl.getText().toString().trim().isEmpty() ||
                editLugarSalida.getText().toString().trim().isEmpty() ||
                fechaInicio.isEmpty() ||
                fechaFin.isEmpty()) {

            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDatePicker(boolean esInicio) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fecha = String.format("%04d-%02d-%02d", year, month + 1, day);
                    if (esInicio) {
                        fechaInicio = fecha;
                        editFechaInicio.setText(fecha);
                    } else {
                        fechaFin = fecha;
                        editFechaFin.setText(fecha);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private double[] obtenerCoordenadasDesdeDireccion(String direccion) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address location = direcciones.get(0);
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}