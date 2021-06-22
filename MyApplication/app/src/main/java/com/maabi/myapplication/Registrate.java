package com.maabi.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.maabi.myapplication.interfaces.AfiliacionService;
import com.maabi.myapplication.models.AfiliacionResults;
import com.maabi.myapplication.models.Clientes;

import org.json.JSONObject;

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

public class Registrate extends AppCompatActivity {
    EditText txtRegistrateNombres;
    EditText txtRegistrateApellidos;
    EditText txtRegistrateCorreoElectronico;
    Button btnRegistrateFinalizar;
    Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);
        txtRegistrateNombres = (EditText) findViewById(R.id.txtRegistrateNombres);
        txtRegistrateNombres.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_person_24, 0);
        txtRegistrateApellidos = (EditText) findViewById(R.id.txtRegistrateApellidos);
        txtRegistrateApellidos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_person_24, 0);
        txtRegistrateCorreoElectronico = (EditText) findViewById(R.id.txtRegistrateCorreoElectronico);
        txtRegistrateCorreoElectronico.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_email_24, 0);
        btnRegistrateFinalizar=(Button) findViewById(R.id.btnRegistrateFinalizar);
        btnRegistrateFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                b = getIntent().getExtras();
                String celular_numero=b.getString("celular_numero");
                String nombres=String.valueOf(txtRegistrateNombres.getText());
                String apellidos=String.valueOf(txtRegistrateApellidos.getText());
                String correo_electronico=String.valueOf(txtRegistrateCorreoElectronico.getText());

                retrofit=new Retrofit.Builder()
                        .baseUrl(MainActivity.API_BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                AfiliacionService AfiliacionService=retrofit.create(AfiliacionService.class);

                Map<String, Object> map = new HashMap();
                map.put("celular_numero",celular_numero);
                map.put("correo_electronico",correo_electronico);
                map.put("nombres",nombres);
                map.put("apellidos",apellidos);

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
                Call<AfiliacionResults> called=AfiliacionService.nuevoRegistro(requestBody);

                called.enqueue(new Callback<AfiliacionResults>() {
                    @Override
                    public void onResponse(Call<AfiliacionResults> call, Response<AfiliacionResults> response) {
                        if(response.isSuccessful()){
                            Log.i(TAG,"onResponse:"+celular_numero+","+nombres+","+apellidos+","+correo_electronico);
                            AfiliacionResults respuesta=response.body();
                            try{
                                JSONObject jsonObject = new JSONObject(respuesta.getResponse());
                                boolean success = jsonObject.getBoolean("success");
                                if(!success){
                                    String msg = jsonObject.getString("msg");
                                    Log.i(TAG, "onResponse:"+msg);
                                    return;
                                }
                                JSONObject data=jsonObject.getJSONObject("data");
                                Intent intent=new Intent(Registrate.this,MainActivity.class);

                                SharedPreferences preferences= getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
                                String clienteJSON=preferences.getString("cliente","");
                                SharedPreferences.Editor editor = preferences.edit();
                                Clientes cliente=new Clientes();
                                cliente.setId(data.getInt("id"));
                                cliente.setCelular(celular_numero);
                                cliente.setCorreo_electronico(correo_electronico);
                                cliente.setNombres(nombres);
                                cliente.setApellidos(apellidos);
                                String json = new Gson().toJson(cliente);
                                editor.putString("cliente", json);
                                editor.commit();

                                startActivity(intent);

                            }catch(Exception ex){

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AfiliacionResults> call, Throwable t) {

                    }
                });

            }
        });
    }


}