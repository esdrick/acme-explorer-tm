package com.example.acme_explorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView textViewTitulo, textViewCiudad, textViewCodigo, textViewDescripcion, textViewPrecio, textViewFechas;
    ImageView imageView;
    ImageButton buttonSeleccionar;
    boolean seleccionado = false;
    String codigo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del viaje");
        }

        textViewTitulo = findViewById(R.id.textViewTituloDetalle);
        textViewCiudad = findViewById(R.id.textViewCiudadDetalle);
        textViewCodigo = findViewById(R.id.textViewCodigoDetalle);
        textViewDescripcion = findViewById(R.id.textViewDescripcionDetalle);
        textViewPrecio = findViewById(R.id.textViewPrecioDetalle);
        imageView = findViewById(R.id.imageViewDetalle);
        textViewFechas = findViewById(R.id.textViewFecha);
        buttonSeleccionar = findViewById(R.id.buttonSeleccionar);

        String titulo = getIntent().getStringExtra("titulo");
        String ciudad = getIntent().getStringExtra("ciudad");
        codigo = getIntent().getStringExtra("codigo");
        String descripcion = getIntent().getStringExtra("descripcion");
        int precio = getIntent().getIntExtra("precio", 0);
        String imagenUrl = getIntent().getStringExtra("imagenUrl");
        String fechaInicio = getIntent().getStringExtra("fechaInicio");
        String fechaFin = getIntent().getStringExtra("fechaFin");
        String lugarSalida = getIntent().getStringExtra("lugarSalida");
        seleccionado = getIntent().getBooleanExtra("seleccionado", false);

        textViewTitulo.setText(titulo);
        textViewCiudad.setText("Destino: " + ciudad + "\nDesde: " + lugarSalida);
        textViewCodigo.setText("Código: " + codigo);
        textViewDescripcion.setText("Descripción: " + descripcion);
        textViewPrecio.setText("Precio: " + precio + " €");
        textViewFechas.setText("Del " + fechaInicio + " al " + fechaFin);

        Picasso.get().load(imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView);

        actualizarEstrella();

        buttonSeleccionar.setOnClickListener(v -> {
            seleccionado = !seleccionado;
            actualizarEstrella();
        });
    }

    private void actualizarEstrella() {
        buttonSeleccionar.setImageResource(
                seleccionado ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );
    }

    @Override
    public void onBackPressed() {
        // Devolver el resultado
        Intent resultIntent = new Intent();
        resultIntent.putExtra("codigo", codigo);
        resultIntent.putExtra("seleccionado", seleccionado);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}