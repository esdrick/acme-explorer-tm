package com.example.acme_explorer.data;

public class Trip {
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

    public String getTitulo() {
        return titulo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public int getPrecio() {
        return precio;
    }
    public String getFechaInicio() {
        return fechaInicio;
    }
    public String getFechaFin() {
        return fechaFin;
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
}
