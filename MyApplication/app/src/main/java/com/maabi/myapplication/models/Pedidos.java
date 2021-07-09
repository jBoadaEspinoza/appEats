package com.maabi.myapplication.models;

public class Pedidos {
    private int id;
    private String fecha_hora_emision;
    private Clientes cliente;
    private double entrega_lat;
    private double entrega_lng;
    private String entrega_referencia;
    private int forma_de_pago_id;
    private double forma_de_pago_monto_a_entregar;
    private String hora_de_preparacion;
    private double importe_productos_pen;
    private double importe_delivery_pen;
    private Estados estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha_hora_emision() {
        return fecha_hora_emision;
    }

    public void setFecha_hora_emision(String fecha_hora_emision) {
        this.fecha_hora_emision = fecha_hora_emision;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public double getEntrega_lat() {
        return entrega_lat;
    }

    public void setEntrega_lat(double entrega_lat) {
        this.entrega_lat = entrega_lat;
    }

    public double getEntrega_lng() {
        return entrega_lng;
    }

    public void setEntrega_lng(double entrega_lng) {
        this.entrega_lng = entrega_lng;
    }

    public String getEntrega_referencia() {
        return entrega_referencia;
    }

    public void setEntrega_referencia(String entrega_referencia) {
        this.entrega_referencia = entrega_referencia;
    }

    public int getForma_de_pago_id() {
        return forma_de_pago_id;
    }

    public void setForma_de_pago_id(int forma_de_pago_id) {
        this.forma_de_pago_id = forma_de_pago_id;
    }

    public double getForma_de_pago_monto_a_entregar() {
        return forma_de_pago_monto_a_entregar;
    }

    public void setForma_de_pago_monto_a_entregar(double forma_de_pago_monto_a_entregar) {
        this.forma_de_pago_monto_a_entregar = forma_de_pago_monto_a_entregar;
    }

    public String getHora_de_preparacion() {
        return hora_de_preparacion;
    }

    public void setHora_de_preparacion(String hora_de_preparacion) {
        this.hora_de_preparacion = hora_de_preparacion;
    }

    public double getImporte_productos_pen() {
        return importe_productos_pen;
    }

    public void setImporte_productos_pen(double importe_productos_pen) {
        this.importe_productos_pen = importe_productos_pen;
    }

    public double getImporte_delivery_pen() {
        return importe_delivery_pen;
    }

    public void setImporte_delivery_pen(double importe_delivery_pen) {
        this.importe_delivery_pen = importe_delivery_pen;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }
}
