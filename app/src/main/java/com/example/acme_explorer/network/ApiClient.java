package com.example.acme_explorer.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // La URL base de la API de Google Places
    private static final String BASE_URL = "https://maps.googleapis.com/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // URL base para las peticiones
                    .addConverterFactory(GsonConverterFactory.create())  // Convertir la respuesta JSON en objetos Java
                    .build();
        }
        return retrofit;
    }
}