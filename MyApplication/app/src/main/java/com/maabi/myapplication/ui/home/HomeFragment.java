package com.maabi.myapplication.ui.home;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.interfaces.EstablecimientosTiposService;
import com.maabi.myapplication.models.EstablecimientosTipos;
import com.maabi.myapplication.models.EstablecimientosTiposResults;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    private Retrofit retrofit;
    private GridView gridView;
    private MainActivity mainActivity;
    Menu menuAntojos;
    List<EstablecimientosTipos> listaPublica;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;

    }
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        menu.findItem(R.id.navigation_top_cart).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity=(MainActivity) getActivity();
        if(mainActivity.getSupportActionBar()!=null){
            mainActivity.getSupportActionBar().setTitle(R.string.app_name_title);
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        this.retrofit=new Retrofit.Builder()
                .baseUrl(MainActivity.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gridView=view.findViewById(R.id.gridView);
        cargarDatos();

    }


    private void cargarDatos() {
        EstablecimientosTiposService service=this.retrofit.create(EstablecimientosTiposService.class);
        Call<EstablecimientosTiposResults> called=service.obtenerListaTipoEstablecimientos("grupo",1);
        called.enqueue(new Callback<EstablecimientosTiposResults>() {
            @Override
            public void onResponse(Call<EstablecimientosTiposResults> call, Response<EstablecimientosTiposResults> response) {
                if(response.isSuccessful()){
                    EstablecimientosTiposResults respuesta=response.body();
                    try {
                        JSONObject jsonObject=new JSONObject(respuesta.getResponse());
                        boolean success = jsonObject.getBoolean("success");
                        if(!success){
                            String msg = jsonObject.getString("msg");
                            Log.i(TAG, "onResponse:"+msg);
                            return;
                        }

                        JSONArray data=jsonObject.getJSONArray("data");
                        listaPublica=new ArrayList<>();

                        for(int i=0;i<data.length();i++){
                            JSONObject item=data.getJSONObject(i);
                            int id=item.getInt("id");
                            String denominacion=item.getString("denominacion");
                            String imagen_url=item.getString("imagen_url");
                            EstablecimientosTipos et=new EstablecimientosTipos();
                            et.setId(id);
                            et.setDenominacion(denominacion);
                            et.setImagen_url(imagen_url);
                            listaPublica.add(et);
                        }

                        CustomAdapter adaptador = new CustomAdapter(listaPublica,getContext());
                        gridView.setAdapter(adaptador);
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("establecimiento_tipo_id", listaPublica.get(position).getId());
                                bundle.putString("establecimiento_tipo_denominacion", listaPublica.get(position).getDenominacion());
                                Navigation.findNavController(view).navigate(R.id.cartaSegunTipoEstablecimientoFragment,bundle);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "onResponse:"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<EstablecimientosTiposResults> call, Throwable t) {
                Log.i(TAG, "onFailure:"+t.getMessage());
            }
        });
    }

    public class CustomAdapter extends BaseAdapter {
        List<EstablecimientosTipos> lista;
        Context context;

        public CustomAdapter(List<EstablecimientosTipos> lista, Context context) {
            this.lista = lista;
            this.context = context;
        }

        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public Object getItem(int position) {
            return lista.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            view= LayoutInflater.from(context).inflate(R.layout.item_establecimiento_tipo,null);
            TextView txtDenominacion = view.findViewById(R.id.denominacion);
            txtDenominacion.setText(this.lista.get(position).getDenominacion());
            ImageView ivImagen=view.findViewById(R.id.imagen);
            Glide.with(context)
                    .load(this.lista.get(position).getImagen_url())
                    .centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImagen);
            return view;
        }
    }
}