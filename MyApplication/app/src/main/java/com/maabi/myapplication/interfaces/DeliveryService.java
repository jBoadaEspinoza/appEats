package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.ArticulosResults;
import com.maabi.myapplication.models.DeliveryResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeliveryService {
    @GET("costo-de-delivery")
    Call<DeliveryResults> obtenerDatoDeDelivery(@Query("listar_por") String listar_por, @Query("e_id") int e_id, @Query("lat") double lat, @Query("lng") double lng);
}
