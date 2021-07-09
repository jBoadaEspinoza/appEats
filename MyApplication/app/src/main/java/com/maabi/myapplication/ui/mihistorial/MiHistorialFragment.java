package com.maabi.myapplication.ui.mihistorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.interfaces.EstablecimientosService;
import com.maabi.myapplication.interfaces.EstablecimientosTiposService;
import com.maabi.myapplication.interfaces.PedidosDetallesService;
import com.maabi.myapplication.interfaces.PedidosService;
import com.maabi.myapplication.models.Articulos;
import com.maabi.myapplication.models.Clientes;
import com.maabi.myapplication.models.Establecimientos;
import com.maabi.myapplication.models.EstablecimientosResults;
import com.maabi.myapplication.models.EstablecimientosTiposResults;
import com.maabi.myapplication.models.Estados;
import com.maabi.myapplication.models.Pedidos;
import com.maabi.myapplication.models.PedidosDetalles;
import com.maabi.myapplication.models.PedidosDetallesResults;
import com.maabi.myapplication.models.PedidosResults;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Formatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class MiHistorialFragment extends Fragment {
    private Retrofit retrofit;
    private RecyclerView rvPedidos;
    private Clientes cliente;
    private MainActivity mainActivity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mi_historial_de_compra, container, false);
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
        mainActivity.getSupportActionBar().setTitle("Historial de compra");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navView;
        navView = mainActivity.findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.GONE){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        SharedPreferences preferences= getContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
        String clienteJSON=preferences.getString("cliente","");
        if(!clienteJSON.equals("")){
            JsonParser jsonParser=new JsonParser();
            Object obj=jsonParser.parse(clienteJSON);
            JsonObject jsonObject=(JsonObject) obj;
            cliente=new Gson().fromJson(jsonObject, Clientes.class);
        }
        this.retrofit=new Retrofit.Builder()
                .baseUrl(MainActivity.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rvPedidos=(RecyclerView) getActivity().findViewById(R.id.rvPedidos);
        PedidosService service=this.retrofit.create(PedidosService.class);
        Call<PedidosResults> called=service.obtenerPedidos("estado",1,cliente.getId());
        called.enqueue(new Callback<PedidosResults>() {
            @Override
            public void onResponse(Call<PedidosResults> call, Response<PedidosResults> response) {
                if(response.isSuccessful()){
                    PedidosResults pedidosResults= response.body();
                    try{
                        JSONObject jsonObject=new JSONObject(pedidosResults.getResponse());
                        boolean success = jsonObject.getBoolean("success");
                        if(!success){
                            String msg = jsonObject.getString("msg");
                            Log.i(TAG, "onResponse:"+msg);
                            return;
                        }
                        PedidosAdapter adapter=new PedidosAdapter(getContext());
                        rvPedidos.setAdapter(adapter);
                        rvPedidos.setHasFixedSize(true);

                        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                        rvPedidos.setLayoutManager(layoutManager);
                        JSONArray data=jsonObject.getJSONArray("data");
                        ArrayList<Pedidos> listaPedidos=new ArrayList<>();
                        for(int i=0;i<data.length();i++){
                            JSONObject item=data.getJSONObject(i);
                            int id=item.getInt("id");
                            Pedidos pedido=new Pedidos();
                            pedido.setId(id);
                            Clientes cliente=new Clientes();
                            cliente.setId(item.getInt("cliente_id"));
                            cliente.setNombres(item.getString("cliente_nombres"));
                            cliente.setApellidos(item.getString("cliente_apellidos"));
                            pedido.setCliente(cliente);

                            Estados estado=new Estados();
                            estado.setId(item.getInt("estado_id"));
                            estado.setActualmente(item.getString("estado_actualmente"));
                            estado.setHace(item.getString("estado_hace"));
                            estado.setAccion(item.getString("estado_accion_a_realizar"));
                            pedido.setEstado(estado);

                            listaPedidos.add(pedido);
                        }
                        adapter.adicionarListaPedidos(listaPedidos);
                    }catch (Exception ex){}
                }
            }

            @Override
            public void onFailure(Call<PedidosResults> call, Throwable t) {

            }
        });
    }
    public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder>{
        private ArrayList<Pedidos> dataset;
        private Context context;
        private Retrofit retrofit;
        public PedidosAdapter(Context context){
            this.context=context;
            dataset=new ArrayList<>();
        }
        @Override
        public PedidosAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_historial_de_compras,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
              Pedidos pedido=dataset.get(position);
              Formatter fmt = new Formatter();
              fmt.format("%05d",pedido.getId());
              holder.txtPedidoId.setText("ORD"+String.valueOf(fmt));
              holder.txtClienteHistorialDeCompras.setText(pedido.getCliente().getApellidos()+" "+pedido.getCliente().getNombres());
              holder.txtTiempoTrascurrido.setText("hace "+pedido.getEstado().getHace());
              holder.txtProcesoActual.setText(pedido.getEstado().getActualmente());
              this.retrofit=new Retrofit.Builder()
                    .baseUrl(MainActivity.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
             EstablecimientosService service=this.retrofit.create(EstablecimientosService.class);
             Call<EstablecimientosResults> called=service.obtenerEstablecimientosAsociadosAPedido("pedido",pedido.getId());
             called.enqueue(new Callback<EstablecimientosResults>() {
                 @Override
                 public void onResponse(Call<EstablecimientosResults> call, Response<EstablecimientosResults> response) {
                     if(response.isSuccessful()){
                         EstablecimientosResults establecimientosResults= response.body();
                         try{
                             JSONObject jsonObject=new JSONObject(establecimientosResults.getResponse());
                             boolean success = jsonObject.getBoolean("success");
                             if(!success){
                                 String msg = jsonObject.getString("msg");
                                 Log.i(TAG, "onResponse:"+msg);
                                 return;
                             }
                             EstablecimientosAdapter adapter=new EstablecimientosAdapter(getContext(),pedido);
                             holder.rvEstablecimientosAsociadosApedido.setAdapter(adapter);
                             holder.rvEstablecimientosAsociadosApedido.setHasFixedSize(true);
                             final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                             holder.rvEstablecimientosAsociadosApedido.setLayoutManager(layoutManager);

                             JSONArray data=jsonObject.getJSONArray("data");
                             //Log.i(TAG,"onResponse"+data.toString());
                             ArrayList<Establecimientos> listaEstablecimientos=new ArrayList<>();
                             for(int i=0;i<data.length();i++){
                                 JSONObject item=data.getJSONObject(i);
                                 int id=item.getInt("id");
                                 String nombre_comercial=item.getString("nombre_comercial");
                                 Establecimientos establecimiento=new Establecimientos();
                                 establecimiento.setId(id);
                                 establecimiento.setNombre_comercial(nombre_comercial);
                                 listaEstablecimientos.add(establecimiento);
                             }
                             adapter.adicionarListaEstablecimientos(listaEstablecimientos);
                         }catch(Exception ex){

                         }
                     }
                 }

                 @Override
                 public void onFailure(Call<EstablecimientosResults> call, Throwable t) {

                 }
             });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void adicionarListaPedidos(ArrayList<Pedidos> listaPedidos) {
            dataset.addAll(listaPedidos);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txtPedidoId;
            private TextView txtClienteHistorialDeCompras;
            private TextView txtTiempoTrascurrido;
            private TextView txtProcesoActual;
            private RecyclerView rvEstablecimientosAsociadosApedido;
            public ViewHolder(View itemView) {
                super(itemView);
                txtPedidoId=(TextView) itemView.findViewById(R.id.txtPedidoId);
                txtClienteHistorialDeCompras=(TextView) itemView.findViewById(R.id.txtClienteHistorialDeCompras);
                txtTiempoTrascurrido=(TextView) itemView.findViewById(R.id.txtTiempoTrascurrido);
                txtProcesoActual=(TextView) itemView.findViewById(R.id.txtProcesoActual);
                rvEstablecimientosAsociadosApedido=(RecyclerView) itemView.findViewById(R.id.rvEstablecimientosAsociadosApedido);
            }
        }
    }
    public class EstablecimientosAdapter extends RecyclerView.Adapter<EstablecimientosAdapter.ViewHolder>{
        private ArrayList<Establecimientos> dataset;
        private Context context;
        private Retrofit retrofit;
        private Pedidos pedido;

        public EstablecimientosAdapter(Context context,Pedidos pedido){
            this.context=context;
            dataset=new ArrayList<>();
            this.pedido=pedido;
        }

        @Override
        public EstablecimientosAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_establecimientos_asociados_a_pedido,parent,false);
            return new EstablecimientosAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull EstablecimientosAdapter.ViewHolder holder, int position) {
            Establecimientos establecimiento=dataset.get(position);
            holder.txtEstablecimientoAsociadoAPedido.setText("PRODUCTOS PEDIDOS A "+establecimiento.getNombre_comercial().toUpperCase());
            this.retrofit=new Retrofit.Builder()
                    .baseUrl(MainActivity.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PedidosDetallesService service=this.retrofit.create(PedidosDetallesService.class);
            Call<PedidosDetallesResults> called=service.obtenerArticulosAgregados("pedido_establecimiento",pedido.getId(), establecimiento.getId());
            called.enqueue(new Callback<PedidosDetallesResults>() {
                @Override
                public void onResponse(Call<PedidosDetallesResults> call, Response<PedidosDetallesResults> response) {
                    if(response.isSuccessful()){
                        PedidosDetallesResults pedidosDetallesResults= response.body();
                        try{
                            JSONObject jsonObject=new JSONObject(pedidosDetallesResults.getResponse());
                            boolean success = jsonObject.getBoolean("success");
                            if(!success){
                                String msg = jsonObject.getString("msg");
                                Log.i(TAG, "onResponse:"+msg);
                                return;
                            }
                            PedidosDetallesAdapter adapter=new PedidosDetallesAdapter(getContext());
                            holder.rvDetallePedidoAsociadoAEstablecimiento.setAdapter(adapter);
                            holder.rvDetallePedidoAsociadoAEstablecimiento.setHasFixedSize(true);
                            final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                            holder.rvDetallePedidoAsociadoAEstablecimiento.setLayoutManager(layoutManager);

                            JSONArray data=jsonObject.getJSONArray("data");
                            //Log.i(TAG,"onResponse"+data.toString());
                            ArrayList<PedidosDetalles> listaPedidosDetalles=new ArrayList<>();
                            for(int i=0;i<data.length();i++){
                                PedidosDetalles pd=new PedidosDetalles();
                                JSONObject item=data.getJSONObject(i);
                                int cantidad=item.getInt("cantidad");
                                double precio_unitario_pen=item.getDouble("precio_unitario_pen");
                                String sugerencia=item.getString("sugerencia");
                                JSONObject item_articulo=item.getJSONObject("articulo");
                                String full_denominacion=item_articulo.getString("full_denominacion");
                                Articulos articulo=new Articulos();
                                articulo.setFull_denominacion(full_denominacion);
                                pd.setCantidad(cantidad);
                                pd.setPrecio_unitario_pen(precio_unitario_pen);
                                pd.setSugerencia(sugerencia);
                                pd.setArticulo(articulo);
                                listaPedidosDetalles.add(pd);
                            }
                            adapter.adicionarListaPedidosDetalles(listaPedidosDetalles);
                        }catch(Exception ex){

                        }
                    }
                }

                @Override
                public void onFailure(Call<PedidosDetallesResults> call, Throwable t) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public void adicionarListaEstablecimientos(ArrayList<Establecimientos> listaEstablecimientos) {
            dataset.addAll(listaEstablecimientos);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtEstablecimientoAsociadoAPedido;
            RecyclerView rvDetallePedidoAsociadoAEstablecimiento;
            public ViewHolder(View itemView) {
                super(itemView);
                txtEstablecimientoAsociadoAPedido=(TextView) itemView.findViewById(R.id.txtEstablecimientoAsociadoAPedido);
                rvDetallePedidoAsociadoAEstablecimiento=(RecyclerView) itemView.findViewById(R.id.rvDetallePedidoAsociadoAEstablecimiento);
            }
        }
    }
    public class PedidosDetallesAdapter extends RecyclerView.Adapter<PedidosDetallesAdapter.ViewHolder>{
        private ArrayList<PedidosDetalles> dataset;
        private Context context;

        public PedidosDetallesAdapter(Context context){
            this.context=context;
            dataset=new ArrayList<>();
        }

        @Override
        public PedidosDetallesAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulos_asociados_a_historial_de_compra,parent,false);
            return new PedidosDetallesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull PedidosDetallesAdapter.ViewHolder holder, int position) {
            PedidosDetalles pedidoDetalles=dataset.get(position);
            holder.txtFullNombreArticuloAsociado.setText(pedidoDetalles.getArticulo().getFull_denominacion());
            holder.txtCantidadArticuloAsociado.setText(String.valueOf(pedidoDetalles.getCantidad())+"x ");
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
        public void adicionarListaPedidosDetalles(ArrayList<PedidosDetalles> listaPedidosDetalles) {
            dataset.addAll(listaPedidosDetalles);
            notifyDataSetChanged();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtFullNombreArticuloAsociado;
            TextView txtCantidadArticuloAsociado;
            public ViewHolder(View itemView) {
                super(itemView);
                txtFullNombreArticuloAsociado=(TextView) itemView.findViewById(R.id.txtFullNombreArticuloAsociado);
                txtCantidadArticuloAsociado=(TextView) itemView.findViewById(R.id.txtCantidadArticuloAsociado);
            }
        }
    }
}