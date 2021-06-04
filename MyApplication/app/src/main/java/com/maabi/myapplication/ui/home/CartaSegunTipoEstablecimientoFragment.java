package com.maabi.myapplication.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.interfaces.ProductosService;
import com.maabi.myapplication.models.EstablecimientosTipos;
import com.maabi.myapplication.models.EstablecimientosTiposResults;
import com.maabi.myapplication.models.Productos;
import com.maabi.myapplication.models.ProductosResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        this.retrofit=new Retrofit.Builder()
                .baseUrl(MainActivity.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cargarDatos();



        return root;
    }

    private void cargarDatos() {
        //Toast.makeText(getContext(),getArguments().getString("establecimiento_tipo_denominacion"),
        //       Toast.LENGTH_SHORT).show();

        ProductosService service=this.retrofit.create(ProductosService.class);
        Call<ProductosResults> called=service.obtenerProductos("tipo_establecimiento",getArguments().getInt("establecimiento_tipo_id"));

        called.enqueue(new Callback<ProductosResults>(){

            @Override
            public void onResponse(Call<ProductosResults> call, Response<ProductosResults> response) {
                if(response.isSuccessful()){
                    ProductosResults respuesta=response.body();
                    try {
                        JSONObject jsonObject=new JSONObject(respuesta.getResponse());
                        boolean success = jsonObject.getBoolean("success");
                        if(!success){
                            String msg = jsonObject.getString("msg");
                            Log.i(TAG, "onResponse:"+msg);
                            return;
                        }
                        recyclerView=(RecyclerView) getActivity().findViewById(R.id.recyclerView);
                        ProductosAdaptador adaptador = new ProductosAdaptador(getContext());
                        recyclerView.setAdapter(adaptador);
                        recyclerView.setHasFixedSize(true);

                        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                        recyclerView.setLayoutManager(layoutManager);

                        JSONArray data=jsonObject.getJSONArray("data");
                        ArrayList<Productos> listaProductos=new ArrayList<>();

                        for(int i=0;i<data.length();i++){
                            JSONObject item=data.getJSONObject(i);
                            int id=item.getInt("id");
                            String full_denominacion=item.getString("full_denominacion");
                            double precio_pen=item.getDouble("precio_pen");
                            String imagen_url=item.getString("imagen_url");
                            Productos p=new Productos();
                            p.setId(id);
                            p.setFull_denominacion(full_denominacion);
                            p.setPrecio_pen(precio_pen);
                            p.setImagen_url(imagen_url);
                            listaProductos.add(p);
                        }

                        adaptador.adicionarListaProductos(listaProductos);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    Log.i(TAG, "onResponse:"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ProductosResults> call, Throwable t) {

            }
        });
    }
    public class ProductosAdaptador extends  RecyclerView.Adapter<ProductosAdaptador.ViewHolder> {
        private ArrayList<Productos> dataset;
        private Context context;

        public ProductosAdaptador(Context context){
            this.context = context;
            dataset = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Productos p = dataset.get(position);
            holder.fullDenominacionTextView.setText(p.getFull_denominacion());
            holder.precioPenTextView.setText(String.valueOf(p.getPrecio_pen()));

            Glide.with(context)
                    .load(p.getImagen_url())
                  .centerCrop()
                  .crossFade()
                  .diskCacheStrategy(DiskCacheStrategy.ALL)
                  .into(holder.productoImagenImageView);
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void adicionarListaProductos(ArrayList<Productos> listaProductos) {
            dataset.addAll(listaProductos);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            //private ImageView fotoImageView;
            private TextView fullDenominacionTextView;
            private TextView precioPenTextView;
            private ImageView productoImagenImageView;
            public ViewHolder(View itemView) {
                super(itemView);
                productoImagenImageView = (ImageView) itemView.findViewById(R.id.producto_imagen);
                fullDenominacionTextView = (TextView) itemView.findViewById(R.id.full_denominacion);
                precioPenTextView = (TextView) itemView.findViewById(R.id.precio_pen);

            }
        }
    }
}