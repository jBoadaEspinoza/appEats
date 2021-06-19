package com.maabi.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Registrate extends AppCompatActivity {
    EditText txtRegistrateNombres;
    EditText txtRegistrateApellidos;
    EditText txtRegistrateCorreoElectronico;
    Button btnRegistrateFinalizar;
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
                Intent intent=new Intent(Registrate.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}