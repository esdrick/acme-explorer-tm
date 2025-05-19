package com.example.acme_explorer.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.acme_explorer.R;
import com.example.acme_explorer.data.Actor;
import com.example.acme_explorer.auth.ActorService;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName, editSurname, editPhone;
    private Button buttonEditarGuardar;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializa el Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Configura el toolbar como ActionBar

        // Configura el botón de retroceso en el Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Activar el botón de retroceso
        getSupportActionBar().setHomeButtonEnabled(true); // Permitir que el botón de retroceso funcione

        // Título del Toolbar (si es necesario)
        getSupportActionBar().setTitle("Perfil de Usuario");

        editName = findViewById(R.id.editTextName);
        editSurname = findViewById(R.id.editTextSurname);
        editPhone = findViewById(R.id.editTextPhone);
        buttonEditarGuardar = findViewById(R.id.buttonGuardarPerfil);

        // Por defecto: modo visualización
        setCamposHabilitados(false);

        cargarPerfil();

        buttonEditarGuardar.setOnClickListener(v -> {
            if (!modoEdicion) {
                // Cambiar a modo edición
                modoEdicion = true;
                setCamposHabilitados(true);
                buttonEditarGuardar.setText("Guardar");
            } else {
                // Guardar cambios
                guardarPerfil();
            }
        });
    }

    private void setCamposHabilitados(boolean habilitar) {
        editName.setEnabled(habilitar);
        editSurname.setEnabled(habilitar);
        editPhone.setEnabled(habilitar);
    }

    private void cargarPerfil() {
        ActorService.getCurrentActorRef().get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Actor actor = snapshot.toObject(Actor.class);
                if (actor != null) {
                    editName.setText(actor.getName());
                    editSurname.setText(actor.getSurname());
                    editPhone.setText(actor.getPhone());
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show());
    }

    private void guardarPerfil() {
        ActorService.getCurrentActorRef().get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Actor actor = snapshot.toObject(Actor.class);
                if (actor != null) {
                    actor.setName(editName.getText().toString());
                    actor.setSurname(editSurname.getText().toString());
                    actor.setPhone(editPhone.getText().toString());

                    // Aquí va el bloque que mencionas
                    ActorService.createOrUpdateActor(actor)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                                modoEdicion = false;
                                setCamposHabilitados(false);
                                buttonEditarGuardar.setText("Editar");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar perfil", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos actuales", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}