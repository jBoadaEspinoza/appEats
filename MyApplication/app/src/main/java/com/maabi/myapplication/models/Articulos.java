package com.maabi.myapplication.models;

public class Articulos {
    private int id;
    private String full_denominacion;
    private double precio_pen;
    private int stock;
    private int tiempo_depacho_min;
    private Establecimientos establecimiento;
    private String imagen_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_denominacion() {
        return full_denominacion;
    }

    public void setFull_denominacion(String full_denominacion) {
        this.full_denominacion = full_denominacion;
    }

    public double getPrecio_pen() {
        return precio_pen;
    }

    public void setPrecio_pen(double precio_pen) {
        this.precio_pen = precio_pen;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getTiempo_depacho_min() {
        return tiempo_depacho_min;
    }

    public void setTiempo_depacho_min(int tiempo_depacho_min) {
        this.tiempo_depacho_min = tiempo_depacho_min;
    }

    public Establecimientos getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimientos establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }
}
