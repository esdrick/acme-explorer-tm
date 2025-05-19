package com.example.acme_explorer.adapter;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acme_explorer.data.PlaceResponse;
import com.example.acme_explorer.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private List<PlaceResponse.Place> placesList;
    private double userLat, userLng;
    private String apiKey;

    // Constructor que recibe ubicación del usuario + API key
    public PlacesAdapter(List<PlaceResponse.Place> placesList, double userLat, double userLng, String apiKey) {
        this.placesList = placesList;
        this.userLat = userLat;
        this.userLng = userLng;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceResponse.Place place = placesList.get(position);

        holder.nameTextView.setText(place.getName());
        holder.vicinityTextView.setText(place.getVicinity());
        holder.ratingTextView.setText("Rating: " + place.getRating());

        // Calcular distancia
        double placeLat = place.getGeometry().getLocation().getLat();
        double placeLng = place.getGeometry().getLocation().getLng();

        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, placeLat, placeLng, results);
        float distanceInMeters = results[0];

        String distanciaTexto = distanceInMeters > 1000 ?
                String.format("%.1f km", distanceInMeters / 1000) :
                String.format("%.0f m", distanceInMeters);

        holder.distanceTextView.setText(distanciaTexto);

        // Cargar imagen con photo_reference si existe
        String photoReference = null;
        if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
            photoReference = place.getPhotos().get(0).getPhotoReference();
        }

        if (photoReference != null) {
            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo"
                    + "?maxwidth=400"
                    + "&photoreference=" + photoReference
                    + "&key=" + apiKey;

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.placeholder)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return placesList != null ? placesList.size() : 0;
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView vicinityTextView;
        TextView ratingTextView;
        TextView distanceTextView;
        ImageView imageView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.place_name);
            vicinityTextView = itemView.findViewById(R.id.place_vicinity);
            ratingTextView = itemView.findViewById(R.id.place_rating);
            distanceTextView = itemView.findViewById(R.id.place_distance);
            imageView = itemView.findViewById(R.id.placeImage); // <- importante
        }
    }
}