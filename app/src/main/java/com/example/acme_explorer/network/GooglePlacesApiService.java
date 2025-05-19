package com.example.acme_explorer.network;

import com.example.acme_explorer.data.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesApiService {

    // Definir una llamada GET para obtener lugares cercanos
    @GET("maps/api/place/nearbysearch/json")
    Call<PlaceResponse> getNearbyPlaces(
            @Query("location") String location,  // Coordenadas (ej. "40.748817,-73.985428")
            @Query("radius") int radius,  // Radio de búsqueda en metros (ej. 1000 metros)
            @Query("type") String type,  // Tipo de lugar (ej. "restaurant", "bar")
            @Query("key") String apiKey  // Tu clave API de Google
    );
}
