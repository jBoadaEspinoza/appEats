package com.maabi.myapplication.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;

import org.jetbrains.annotations.NotNull;


public class FinalizaOrdenFragment extends Fragment {
    private MainActivity mainActivity;
    private Button btnIrAMiHistorialDeCompras;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_finaliza_orden, container, false);
        //limpiamos el sharedPreference
        SharedPreferences preferences= getContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("proOrdenes", "");
        editor.putString("proOrdenesDetalles", "");
        editor.commit();


        mainActivity=(MainActivity)getActivity();
        mainActivity.getSupportActionBar().hide();
        btnIrAMiHistorialDeCompras=(Button) rootView.findViewById(R.id.btnIrAMiHistorialDeCompras);
        btnIrAMiHistorialDeCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_historial_de_compra);
            }
        });
        return rootView;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            default:
                BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
                if(bottomNavigationView.getVisibility()==View.GONE){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
                NavHostFragment.findNavController(this).navigate(R.id.navigation_home);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}