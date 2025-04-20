package com.example.acme_explorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageButton;
import java.util.List;
import java.util.ArrayList;

import com.example.acme_explorer.R;
import com.example.acme_explorer.data.Trip;
import com.example.acme_explorer.ui.DetailActivity;
import com.example.acme_explorer.ui.ListadoActivity;
import com.squareup.picasso.Picasso;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripListOriginal;
    private List<Trip> tripListFiltrada;

    public TripAdapter(List<Trip> tripList) {
        this.tripListOriginal = new ArrayList<>(tripList);
        this.tripListFiltrada = new ArrayList<>(tripList);
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

        if (trip.isSeleccionado()) {
            holder.buttonSeleccionar.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.buttonSeleccionar.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.buttonSeleccionar.setOnClickListener(v -> {
            trip.setSeleccionado(!trip.isSeleccionado());
            notifyItemChanged(position);
        });

        Picasso.get()
                .load(trip.getImagenUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageViewTrip);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("titulo", trip.getTitulo());
            intent.putExtra("ciudad", trip.getCiudad());
            intent.putExtra("codigo", trip.getCodigo());
            intent.putExtra("imagenUrl", trip.getImagenUrl());
            intent.putExtra("descripcion", trip.getDescripcion());
            intent.putExtra("precio", trip.getPrecio());
            intent.putExtra("fechaInicio", trip.getFechaInicio());
            intent.putExtra("fechaFin", trip.getFechaFin());
            intent.putExtra("lugarSalida", trip.getLugarSalida());
            intent.putExtra("seleccionado", trip.isSeleccionado());
            if (context instanceof ListadoActivity) {
                ((ListadoActivity) context).lanzarDetalle(intent);
            } else {
                context.startActivity(intent); // fallback
            }
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

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTrip = itemView.findViewById(R.id.imageViewTrip);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewCiudad = itemView.findViewById(R.id.textViewCiudad);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            buttonSeleccionar = itemView.findViewById(R.id.buttonSeleccionar);
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
}