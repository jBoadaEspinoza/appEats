package com.maabi.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MiCodigoEs extends AppCompatActivity {
    Button btnMiCodigoEsSiguiente;
    EditText txtCifra1;
    EditText txtCifra2;
    EditText txtCifra3;
    EditText txtCifra4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_codigo_es);
        txtCifra1=(EditText)findViewById(R.id.txtCifra1);
        txtCifra2=(EditText)findViewById(R.id.txtCifra2);
        txtCifra3=(EditText)findViewById(R.id.txtCifra3);
        txtCifra4=(EditText)findViewById(R.id.txtCifra4);
        btnMiCodigoEsSiguiente=(Button) findViewById(R.id.btnMiCodigoEsSiguiente);
        btnMiCodigoEsSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String codigo_verificacion_digitado=String.valueOf(txtCifra1.getText())+String.valueOf(txtCifra2.getText())+String.valueOf(txtCifra3.getText())+String.valueOf(txtCifra4.getText());
                Bundle b = new Bundle();
                b = getIntent().getExtras();
                String codigo_verificacion = b.getString("codigo_verificacion");
                String celular_numero=b.getString("celular_numero");
                if(codigo_verificacion.equals(codigo_verificacion_digitado)){
                    Intent intent=new Intent(MiCodigoEs.this,Registrate.class);
                    intent.putExtra("celular_numero",celular_numero);
                    startActivity(intent);
                }else{
                    Toast.makeText(view.getContext(),"El codigo de verificación digitado es incorrecto",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}