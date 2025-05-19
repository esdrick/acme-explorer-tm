package com.example.acme_explorer.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.example.acme_explorer.data.Trip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EditTripActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextCiudad, editTextPrecio, editTextFechaInicio, editTextFechaFin, editTextLugarSalida;
    private EditText editTextDescripcion, editTextImagenUrl;
    private Button buttonGuardar;
    private Trip trip;
    private String fechaInicio, fechaFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Editar viaje");

        // Obtener el viaje recibido
        trip = (Trip) getIntent().getSerializableExtra("trip");

        // Inicializar campos
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextCiudad = findViewById(R.id.editTextCiudad);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        editTextFechaInicio = findViewById(R.id.editTextFechaInicio);
        editTextFechaFin = findViewById(R.id.editTextFechaFin);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextImagenUrl = findViewById(R.id.editTextImagenUrl);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        editTextLugarSalida = findViewById(R.id.editTextLugarSalida);

        // Precargar datos
        if (trip != null) {
            editTextTitulo.setText(trip.getTitulo());
            editTextCiudad.setText(trip.getCiudad());
            editTextPrecio.setText(String.valueOf(trip.getPrecio()));
            editTextFechaInicio.setText(trip.getFechaInicio());
            editTextFechaFin.setText(trip.getFechaFin());
            editTextDescripcion.setText(trip.getDescripcion());
            editTextImagenUrl.setText(trip.getImagenUrl());
            fechaInicio = trip.getFechaInicio();
            fechaFin = trip.getFechaFin();
            editTextLugarSalida.setText(trip.getLugarSalida());
        }

        // Selección de fechas
        editTextFechaInicio.setOnClickListener(v -> showDatePicker(true));
        editTextFechaFin.setOnClickListener(v -> showDatePicker(false));

        // Guardar cambios
        buttonGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void showDatePicker(boolean esInicio) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fechaFormateada = String.format("%04d-%02d-%02d", year, month + 1, day);
                    if (esInicio) {
                        fechaInicio = fechaFormateada;
                        editTextFechaInicio.setText("Inicio: " + fechaFormateada);
                    } else {
                        fechaFin = fechaFormateada;
                        editTextFechaFin.setText("Fin: " + fechaFormateada);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void guardarCambios() {
        // Obtener campos
        String titulo = editTextTitulo.getText().toString();
        String ciudad = editTextCiudad.getText().toString();
        String precioStr = editTextPrecio.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();
        String imagenUrl = editTextImagenUrl.getText().toString();
        String lugarSalida = editTextLugarSalida.getText().toString();

        // Validación
        if (titulo.isEmpty() || ciudad.isEmpty() || precioStr.isEmpty()
                || descripcion.isEmpty() || imagenUrl.isEmpty() || (lugarSalida.isEmpty())
                || fechaInicio == null || fechaFin == null) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.WEB_URL.matcher(imagenUrl).matches()) {
            editTextImagenUrl.setError("La URL de la imagen no es válida");
            return;
        }

        // Parsear precio
        int precioInt;
        try {
            precioInt = Integer.parseInt(precioStr);
        } catch (NumberFormatException e) {
            editTextPrecio.setError("Precio inválido");
            return;
        }

        // Actualizar objeto trip
        trip.setTitulo(titulo);
        trip.setCiudad(ciudad);
        trip.setPrecio(precioInt);
        trip.setFechaInicio(fechaInicio);
        trip.setFechaFin(fechaFin);
        trip.setDescripcion(descripcion);
        trip.setImagenUrl(imagenUrl);
        trip.setLugarSalida(lugarSalida);

        // Guardar en Firebase
        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("trips").child(trip.getCodigo());
        tripRef.setValue(trip).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Viaje actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el viaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Botón de retroceso
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}