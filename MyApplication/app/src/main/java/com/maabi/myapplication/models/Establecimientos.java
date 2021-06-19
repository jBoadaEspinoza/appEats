package com.maabi.myapplication.models;

public class Establecimientos {
    private int id;
    private String nombre_comercial;
    private double direccion_lat;
    private double direccion_lng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre_comercial() {
        return nombre_comercial;
    }

    public void setNombre_comercial(String nombre_comercial) {
        this.nombre_comercial = nombre_comercial;
    }

    public double getDireccion_lat() {
        return direccion_lat;
    }

    public void setDireccion_lat(double direccion_lat) {
        this.direccion_lat = direccion_lat;
    }

    public double getDireccion_lng() {
        return direccion_lng;
    }

    public void setDireccion_lng(double direccion_lng) {
        this.direccion_lng = direccion_lng;
    }
}
