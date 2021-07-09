package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.DeliveryResults;
import com.maabi.myapplication.models.EstablecimientosResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EstablecimientosService {
    @GET("establecimientos")
    Call<EstablecimientosResults> obtenerEstablecimientosAsociadosAPedido(@Query("listar_por") String listar_por, @Query("p_id") int p_id);
}
