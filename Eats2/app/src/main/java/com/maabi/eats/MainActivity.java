package com.maabi.eats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;

import com.maabi.eats.fragments.InicioFragment;

public class MainActivity extends AppCompatActivity  {
    Fragment fragmentInicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentInicio=new InicioFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedorFrameLayout,fragmentInicio).commit();

    }

}