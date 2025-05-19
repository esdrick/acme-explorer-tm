package com.example.acme_explorer.data;

import java.io.Serializable;

public class Trip implements Serializable {
    private String titulo;
    private String ciudad;
    private String codigo;
    private String imagenUrl;
    private String descripcion;
    private int precio;
    private String fechaInicio;
    private String fechaFin;
    private boolean seleccionado;
    private String lugarSalida;
    private double latInicio;
    private double lonInicio;
    private double latDestino;
    private double lonDestino;

    // Constructor
    public Trip() {}

    public Trip(String titulo, String ciudad, String codigo, String lugarSalida, String imagenUrl,
                String descripcion, int precio, String fechaInicio, String fechaFin) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.codigo = codigo;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.seleccionado = false;
        this.lugarSalida = lugarSalida;
    }

    // Métodos get y set para cada campo

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getLugarSalida() {
        return lugarSalida;
    }

    public void setLugarSalida(String lugarSalida) {
        this.lugarSalida = lugarSalida;
    }

    public double getLatInicio() {
        return latInicio;
    }

    public void setLatInicio(double latInicio) {
        this.latInicio = latInicio;
    }

    public double getLonInicio() {
        return lonInicio;
    }

    public void setLonInicio(double lonInicio) {
        this.lonInicio = lonInicio;
    }

    public double getLatDestino() {
        return latDestino;
    }

    public void setLatDestino(double latDestino) {
        this.latDestino = latDestino;
    }

    public double getLonDestino() {
        return lonDestino;
    }

    public void setLonDestino(double lonDestino) {
        this.lonDestino = lonDestino;
    }
}