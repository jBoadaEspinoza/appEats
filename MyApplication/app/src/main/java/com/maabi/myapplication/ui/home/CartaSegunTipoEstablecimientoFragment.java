package com.maabi.myapplication.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.interfaces.ArticulosService;
import com.maabi.myapplication.models.Establecimientos;
import com.maabi.myapplication.models.Articulos;
import com.maabi.myapplication.models.ArticulosResults;
import com.maabi.myapplication.models.PreOrdenesDetalles;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class CartaSegunTipoEstablecimientoFragment extends Fragment {
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_carta_segun_tipo_establecimiento, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity=(MainActivity)getActivity();
        if(!mainActivity.getSupportActionBar().isShowing()){
            mainActivity.getSupportActionBar().show();
        }
        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.GONE){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        mainActivity.getSupportActionBar().setTitle("");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=(RecyclerView) getActivity().findViewById(R.id.recyclerView);

        this.retrofit=new Retrofit.Builder()
                .baseUrl(MainActivity.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cargarDatos();
    }

    private void cargarDatos() {
        //Toast.makeText(getContext(),getArguments().getString("establecimiento_tipo_denominacion"),
        //       Toast.LENGTH_SHORT).show();

        ArticulosService service=this.retrofit.create(ArticulosService.class);
        Call<ArticulosResults> called=service.obtenerArticulos("tipo_establecimiento",getArguments().getInt("establecimiento_tipo_id"),-13.840682900935564,-76.2546660316882);

        called.enqueue(new Callback<ArticulosResults>(){

            @Override
            public void onResponse(Call<ArticulosResults> call, Response<ArticulosResults> response) {
                if(response.isSuccessful()){
                    ArticulosResults respuesta=response.body();
                    try {
                        JSONObject jsonObject=new JSONObject(respuesta.getResponse());
                        boolean success = jsonObject.getBoolean("success");
                        if(!success){
                            String msg = jsonObject.getString("msg");
                            Log.i(TAG, "onResponse:"+msg);
                            return;
                        }

                        ArticulosAdaptador adaptador = new ArticulosAdaptador(getContext());
                        recyclerView.setAdapter(adaptador);
                        recyclerView.setHasFixedSize(true);

                        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(layoutManager);

                        JSONArray data=jsonObject.getJSONArray("data");
                        ArrayList<Articulos> listaArticulos=new ArrayList<>();

                        for(int i=0;i<data.length();i++){
                            JSONObject item=data.getJSONObject(i);
                            int id=item.getInt("id");
                            String full_denominacion=item.getString("full_denominacion");
                            String descripcion = item.getString("descripcion");
                            double precio_pen=item.getDouble("precio_pen");
                            String imagen_url=item.getString("imagen_url");
                            int tiempo_despacho_min=item.getInt("tiempo_despacho_min");
                            int tiempo_aproximada_entrega_min=item.getInt("tiempo_aproximada_entrega_min");
                            JSONObject establecimientoJSON=item.getJSONObject("establecimiento");
                            int establecimiento_id=establecimientoJSON.getInt("id");
                            String nombre_comercial=establecimientoJSON.getString("nombre_comercial");
                            double direccion_lat=establecimientoJSON.getDouble("direccion_lat");
                            double direccion_lng=establecimientoJSON.getDouble("direccion_lng");

                            Establecimientos e=new Establecimientos();
                            e.setId(establecimiento_id);
                            e.setNombre_comercial(nombre_comercial);
                            e.setDireccion_lat(direccion_lat);
                            e.setDireccion_lng(direccion_lng);

                            Articulos p=new Articulos();
                            p.setId(id);
                            p.setFull_denominacion(full_denominacion);
                            p.setDescripcion(descripcion);
                            p.setTiempo_depacho_min(tiempo_despacho_min);
                            p.setTiempo_aproximada_entrega_min(tiempo_aproximada_entrega_min);
                            p.setPrecio_pen(precio_pen);
                            p.setImagen_url(imagen_url);
                            p.setEstablecimiento(e);
                            listaArticulos.add(p);
                        }
                        adaptador.adicionarListaArticulos(listaArticulos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "onResponse:"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ArticulosResults> call, Throwable t) {

            }
        });
    }
    public class ArticulosAdaptador extends  RecyclerView.Adapter<ArticulosAdaptador.ViewHolder> {
        private ArrayList<Articulos> dataset;
        private Context context;

        public ArticulosAdaptador(Context context){
            this.context = context;
            dataset = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Articulos articulo = dataset.get(position);
            holder.fullDenominacionTextView.setText(articulo.getFull_denominacion());
            DecimalFormat decFor = new DecimalFormat("#,###.00");

            holder.precioPenTextView.setText("S/."+String.valueOf(decFor.format(articulo.getPrecio_pen())));
            holder.nombreComercialTextView.setText(articulo.getEstablecimiento().getNombre_comercial());
            Glide.with(context)
                    .load(articulo.getImagen_url())
                  .centerCrop()
                  .crossFade()
                  .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .into(holder.articuloImagenImageView);
            //Evaluamos si aticulo esta agregado al carrito
            SharedPreferences preferences= context.getSharedPreferences("mis_preferencias",Context.MODE_PRIVATE);
            String ordenesJSON=preferences.getString("proOrdenesDetalles","");
            if(!ordenesJSON.equals("")){
                JsonParser jsonParser=new JsonParser();
                Object obj=jsonParser.parse(ordenesJSON);
                JsonArray jsonArray=(JsonArray) obj;
                boolean is_added=false;
                for(int i=0;i<jsonArray.size();i++){
                    PreOrdenesDetalles item=new Gson().fromJson(jsonArray.get(i).getAsJsonObject(),PreOrdenesDetalles.class);
                    if(String.valueOf(item.getArticulo_id()).equals(String.valueOf(articulo.getId()))){
                        is_added=true;
                    }
                }
                if(is_added){
                    holder.btnAgregarAlCarrito.setText("Retirar del carrito");
                    holder.btnAgregarAlCarrito.setBackgroundResource(R.drawable.button_round_remove);
                }else{
                    holder.btnAgregarAlCarrito.setText("Agregar al carrito");
                    holder.btnAgregarAlCarrito.setBackgroundResource(R.drawable.button_round);
                }
            }
            holder.btnAgregarAlCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.btnAgregarAlCarrito.getText().equals("Agregar al carrito")){
                        Bundle bundle = new Bundle();
                        bundle.putInt("articulo_id",articulo.getId());
                        bundle.putString("articulo_full_denominacion",articulo.getFull_denominacion());
                        bundle.putString("articulo_descripcion",articulo.getDescripcion());
                        bundle.putDouble("articulo_precio_pen",articulo.getPrecio_pen());
                        bundle.putInt("articulo_stock",articulo.getStock());
                        bundle.putInt("articulo_tiempo_despacho_min",articulo.getTiempo_depacho_min());
                        bundle.putInt("articulo_tiempo_aproximada_entrega_min",articulo.getTiempo_aproximada_entrega_min());
                        bundle.putString("articulo_imagen_url",articulo.getImagen_url());
                        bundle.putInt("establecimiento_id",articulo.getEstablecimiento().getId());
                        bundle.putString("establecimiento_nombre_comercial",articulo.getEstablecimiento().getNombre_comercial());
                        bundle.putDouble("establecimiento_direccion_lat",articulo.getEstablecimiento().getDireccion_lat());
                        bundle.putDouble("establecimiento_direccion_lng",articulo.getEstablecimiento().getDireccion_lng());
                        Navigation.findNavController(view).navigate(R.id.articuloSeleccionadoFragment,bundle);
                    }else{
                        SharedPreferences preferences= context.getSharedPreferences("mis_preferencias",Context.MODE_PRIVATE);
                        String ordenesJSON=preferences.getString("proOrdenesDetalles","");
                        SharedPreferences.Editor editor = preferences.edit();
                        if(!ordenesJSON.equals("")) {
                            JsonParser jsonParser=new JsonParser();
                            Object obj=jsonParser.parse(ordenesJSON);
                            JsonArray jsonArray=(JsonArray) obj;
                            ArrayList<PreOrdenesDetalles> listaOrdenes=new ArrayList<>();
                            for(int i=0;i<jsonArray.size();i++){
                                PreOrdenesDetalles preOrdenesDetalles=new Gson().fromJson(jsonArray.get(i).getAsJsonObject(),PreOrdenesDetalles.class);
                                if(!String.valueOf(preOrdenesDetalles.getArticulo_id()).equals(String.valueOf(articulo.getId()))){
                                    listaOrdenes.add(preOrdenesDetalles);
                                }
                            }
                            String json = new Gson().toJson(listaOrdenes);
                            editor.putString("proOrdenesDetalles", json);
                            editor.commit();
                        }
                        holder.btnAgregarAlCarrito.setText("Agregar al carrito");
                        holder.btnAgregarAlCarrito.setBackgroundResource(R.drawable.button_round);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void adicionarListaArticulos(ArrayList<Articulos> listaArticulos) {
            dataset.addAll(listaArticulos);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //private ImageView fotoImageView;
            private TextView fullDenominacionTextView;
            private TextView precioPenTextView;
            private TextView nombreComercialTextView;
            private ImageView articuloImagenImageView;
            private Button btnAgregarAlCarrito;
            public ViewHolder(View itemView) {
                super(itemView);
                articuloImagenImageView = (ImageView) itemView.findViewById(R.id.articulo_imagen);
                fullDenominacionTextView = (TextView) itemView.findViewById(R.id.full_denominacion);
                nombreComercialTextView = (TextView) itemView.findViewById(R.id.nombre_comercial);
                precioPenTextView = (TextView) itemView.findViewById(R.id.precio_pen);
                btnAgregarAlCarrito=(Button) itemView.findViewById(R.id.btnAgregarAlCarrito);

            }
        }
    }
}