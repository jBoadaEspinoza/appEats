package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.EstablecimientosTiposResults;
import com.maabi.myapplication.models.ProductosResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductosService {
    @GET("productos")
    Call<ProductosResults> obtenerProductos(@Query("listar_por") String listar_por, @Query("id") int id);
}
