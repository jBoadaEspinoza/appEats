package com.maabi.myapplication.ui.miperfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;

import org.jetbrains.annotations.NotNull;

public class MiPerfilFragment extends Fragment {

    MainActivity mainActivity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity=(MainActivity) getActivity();
        MainActivity mainActivity=(MainActivity)getActivity();
        if(!mainActivity.getSupportActionBar().isShowing()){
            mainActivity.getSupportActionBar().show();
        }
        mainActivity.getSupportActionBar().setTitle("Mi perfil");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView navView;
        navView = mainActivity.findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.GONE){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}