package com.example.acme_explorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acme_explorer.R;
import com.example.acme_explorer.data.Trip;
import com.example.acme_explorer.ui.DetailActivity;
import com.example.acme_explorer.ui.EditTripActivity;
import com.example.acme_explorer.ui.ListadoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripListOriginal;
    private List<Trip> tripListFiltrada;
    private boolean esAdmin;

    public TripAdapter(List<Trip> tripList, boolean esAdmin) {
        this.tripListOriginal = new ArrayList<>(tripList);
        this.tripListFiltrada = new ArrayList<>(tripList);
        this.esAdmin = esAdmin;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripListFiltrada.get(position);

        holder.textViewTitulo.setText(trip.getTitulo());
        holder.textViewCiudad.setText("Destino: " + trip.getCiudad() + " - Desde: " + trip.getLugarSalida());
        holder.textViewFecha.setText("Del " + trip.getFechaInicio() + " al " + trip.getFechaFin());
        holder.textViewPrecio.setText("Precio: " + trip.getPrecio() + " €");

        // Estado del botón de favorito
        holder.buttonSeleccionar.setImageResource(trip.isSeleccionado()
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);

        // Clic en favorito
        holder.buttonSeleccionar.setOnClickListener(v -> {
            boolean nuevoEstado = !trip.isSeleccionado();
            trip.setSeleccionado(nuevoEstado);
            notifyItemChanged(position);

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String codigo = trip.getCodigo();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("selectedTrips").child(uid).child(codigo);

            if (nuevoEstado) {
                ref.setValue(true);
            } else {
                ref.removeValue();
            }
        });

        // Mostrar botones si es administrador
        if (esAdmin) {
            holder.buttonEditar.setVisibility(View.VISIBLE);
            holder.buttonEliminar.setVisibility(View.VISIBLE);

            holder.buttonEditar.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EditTripActivity.class);
                intent.putExtra("trip", trip);  // Pasar el viaje completo
                v.getContext().startActivity(intent);
            });

            holder.buttonEliminar.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference("trips").child(trip.getCodigo())
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            tripListOriginal.remove(trip);
                            tripListFiltrada.remove(trip);
                            notifyDataSetChanged();
                            Toast.makeText(v.getContext(), "Viaje eliminado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.buttonEditar.setVisibility(View.GONE);
            holder.buttonEliminar.setVisibility(View.GONE);
        }

        // Cargar imagen
        Picasso.get()
                .load(trip.getImagenUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewTrip);

        // Clic en el item (detalle)
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetailActivity.class);

            // Aquí pasamos el objeto completo 'Trip' a la actividad DetailActivity
            intent.putExtra("trip", trip); // Pasamos el objeto Trip completo

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tripListFiltrada.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewTrip;
        TextView textViewTitulo, textViewCiudad, textViewFecha, textViewPrecio;
        ImageButton buttonSeleccionar;
        ImageButton buttonEditar;       // 👈 nuevo
        ImageButton buttonEliminar;     // 👈 nuevo

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTrip = itemView.findViewById(R.id.imageViewTrip);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewCiudad = itemView.findViewById(R.id.textViewCiudad);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            buttonSeleccionar = itemView.findViewById(R.id.buttonSeleccionar);
            buttonEditar = itemView.findViewById(R.id.buttonEditar);         // 👈 nuevo
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);     // 👈 nuevo
        }
    }

    public void filtrar(String texto) {
        tripListFiltrada.clear();
        if (texto.isEmpty()) {
            tripListFiltrada.addAll(tripListOriginal);
        } else {
            texto = texto.toLowerCase();
            for (Trip trip : tripListOriginal) {
                if (trip.getTitulo().toLowerCase().contains(texto)
                        || trip.getCiudad().toLowerCase().contains(texto)
                        || trip.getDescripcion().toLowerCase().contains(texto)) {
                    tripListFiltrada.add(trip);
                }
            }
        }
        notifyDataSetChanged();
    }

    public TripAdapter(List<Trip> tripList) {
        this(tripList, false); // Por defecto, no es admin
    }
}