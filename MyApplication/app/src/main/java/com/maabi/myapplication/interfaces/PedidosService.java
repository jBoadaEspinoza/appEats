package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.PedidosResults;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PedidosService {
    @Headers("Content-Type: application/json")
    @POST("pedidos")
    Call<PedidosResults> insertar(@Body String body);
}
