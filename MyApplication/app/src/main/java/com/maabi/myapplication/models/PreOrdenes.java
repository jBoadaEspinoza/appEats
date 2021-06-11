package com.maabi.myapplication.models;

public class PreOrdenes {
    private int cliente_id;
    private double entrega_lat;
    private double entrega_lng;
    private String entrega_referencia;
    private String entrega_hora;
    private int tipo_pago_id;
    private double monto_recibido;

    public int getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(int cliente_id) {
        this.cliente_id = cliente_id;
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

    public String getEntrega_hora() {
        return entrega_hora;
    }

    public void setEntrega_hora(String entrega_hora) {
        this.entrega_hora = entrega_hora;
    }

    public int getTipo_pago_id() {
        return tipo_pago_id;
    }

    public void setTipo_pago_id(int tipo_pago_id) {
        this.tipo_pago_id = tipo_pago_id;
    }

    public double getMonto_recibido() {
        return monto_recibido;
    }

    public void setMonto_recibido(double monto_recibido) {
        this.monto_recibido = monto_recibido;
    }
}
