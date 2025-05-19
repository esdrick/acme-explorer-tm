package com.example.acme_explorer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acme_explorer.data.Trip;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView textViewTitulo, textViewCiudad, textViewCodigo, textViewDescripcion, textViewPrecio, textViewFechas;
    ImageView imageView;
    ImageButton buttonSeleccionar;
    Button buttonVerMapa;

    boolean seleccionado = false;
    String codigo = "";

    private Trip trip;  // Declarar la variable trip

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Configuración de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del viaje");
        }

        // Inicialización de vistas
        textViewTitulo = findViewById(R.id.textViewTituloDetalle);
        textViewCiudad = findViewById(R.id.textViewCiudadDetalle);
        textViewCodigo = findViewById(R.id.textViewCodigoDetalle);
        textViewDescripcion = findViewById(R.id.textViewDescripcionDetalle);
        textViewPrecio = findViewById(R.id.textViewPrecioDetalle);
        imageView = findViewById(R.id.imageViewDetalle);
        textViewFechas = findViewById(R.id.textViewFecha);
        buttonSeleccionar = findViewById(R.id.buttonSeleccionar);
        buttonVerMapa = findViewById(R.id.buttonVerMapa);
        Button buttonOpenPlaces = findViewById(R.id.buttonOpenPlaces);

        // Recibir el objeto Trip desde ListadoActivity a través de la Intent
        trip = (Trip) getIntent().getSerializableExtra("trip");

        // Asegurarse de que el objeto trip no sea nulo antes de acceder a sus valores
        if (trip != null) {
            // Mostrar la información del viaje
            textViewTitulo.setText(trip.getTitulo());
            textViewCiudad.setText("Destino: " + trip.getCiudad() + "\nDesde: " + trip.getLugarSalida());
            textViewCodigo.setText("Código: " + trip.getCodigo());
            textViewDescripcion.setText("Descripción: " + trip.getDescripcion());
            textViewPrecio.setText("Precio: " + trip.getPrecio() + " €");
            textViewFechas.setText("Del " + trip.getFechaInicio() + " al " + trip.getFechaFin());

            // Cargar la imagen del viaje
            Picasso.get().load(trip.getImagenUrl()).into(imageView);
        }

        // Actualizar el estado del botón de favorito
        actualizarEstrella();

        buttonSeleccionar.setOnClickListener(v -> {
            seleccionado = !seleccionado;
            actualizarEstrella();

            // 🔁 Guardar o quitar selección en Firebase
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String codigo = trip.getCodigo(); // ← esto lo tienes que obtener del trip recibido
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("selectedTrips").child(uid).child(codigo);

            if (seleccionado) {
                ref.setValue(true);
            } else {
                ref.removeValue();
            }
        });

        // Cuando se hace clic en el botón "Ver Mapa", pasamos las coordenadas a MapActivity
        buttonVerMapa.setOnClickListener(v -> {
            Intent intentMapa = new Intent(DetailActivity.this, MapActivity.class);
            intentMapa.putExtra("latInicio", trip.getLatInicio());
            intentMapa.putExtra("lonInicio", trip.getLonInicio());
            intentMapa.putExtra("latDestino", trip.getLatDestino());
            intentMapa.putExtra("lonDestino", trip.getLonDestino());
            startActivity(intentMapa); // Iniciar MapActivity
        });

        // Encuentra el botón por su ID

        // Establece el evento onClick para abrir PlacesActivity
        buttonOpenPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea la intención para abrir PlacesActivity
                Intent intent = new Intent(DetailActivity.this, PlacesActivity.class);
                // Pasar coordenadas a PlacesActivity
                intent.putExtra("latDestino", trip.getLatDestino());
                intent.putExtra("lonDestino", trip.getLonDestino());
                startActivity(intent);  // Inicia PlacesActivity
            }
        });
    }

    // Actualiza el icono del botón de selección (estrella)
    private void actualizarEstrella() {
        buttonSeleccionar.setImageResource(
                seleccionado ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );
    }

    // Al presionar el botón de retroceso, devolvemos el resultado con el estado de selección
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("codigo", codigo);
        resultIntent.putExtra("seleccionado", seleccionado);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    // Para la navegación con el botón de retroceso en la toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}