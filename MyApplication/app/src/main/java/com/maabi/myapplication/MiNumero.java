package com.maabi.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.maabi.myapplication.interfaces.AfiliacionService;
import com.maabi.myapplication.interfaces.PedidosService;
import com.maabi.myapplication.models.AfiliacionResults;
import com.maabi.myapplication.models.PedidosResults;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.ContentValues.TAG;

public class MiNumero extends AppCompatActivity {
    Button btnMiNumeroSiguiente;
    Retrofit retrofit;
    EditText txtMiNumero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_numero);
        txtMiNumero = (EditText) findViewById(R.id.txtMinumeroDeCelular);
        txtMiNumero.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_phone_android_24, 0);

        btnMiNumeroSiguiente=(Button)findViewById(R.id.btnMiNumeroSiguiente);
        btnMiNumeroSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofit=new Retrofit.Builder()
                        .baseUrl(MainActivity.API_BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                cargarDatos();
            }
        });
    }

    private void cargarDatos() {
        int codigo=(int)(Math.random()*(9999-100+1)+100);
        Formatter obj = new Formatter();
        String codigo_verificacion = String.valueOf(obj.format("%04d", codigo));
        AfiliacionService AfiliacionService=retrofit.create(AfiliacionService.class);
        Map<String, Object> map = new HashMap();
        map.put("celular",String.valueOf(txtMiNumero.getText()));
        map.put("codigo",codigo_verificacion);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
        Call<AfiliacionResults> called=AfiliacionService.enviarCodigo("validar_celular",requestBody);
        called.enqueue(new Callback<AfiliacionResults>() {
            @Override
            public void onResponse(Call<AfiliacionResults> call, Response<AfiliacionResults> response) {
                if(response.isSuccessful()) {
                    AfiliacionResults respuesta=response.body();
                    Log.i(TAG,"onResponse:"+respuesta.getResponse());
                    try {
                        JSONObject jsonObject = new JSONObject(respuesta.getResponse());
                        boolean success = jsonObject.getBoolean("success");
                        if(!success){
                            String msg = jsonObject.getString("msg");
                            Log.i(TAG, "onResponse:"+msg);
                            return;
                        }
                        JSONObject data=jsonObject.getJSONObject("data");
                        String codigo_verificacion=data.getString("codigo_verificacion");
                        String celular_numero=data.getString("celular_numero");
                        Intent intent=new Intent(MiNumero.this,MiCodigoEs.class);
                        intent.putExtra("codigo_verificacion", codigo_verificacion);
                        intent.putExtra("celular_numero",celular_numero);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<AfiliacionResults> call, Throwable t) {

            }
        });

    }
}