package com.maabi.eats.models;

import com.google.gson.Gson;

public class EstablecimientosTiposResults {
    private Object response;

    public String getResponse() {
        return new Gson().toJson(response);
    }
}
