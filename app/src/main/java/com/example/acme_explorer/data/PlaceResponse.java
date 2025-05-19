package com.example.acme_explorer.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlaceResponse {

    @SerializedName("results")
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public static class Place {

        @SerializedName("name")
        private String name;

        @SerializedName("vicinity")
        private String vicinity;

        @SerializedName("rating")
        private float rating;

        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("photos")
        private List<Photo> photos;  // <-- Aquí agregamos las fotos

        public String getName() {
            return name;
        }

        public String getVicinity() {
            return vicinity;
        }

        public float getRating() {
            return rating;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public List<Photo> getPhotos() {
            return photos;
        }
    }

    public static class Geometry {
        @SerializedName("location")
        private Location location;

        public Location getLocation() {
            return location;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;

        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static class Photo {
        @SerializedName("photo_reference")
        private String photoReference;

        public String getPhotoReference() {
            return photoReference;
        }
    }
}