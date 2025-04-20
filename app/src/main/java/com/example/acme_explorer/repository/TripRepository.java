package com.example.acme_explorer.repository;

import com.example.acme_explorer.data.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    private static final List<Trip> listaViajes = new ArrayList<>();

    public static void setListaViajes(List<Trip> viajes) {
        listaViajes.clear();
        listaViajes.addAll(viajes);
    }

    public static List<Trip> getAllTrips() {
        return new ArrayList<>(listaViajes); // Devuelve una copia para evitar modificaciones directas
    }
}