package com.maabi.myapplication.ui.home;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.interfaces.ArticulosService;
import com.maabi.myapplication.interfaces.DeliveryService;
import com.maabi.myapplication.interfaces.PedidosService;
import com.maabi.myapplication.models.Articulos;
import com.maabi.myapplication.models.ArticulosResults;
import com.maabi.myapplication.models.DeliveryResults;
import com.maabi.myapplication.models.Establecimientos;
import com.maabi.myapplication.models.PedidosResults;
import com.maabi.myapplication.models.PreOrdenes;
import com.maabi.myapplication.models.PreOrdenesDetalles;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.ContentValues.TAG;


public class ResumenOrdenesFragment extends Fragment {
    private GridView gridViewEstablecimientosResumen;
    private Spinner spFormaDePago;
    private LinearLayout btnDireccionDeEntrega;
    private LinearLayout btnHoraDeEntrega;
    private LinearLayout btnFormaDePago;
    private Button btnPedirAhora;
    private Switch stSeleccionHora;
    private TextView txtHoraEntrega;
    private TextView txtTotalImporte;
    private TextView txtTotalEntrega;
    private TextView txtTotalAPagar;
    private TextView txtFormaDePago;
    private MainActivity mainActivity;
    private View rootView;
    private Retrofit retrofit;
    private int tiempo_entrega_min_maximo=0;
    private double tiempo_delivery_maximo=0;
    PreOrdenes preOrdenes;
    int hour,minute;
    double totalImporte=0;
    double costoDelivery=0;
    Fragment self;
    public ResumenOrdenesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        menu.findItem(R.id.navigation_top_cart).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.top_nav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        self=this;
        rootView= inflater.inflate(R.layout.fragment_resumen_ordenes, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    Log.i(TAG,"onResponse:"+"hola");
                    BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
                    if(bottomNavigationView.getVisibility()==View.GONE){
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                    NavHostFragment.findNavController(self).navigate(R.id.navigation_home);
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mainActivity=(MainActivity) getActivity();
        MainActivity mainActivity=(MainActivity)getActivity();
        if(!mainActivity.getSupportActionBar().isShowing()){
            mainActivity.getSupportActionBar().show();
        }
        mainActivity.getSupportActionBar().setTitle("Detalle del pedido");
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BottomNavigationView navView;
        navView = mainActivity.findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.VISIBLE){
            bottomNavigationView.setVisibility(View.GONE);
        }

        SharedPreferences preferences= view.getContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
        String ordenesJSON=preferences.getString("proOrdenesDetalles","");

        if(!ordenesJSON.equals("")){
            String preOrdenesCabecera=preferences.getString("proOrdenes","");
            if(!preOrdenesCabecera.equals("")){
                JsonParser jsonParser=new JsonParser();
                Object obj=jsonParser.parse(preOrdenesCabecera);
                JsonObject jsonObject=(JsonObject) obj;
                preOrdenes=new Gson().fromJson(jsonObject,PreOrdenes.class);
                TextView txtDireccionEntrega=(TextView) view.findViewById(R.id.txtDireccionEntrega);
                if(!preOrdenes.getEntrega_referencia().equals("")){
                    txtDireccionEntrega.setText(preOrdenes.getEntrega_referencia());
                    txtDireccionEntrega.setTextColor(getResources().getColor(R.color.black));
                }else{
                    txtDireccionEntrega.setTextColor(getResources().getColor(R.color.gris_claro));
                }
            }else{
                preOrdenes=new PreOrdenes();
            }
            JsonParser jsonParser=new JsonParser();
            Object obj=jsonParser.parse(ordenesJSON);
            JsonArray jsonArray=(JsonArray) obj;
            List<Establecimientos> listaEstablecimientos=new ArrayList<>();
            List<PreOrdenesDetalles> listaPreOrdenesDetalle=new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++){
                PreOrdenesDetalles item=new Gson().fromJson(jsonArray.get(i).getAsJsonObject(),PreOrdenesDetalles.class);
                listaPreOrdenesDetalle.add(item);
                Establecimientos establecimientos=new Establecimientos();
                establecimientos.setId(item.getEstablecimiento_id());
                establecimientos.setNombre_comercial(item.getEstablecimiento_nombre_comercial());
                boolean esta=false;
                for(int j=0;j<listaEstablecimientos.size();j++){
                    if(listaEstablecimientos.get(j).getId()==item.getEstablecimiento_id()){
                        esta=true;
                    }
                }
                if(!esta){
                    listaEstablecimientos.add(establecimientos);

                    //debemos de obtener lat y lng de cliente;
                    double lat_entrega=preOrdenes.getEntrega_lat();
                    double lng_entrega=preOrdenes.getEntrega_lng();

                    //debemos de obetener el id del establecimiento
                    int establecimiento_id=establecimientos.getId();

                    //llamamos al retrofit
                    retrofit=new Retrofit.Builder()
                            .baseUrl(MainActivity.API_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    DeliveryService service=retrofit.create(DeliveryService.class);
                    Call<DeliveryResults> called=service.obtenerDatoDeDelivery("ubicacion_de_cliente",establecimiento_id,lat_entrega,lng_entrega);
                    called.enqueue(new Callback<DeliveryResults>() {
                        @Override
                        public void onResponse(Call<DeliveryResults> call, Response<DeliveryResults> response) {
                            if(response.isSuccessful()){
                                DeliveryResults respuesta=response.body();
                                try{
                                    JSONObject jsonObject=new JSONObject(respuesta.getResponse());
                                    boolean success = jsonObject.getBoolean("success");
                                    if(!success){
                                        String msg = jsonObject.getString("msg");
                                        Log.i(TAG, "onResponse:"+msg);
                                        return;
                                    }
                                    JSONObject data=jsonObject.getJSONObject("data");

                                    double costo_pen=data.getDouble("costo_pen");
                                    double tiempo_min=data.getDouble("tiempo_min");

                                    if(tiempo_delivery_maximo<tiempo_min){
                                        tiempo_delivery_maximo=tiempo_min;
                                    }
                                    costoDelivery+=costo_pen;
                                    DecimalFormat decFor = new DecimalFormat("#,###.00");
                                    txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
                                    txtTotalEntrega.setText("S/."+String.valueOf(decFor.format(costoDelivery)));
                                    txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte+costoDelivery)));
                                    btnPedirAhora.setText("Pedir ahora por (S/."+decFor.format(totalImporte+costoDelivery)+")");
                                }catch (Exception ex){

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DeliveryResults> call, Throwable t) {

                        }
                    });
                }
            }

            gridViewEstablecimientosResumen=view.findViewById(R.id.gridViewEstablecimentosResumen);
            EstablecimientosResumenAdaptador establecimientosResumenAdaptador=new EstablecimientosResumenAdaptador(listaEstablecimientos,listaPreOrdenesDetalle,getContext());
            gridViewEstablecimientosResumen.setAdapter(establecimientosResumenAdaptador);

            btnDireccionDeEntrega=(LinearLayout) view.findViewById(R.id.lyUbicacion);
            btnDireccionDeEntrega.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.direccionDeEntregaFragment);
                }
            });


            txtHoraEntrega=(TextView) view.findViewById(R.id.txtHoraEntrega);
            btnHoraDeEntrega=(LinearLayout) view.findViewById(R.id.lyHoraDeEntrega);
            btnHoraDeEntrega.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                    builderSingle.setIcon(R.drawable.ic_baseline_access_time_24);
                    builderSingle.setTitle("Selecciona una opcion de entrega:");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Lo antes posible");
                    arrayAdapter.add("Indicar la hora de entrega");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            if(strName.equals("Lo antes posible")){
                                String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                                preOrdenes.setHora_preparacion_inicio(currentTime);
                                txtHoraEntrega.setText("Recibiras tu pedido en "+String.valueOf(tiempo_entrega_min_maximo+tiempo_delivery_maximo)+"min aprox.");
                                txtHoraEntrega.setTextColor(getResources().getColor(R.color.black));
                                return;
                            }
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                            builderInner.setMessage(strName);
                            builderInner.setTitle("Your Selected Item is");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.show();
                }
            });
            txtFormaDePago=(TextView) view.findViewById(R.id.txtFormaDePago);
            btnFormaDePago=(LinearLayout) view.findViewById(R.id.lyFormaDePago);
            btnFormaDePago.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                    builderSingle.setIcon(R.drawable.ic_baseline_attach_money_24);
                    builderSingle.setTitle("Selecciona una opcion:");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Pago en efectivo al recibir el pedido");
                    arrayAdapter.add("Pago con tarjeta al recibir el pedido");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            if(strName.equals("Pago en efectivo al recibir el pedido")) {
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                                builderInner.setMessage("¿Con cuanto pagará?");
                                builderInner.setTitle("El total a pagar es "+txtTotalAPagar.getText());
                                final EditText inputText = new EditText(getActivity());
                                builderInner.setView(inputText);
                                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        preOrdenes.setForma_de_pago_id(1);
                                        preOrdenes.setForma_de_pago_monto_a_entregar(Double.parseDouble(String.valueOf(inputText.getText())));
                                        txtFormaDePago.setText(strName+", monto a entregar "+String.valueOf(inputText.getText()));
                                        txtFormaDePago.setTextColor(getResources().getColor(R.color.black));
                                        dialog.dismiss();
                                    }
                                });
                                builderInner.show();
                                return;
                            }
                            if(strName.equals("Pago con tarjeta al recibir el pedido")) {
                                preOrdenes.setForma_de_pago_id(2);
                                preOrdenes.setForma_de_pago_monto_a_entregar(totalImporte+costoDelivery);
                                txtFormaDePago.setText(strName);
                                txtFormaDePago.setTextColor(getResources().getColor(R.color.black));
                                return;
                            }
                        }
                    });
                    builderSingle.show();
                }
            });

            btnPedirAhora=(Button) view.findViewById(R.id.btnPedirAhora);
            btnPedirAhora.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    retrofit=new Retrofit.Builder()
                            .baseUrl(MainActivity.API_BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    PedidosService pedidosService=retrofit.create(PedidosService.class);
                    Map<String, Object> map = new HashMap();
                    map.put("cliente_id",1);
                    map.put("entrega_lat",preOrdenes.getEntrega_lat());
                    map.put("entrega_lng",preOrdenes.getEntrega_lng());
                    map.put("entrega_referencia",preOrdenes.getEntrega_referencia());
                    map.put("forma_de_pago_id",preOrdenes.getForma_de_pago_id());
                    map.put("forma_de_pago_monto_a_entregar",preOrdenes.getForma_de_pago_monto_a_entregar());
                    map.put("hora_preparacion_inicio",preOrdenes.getHora_preparacion_inicio());
                    map.put("detalles",new Gson().toJson(listaPreOrdenesDetalle).toString());
                    Log.i(TAG,"onResponse:"+new Gson().toJson(preOrdenes).toString());
                    Log.i(TAG,"onResponse:"+new Gson().toJson(listaPreOrdenesDetalle).toString());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(map)).toString());
                    Call<PedidosResults> called=pedidosService.insertar(requestBody);

                    called.enqueue(new Callback<PedidosResults>() {
                        @Override
                        public void onResponse(Call<PedidosResults> call, Response<PedidosResults> response) {
                            if(response.isSuccessful()){
                                PedidosResults respuesta=response.body();
                                try {
                                    JSONObject jsonObject = new JSONObject(respuesta.getResponse());
                                    boolean success = jsonObject.getBoolean("success");
                                    if(!success){
                                        String msg = jsonObject.getString("msg");
                                        Log.i(TAG, "onResponse:"+msg);
                                        return;
                                    }
                                    JSONObject data=jsonObject.getJSONObject("data");
                                    int id=data.getInt("id");
                                    Navigation.findNavController(view).navigate(R.id.finalizaOrdenFragment);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Log.i(TAG, "onResponse:"+response.errorBody());
                            }
                        }
                        @Override
                        public void onFailure(Call<PedidosResults> call, Throwable t) {
                            Log.i(TAG, "onFailure:"+t.getMessage());
                        }
                    });

                }
            });

            txtTotalImporte=(TextView)view.findViewById(R.id.txtTotalImporte);
            txtTotalEntrega=(TextView)view.findViewById(R.id.txtTotalEntrega);
            txtTotalAPagar=(TextView)view.findViewById(R.id.txtTotalAPagar);
        }
    }

    public class EstablecimientosResumenAdaptador extends BaseAdapter{
        List<Establecimientos> listaEstablecimientos;
        List<PreOrdenesDetalles> listaPreOrdenesDetalles;
        RecyclerView rvItemArticulosResumen;
        Context context;
        public EstablecimientosResumenAdaptador(List<Establecimientos> listaEstablecimientos,List<PreOrdenesDetalles> listaPreOrdenesDetalles,Context context){
            this.listaEstablecimientos=listaEstablecimientos;
            this.context=context;
            this.listaPreOrdenesDetalles=listaPreOrdenesDetalles;
        }

        @Override
        public int getCount() {
            return listaEstablecimientos.size();
        }

        @Override
        public Object getItem(int position) {
            return listaEstablecimientos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            if (convertView == null) {
                LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=inflater.inflate(R.layout.item_establecimiento_resumen,parent,false);
            }

            TextView txtNombreComercialResumen = convertView.findViewById(R.id.txtNombreComercialResumen);
            txtNombreComercialResumen.setText("PEDIR A "+listaEstablecimientos.get(position).getNombre_comercial().toUpperCase());
            rvItemArticulosResumen=(RecyclerView) convertView.findViewById(R.id.rvArticulosDeEstablecimientoResumen);

            List<PreOrdenesDetalles> newPreOrdenesDetalles=new ArrayList<>();
            for(int i=0;i<listaPreOrdenesDetalles.size();i++){
                if(this.listaPreOrdenesDetalles.get(i).getEstablecimiento_id()==this.listaEstablecimientos.get(position).getId()){
                    newPreOrdenesDetalles.add(this.listaPreOrdenesDetalles.get(i));

                }
            }
            ChildRecycleView adaptator=new ChildRecycleView(context);
            rvItemArticulosResumen.setAdapter(adaptator);
            rvItemArticulosResumen.setHasFixedSize(true);
            final GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
            rvItemArticulosResumen.setLayoutManager(layoutManager);
            adaptator.adicionarListaArticulos(newPreOrdenesDetalles);
            return convertView;
        }
    }

    private class ChildRecycleView extends RecyclerView.Adapter<ChildRecycleView.ViewHolder>{
        private List<PreOrdenesDetalles> dataset;
        private Context context;
        public ChildRecycleView(Context context) {
            this.context=context;
            dataset = new ArrayList<>();
        }

        @Override
        public ChildRecycleView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulos_ordenes_resumen, parent, false);
            return new ResumenOrdenesFragment.ChildRecycleView.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ChildRecycleView.ViewHolder holder, int position) {
            PreOrdenesDetalles preOrdenesDetalles = dataset.get(position);
            int cantidad=preOrdenesDetalles.getCantidad();
            holder.txtArticuloCantidad.setText(cantidad+"x ");
            holder.txtArticuloFullDenominacion.setText(preOrdenesDetalles.getArticulo_full_denominacion());
            DecimalFormat decFor = new DecimalFormat("#,###.00");
            double precioUnitario=preOrdenesDetalles.getPrecio_unitario_pen();
            double importe=precioUnitario*cantidad;

            totalImporte=0;
            for(int i=0;i<dataset.size();i++){
                if(tiempo_entrega_min_maximo<dataset.get(i).getArticulo_tiempo_despacho_min()){
                    tiempo_entrega_min_maximo=dataset.get(i).getArticulo_tiempo_despacho_min();
                }
                totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
            }
            Log.i(TAG,"onResponse vamos a ver:"+costoDelivery);
            txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
            txtTotalEntrega.setText("S/."+String.valueOf(decFor.format(costoDelivery)));
            txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte+costoDelivery)));
            btnPedirAhora.setText("Pedir ahora por (S/."+decFor.format(totalImporte+costoDelivery)+")");
            holder.txtArticuloPrecioUnitario.setText("S/."+String.valueOf(decFor.format(importe)));
            holder.btnAgregarCantidad.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int cantidad=preOrdenesDetalles.getCantidad();
                    cantidad++;
                    dataset.get(position).setCantidad(cantidad);
                    holder.txtArticuloCantidad.setText(cantidad+"x ");
                    holder.txtArticuloFullDenominacion.setText(preOrdenesDetalles.getArticulo_full_denominacion());
                    DecimalFormat decFor = new DecimalFormat("#,###.00");
                    double precioUnitario=preOrdenesDetalles.getPrecio_unitario_pen();
                    double importe=precioUnitario*cantidad;
                    holder.txtArticuloPrecioUnitario.setText("S/."+String.valueOf(decFor.format(importe)));
                    SharedPreferences preferences= context.getSharedPreferences("mis_preferencias",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    String json = new Gson().toJson(dataset);
                    editor.putString("proOrdenesDetalles", json);
                    editor.commit();

                    totalImporte=0;
                    for(int i=0;i<dataset.size();i++){
                        totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
                    }
                    txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
                    txtTotalEntrega.setText("S/."+String.valueOf(decFor.format(costoDelivery)));
                    txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte+costoDelivery)));
                    btnPedirAhora.setText("Pedir ahora por (S/."+decFor.format(totalImporte+costoDelivery)+")");
                }
            });
            holder.btnQuitarCantidad.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int cantidad=preOrdenesDetalles.getCantidad();
                    if(cantidad>1) {
                        cantidad--;
                        dataset.get(position).setCantidad(cantidad);
                        holder.txtArticuloCantidad.setText(cantidad+"x ");
                        holder.txtArticuloFullDenominacion.setText(preOrdenesDetalles.getArticulo_full_denominacion());
                        DecimalFormat decFor = new DecimalFormat("#,###.00");
                        double precioUnitario=preOrdenesDetalles.getPrecio_unitario_pen();
                        double importe=precioUnitario*cantidad;
                        holder.txtArticuloPrecioUnitario.setText("S/."+String.valueOf(decFor.format(importe)));
                        SharedPreferences preferences= context.getSharedPreferences("mis_preferencias",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        String json = new Gson().toJson(dataset);
                        editor.putString("proOrdenesDetalles", json);
                        editor.commit();

                        totalImporte=0;
                        for(int i=0;i<dataset.size();i++){
                            totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
                        }
                        Log.i(TAG,"onResponse vamos a ver:"+costoDelivery);
                        txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
                        txtTotalEntrega.setText("S/."+String.valueOf(decFor.format(costoDelivery)));
                        txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte+costoDelivery)));
                        btnPedirAhora.setText("Pedir ahora por (S/."+decFor.format(totalImporte+costoDelivery)+")");
                    }
                }
            });
        }
        public void adicionarListaArticulos(List<PreOrdenesDetalles> preOrdenesDetalles) {
            dataset.addAll(preOrdenesDetalles);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txtArticuloCantidad;
            private TextView txtArticuloFullDenominacion;
            private TextView txtArticuloPrecioUnitario;
            private Button btnAgregarCantidad;
            private Button btnQuitarCantidad;

            public ViewHolder(View itemView) {
                super(itemView);
                this.txtArticuloCantidad = (TextView) itemView.findViewById(R.id.txtCantidadOrdenResumen);
                this.txtArticuloFullDenominacion = (TextView) itemView.findViewById(R.id.txtDescripcionOrdenResumen);
                this.txtArticuloPrecioUnitario = (TextView) itemView.findViewById((R.id.txtImporteOrdenResumen));
                this.btnAgregarCantidad = (Button) itemView.findViewById(R.id.btnAgregarArticuloOrdenesResumen);
                this.btnQuitarCantidad = (Button) itemView.findViewById(R.id.btnQuitarArticuloOrdenesResumen);
            }
        }
    }
}