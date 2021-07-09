package com.maabi.myapplication.models;

public class PedidosDetalles {
    private double precio_unitario_pen;
    private Articulos articulo;
    private int cantidad;
    private String sugerencia;

    public double getPrecio_unitario_pen() {
        return precio_unitario_pen;
    }

    public void setPrecio_unitario_pen(double precio_unitario_pen) {
        this.precio_unitario_pen = precio_unitario_pen;
    }

    public Articulos getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulos articulo) {
        this.articulo = articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }
}
