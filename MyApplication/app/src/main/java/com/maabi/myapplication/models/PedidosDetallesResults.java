package com.maabi.myapplication.models;

import com.google.gson.Gson;

public class PedidosDetallesResults {
    private Object response;

    public String getResponse() {
        return new Gson().toJson(response);
    }
}
