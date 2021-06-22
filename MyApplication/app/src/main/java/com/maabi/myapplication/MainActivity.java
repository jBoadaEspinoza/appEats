package com.maabi.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maabi.myapplication.models.EstablecimientosTipos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Menu menuAntojos;
    NavController navController;
    BottomNavigationView navView;
    public static final String API_BASE_URL="https://api.maabiapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(Color.parseColor("#FE2E64"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        SpannableStringBuilder str = new SpannableStringBuilder("AntojosApp");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 5, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(str);

        navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuAntojos.findItem(R.id.navigation_top_cart).setVisible(true);
        menuAntojos.findItem(R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu,menu);
        menuAntojos=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.navigation_top_cart:
                navController.navigate(R.id.resumenOrdenesFragment);
                break;
            case R.id.menu_item_location_address:
                //SearchView searchView=(SearchView) menuAntojos.findItem(item.getItemId()).getActionView();
                //searchView.setQueryHint("Buscar por...");
            case R.id.menu_item_search_address:
                break;
            case R.id.action_search:
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}