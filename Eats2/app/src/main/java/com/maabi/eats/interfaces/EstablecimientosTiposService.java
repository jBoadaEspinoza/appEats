package com.maabi.eats.interfaces;

import com.maabi.eats.models.EstablecimientosTiposResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EstablecimientosTiposService {
    @GET("tipos-de-establecimientos")
    Call<EstablecimientosTiposResults> obtenerListaTipoEstablecimientos(@Query("listar_por") String listar_por, @Query("id") int id);
}
