package com.maabi.myapplication.models;

public class PreOrdenes {
    private int cliente_id;
    private double entrega_lat;
    private double entrega_lng;
    private String entrega_referencia;
    private int forma_de_pago_id;
    private double forma_de_pago_monto_a_entregar;
    private String hora_preparacion_inicio;

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

    public String getHora_preparacion_inicio() {
        return hora_preparacion_inicio;
    }

    public void setHora_preparacion_inicio(String hora_preparacion_inicio) {
        this.hora_preparacion_inicio = hora_preparacion_inicio;
    }
}
