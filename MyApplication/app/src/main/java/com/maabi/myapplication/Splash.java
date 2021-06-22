package com.maabi.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maabi.myapplication.models.Clientes;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TimerTask tarea=new TimerTask() {
            @Override
            public void run() {
                SharedPreferences preferences= getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
                String clienteJSON=preferences.getString("cliente","");

                if(!clienteJSON.equals("")){
                    Intent intent=new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(Splash.this,MiNumero.class);
                    startActivity(intent);
                    finish();
                }


            }
        };
        Timer tiempo=new Timer();
        tiempo.schedule(tarea,5000);
    }
}