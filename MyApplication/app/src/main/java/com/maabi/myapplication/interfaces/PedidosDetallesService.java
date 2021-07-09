package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.DeliveryResults;
import com.maabi.myapplication.models.PedidosDetallesResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PedidosDetallesService {
    @GET("pedidos-detalles")
    Call<PedidosDetallesResults> obtenerArticulosAgregados(@Query("listar_por") String listar_por, @Query("p_id") int p_id,@Query("e_id") int e_id);

}
