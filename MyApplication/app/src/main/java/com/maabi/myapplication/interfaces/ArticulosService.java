package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.ArticulosResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArticulosService {
    @GET("articulos")
    Call<ArticulosResults> obtenerArticulos(@Query("listar_por") String listar_por, @Query("id") int id);
}
