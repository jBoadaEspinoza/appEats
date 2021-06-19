package com.maabi.myapplication.models;

import android.text.Editable;

public class PreOrdenesDetalles {
    private int articulo_id;
    private String articulo_full_denominacion;
    private int articulo_tiempo_despacho_min;
    private int cantidad;
    private double precio_unitario_pen;
    private String sugerencias;
    private int establecimiento_id;
    private String establecimiento_nombre_comercial;

    public int getArticulo_id() {
        return articulo_id;
    }

    public void setArticulo_id(int articulo_id) {
        this.articulo_id = articulo_id;
    }

    public String getArticulo_full_denominacion() {
        return articulo_full_denominacion;
    }

    public void setArticulo_full_denominacion(String articulo_full_denominacion) {
        this.articulo_full_denominacion = articulo_full_denominacion;
    }

    public int getArticulo_tiempo_despacho_min() {
        return articulo_tiempo_despacho_min;
    }

    public void setArticulo_tiempo_despacho_min(int articulo_tiempo_despacho_min) {
        this.articulo_tiempo_despacho_min = articulo_tiempo_despacho_min;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_unitario_pen() {
        return precio_unitario_pen;
    }

    public void setPrecio_unitario_pen(double precio_unitario_pen) {
        this.precio_unitario_pen = precio_unitario_pen;
    }

    public String getSugerencias() {
        return sugerencias;
    }

    public void setSugerencias(String sugerencias) {
        this.sugerencias = sugerencias;
    }

    public int getEstablecimiento_id() {
        return establecimiento_id;
    }

    public void setEstablecimiento_id(int establecimiento_id) {
        this.establecimiento_id = establecimiento_id;
    }

    public String getEstablecimiento_nombre_comercial() {
        return establecimiento_nombre_comercial;
    }

    public void setEstablecimiento_nombre_comercial(String establecimiento_nombre_comercial) {
        this.establecimiento_nombre_comercial = establecimiento_nombre_comercial;
    }
}
