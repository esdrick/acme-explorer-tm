package com.example.acme_explorer.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    private Spinner spinnerPrecioMin, spinnerPrecioMax, spinnerOrdenPrecio;
    private TextView textFechaInicio, textFechaFin;
    private ImageButton buttonIconFechaInicio, buttonIconFechaFin;
    private Button buttonAplicar;
    private String fechaInicio = null, fechaFin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Filtrar viajes");
        }

        spinnerPrecioMin = findViewById(R.id.spinnerPrecioMin);
        spinnerPrecioMax = findViewById(R.id.spinnerPrecioMax);
        spinnerOrdenPrecio = findViewById(R.id.spinnerOrdenPrecio);

        textFechaInicio = findViewById(R.id.textFechaInicio);
        textFechaFin = findViewById(R.id.textFechaFin);
        buttonIconFechaInicio = findViewById(R.id.buttonIconFechaInicio);
        buttonIconFechaFin = findViewById(R.id.buttonIconFechaFin);
        buttonAplicar = findViewById(R.id.buttonAplicarFiltros);

        // Spinner de precio mínimo
        ArrayAdapter<CharSequence> adapterPrecioMin = ArrayAdapter.createFromResource(
                this,
                R.array.valores_precio_min,
                android.R.layout.simple_spinner_item
        );
        adapterPrecioMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrecioMin.setAdapter(adapterPrecioMin);

        // Spinner de precio máximo
        ArrayAdapter<CharSequence> adapterPrecioMax = ArrayAdapter.createFromResource(
                this,
                R.array.valores_precio_max,
                android.R.layout.simple_spinner_item
        );
        adapterPrecioMax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrecioMax.setAdapter(adapterPrecioMax);

        // Spinner de orden
        ArrayAdapter<CharSequence> adapterOrden = ArrayAdapter.createFromResource(
                this,
                R.array.opciones_orden_precio,
                android.R.layout.simple_spinner_item
        );
        adapterOrden.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrdenPrecio.setAdapter(adapterOrden);

        // Pickers de fecha
        buttonIconFechaInicio.setOnClickListener(v -> showDatePicker(true));
        buttonIconFechaFin.setOnClickListener(v -> showDatePicker(false));

        // Aplicar filtros
        buttonAplicar.setOnClickListener(v -> {
            String precioMinStr = spinnerPrecioMin.getSelectedItem().toString();
            String precioMaxStr = spinnerPrecioMax.getSelectedItem().toString();

            // ✅ Convertir "Sin mínimo" y "Sin máximo" en campos vacíos
            if (precioMinStr.equals("Sin mínimo")) precioMinStr = "";
            if (precioMaxStr.equals("Sin máximo")) precioMaxStr = "";

            Intent resultIntent = new Intent();
            resultIntent.putExtra("precioMin", precioMinStr);
            resultIntent.putExtra("precioMax", precioMaxStr);
            resultIntent.putExtra("fechaInicio", fechaInicio);
            resultIntent.putExtra("fechaFin", fechaFin);
            resultIntent.putExtra("ordenPrecio", spinnerOrdenPrecio.getSelectedItemPosition());

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void showDatePicker(boolean esInicio) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String fechaFormateada = String.format("%04d-%02d-%02d", year, month + 1, day);
                    if (esInicio) {
                        fechaInicio = fechaFormateada;
                        textFechaInicio.setText("Inicio: " + fechaFormateada);
                    } else {
                        fechaFin = fechaFormateada;
                        textFechaFin.setText("Fin: " + fechaFormateada);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}