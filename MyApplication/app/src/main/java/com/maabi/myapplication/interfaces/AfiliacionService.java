package com.maabi.myapplication.interfaces;

import com.maabi.myapplication.models.AfiliacionResults;
import com.maabi.myapplication.models.PedidosResults;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AfiliacionService {
    @Headers({"Accept-Encoding: gzip, deflate","Accept:application/json", "Content-Type:application/json;"})
    @POST("afiliacion")
    Call<AfiliacionResults> enviarCodigo(@Query("action") String validar_celular, @Body RequestBody body);

    @Headers({"Accept-Encoding: gzip, deflate","Accept:application/json", "Content-Type:application/json;"})
    @POST("afiliacion")
    Call<AfiliacionResults> nuevoRegistro(@Body RequestBody body);
}
