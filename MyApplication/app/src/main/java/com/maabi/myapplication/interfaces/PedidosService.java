package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.DeliveryResults;
import com.maabi.myapplication.models.PedidosResults;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface PedidosService {
    @Headers({"Accept-Encoding: gzip, deflate","Accept:application/json", "Content-Type:application/json;"})
    @POST("pedidos")
    Call<PedidosResults> insertar(@Body RequestBody body);

    @GET("pedidos")
    Call<PedidosResults> obtenerPedidos(@Query("listar_por") String listar_por, @Query("e_id") int e_id, @Query("c_id") double c_id);
}
