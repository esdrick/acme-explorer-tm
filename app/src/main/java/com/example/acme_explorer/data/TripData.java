package com.example.acme_explorer.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripData {
    private static List<Trip> viajes = new ArrayList<>(Arrays.asList(
            new Trip("Escapada a París", "París", "FR001", "Sevilla",
                    "https://images.unsplash.com/photo-1502602898657-3e91760cbb34",
                    "Una escapada romántica por la ciudad del amor", 599, "15/05/2025", "20/05/2025"),

            new Trip("Aventura en Roma", "Roma", "IT002", "Sevilla",
                    "https://viajes.nationalgeographic.com.es/medio/2025/02/26/roma_d59824b1_250226111509_1280x853.webp",
                    "Explora las ruinas romanas y disfruta la pasta", 499,"15/04/2025", "20/04/2025"),

            new Trip("Tour por Berlín", "Berlín", "DE003", "Sevilla",
                    "https://images.unsplash.com/photo-1570129477492-45c003edd2be",
                    "Descubre la historia y la cultura alemana", 549, "25/04/2025", "05/05/2025"),

            new Trip("Naturaleza en Islandia", "Reikiavik", "IS004", "Sevilla",
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                    "Admira auroras boreales, volcanes y cascadas.", 999, "15/05/2025", "23/05/2025"),

            new Trip("Aventura en Marruecos", "Marrakech", "MA005", "Sevilla",
                    "https://www.atalayar.com/media/2023/03/17/20230317143015085159.jpeg",
                    "Viaje exótico al desierto del Sahara.", 679, "12/06/2025", "22/06/2025"),

            new Trip("Playas del Caribe", "Cancún", "MX006", "Sevilla",
                    "https://www.viajesfelizmargarita.com/wp-content/uploads/2022/06/blog_50_824A5.png",
                    "Relájate en las aguas turquesas y arenas blancas del Caribe.", 849, "08/06/2025", "25/06/2025")
    ));

    public static List<Trip> getViajes() {
        return viajes;
    }

    public static void setViajes(List<Trip> nuevosViajes) {
        viajes = nuevosViajes;
    }

    public static List<Trip> getViajesSeleccionados() {
        List<Trip> seleccionados = new ArrayList<>();
        for (Trip t : viajes) {
            if (t.isSeleccionado()) {
                seleccionados.add(t);
            }
        }
        return seleccionados;
    }

    public static void generarViajesAleatorios(int cantidad) {
        class PlantillaViaje {
            String ciudad, lugarSalida, imagenUrl, descripcion;

            PlantillaViaje(String ciudad, String lugarSalida, String imagenUrl, String descripcion) {
                this.ciudad = ciudad;
                this.lugarSalida = lugarSalida;
                this.imagenUrl = imagenUrl;
                this.descripcion = descripcion;
            }
        }

        List<PlantillaViaje> plantillas = Arrays.asList(
                new PlantillaViaje("París", "Sevilla",
                        "https://images.unsplash.com/photo-1502602898657-3e91760cbb34",
                        "Una escapada romántica por París"),
                new PlantillaViaje("Roma", "Valencia",
                        "https://viajes.nationalgeographic.com.es/medio/2025/02/26/roma_d59824b1_250226111509_1280x853.webp",
                        "Explora la antigua Roma"),
                new PlantillaViaje("Berlín", "Bilbao",
                        "https://images.unsplash.com/photo-1570129477492-45c003edd2be",
                        "Descubre la historia alemana"),
                new PlantillaViaje("Islandia", "Granada",
                        "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                        "Auroras boreales en Islandia"),
                new PlantillaViaje("Marrakech", "Málaga",
                        "https://www.atalayar.com/media/2023/03/17/20230317143015085159.jpeg",
                        "Desierto y zocos en Marruecos"),
                new PlantillaViaje("Cancún", "Sevilla",
                        "https://www.viajesfelizmargarita.com/wp-content/uploads/2022/06/blog_50_824A5.png",
                        "Playas paradisíacas en el Caribe")
        );

        for (int i = 0; i < cantidad; i++) {
            PlantillaViaje p = plantillas.get(i % plantillas.size());

            int precio = 400 + (int)(Math.random() * 600); // entre 400 y 999 €
            String codigo = "AUTO" + (i + 1);
            String titulo = "Viaje a " + p.ciudad;
            String fechaInicio = "15/07/2025";
            String fechaFin = "25/07/2025";

            viajes.add(new Trip(titulo, p.ciudad, codigo, p.lugarSalida, p.imagenUrl, p.descripcion, precio, fechaInicio, fechaFin));
        }
    }
}