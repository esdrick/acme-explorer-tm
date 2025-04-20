package com.example.acme_explorer.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;

import java.util.Calendar;

public class DateActivity extends AppCompatActivity {

    private TextView tvHora, tvFecha;

    private Calendar calendar = Calendar.getInstance();
    private int yy = calendar.get(Calendar.YEAR);
    private int mm = calendar.get(Calendar.MONTH);
    private int dd = calendar.get(Calendar.DAY_OF_MONTH);
    private int hh = calendar.get(Calendar.HOUR_OF_DAY);
    private int mi = calendar.get(Calendar.MINUTE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        // ✅ Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ← flechita
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Selecciona Fecha");
        }

        tvHora = findViewById(R.id.textViewHora);
        tvFecha = findViewById(R.id.textViewFecha);
    }

    public void setDia(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker datePicker, int year, int month, int day) -> {
                    tvFecha.setText(day + "/" + (month + 1) + "/" + year);
                    dd = day; mm = month; yy = year;
                }, yy, mm, dd);
        datePickerDialog.show();
    }

    public void setHora(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker timePicker, int hour, int minute) -> {
                    String formattedMinute = (minute < 10) ? "0" + minute : String.valueOf(minute);
                    tvHora.setText(hour + ":" + formattedMinute);
                    hh = hour; mi = minute;
                }, hh, mi, true);
        timePickerDialog.show();
    }

    public void lanzarAdapters(View view) {
        Intent intent = new Intent(DateActivity.this, ListadoActivity.class);
        // Si quieres pasar fecha/hora por Intent, puedes hacerlo así:
        // intent.putExtra("fecha", tvFecha.getText().toString());
        // intent.putExtra("hora", tvHora.getText().toString());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}