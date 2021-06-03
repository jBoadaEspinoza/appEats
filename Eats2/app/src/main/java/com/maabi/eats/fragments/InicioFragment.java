package com.maabi.eats.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.maabi.eats.R;
import com.maabi.eats.adapters.EstablecimientosTiposAdapter;
import com.maabi.eats.interfaces.EstablecimientosTiposService;
import com.maabi.eats.models.EstablecimientosTipos;
import com.maabi.eats.models.EstablecimientosTiposResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class InicioFragment extends Fragment {
    private static final String TAG = "MAabi";

    private Retrofit retrofit;
    private RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InicioFragment() {
        // Required empty public constructor


        this.retrofit=new Retrofit.Builder()
                .baseUrl("https://api.maabiapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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

                        recyclerView = (RecyclerView) getActivity().findViewById(R.id.reciclador);
                        EstablecimientosTiposAdapter adaptador = new EstablecimientosTiposAdapter(getContext());

                        recyclerView.setAdapter((RecyclerView.Adapter) adaptador);
                        recyclerView.setHasFixedSize(true);

                        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

                        recyclerView.setLayoutManager(layoutManager);

                        ArrayList<EstablecimientosTipos> lista=new ArrayList<>();
                        for(int i=0;i<data.length();i++){
                            JSONObject item=data.getJSONObject(i);
                            int id=item.getInt("id");
                            String denominacion=item.getString("denominacion");
                            String imagen_url=item.getString("imagen_url");
                            EstablecimientosTipos et=new EstablecimientosTipos();
                            et.setId(id);
                            et.setDenominacion(denominacion);
                            et.setImagen_url(imagen_url);
                            lista.add(et);
                        }

                        adaptador.adicionarListaEstablecimientosTipos(lista);

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }
}