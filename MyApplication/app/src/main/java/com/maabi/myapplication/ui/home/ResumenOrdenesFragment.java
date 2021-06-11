package com.maabi.myapplication.ui.home;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
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
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.models.Articulos;
import com.maabi.myapplication.models.Establecimientos;
import com.maabi.myapplication.models.PreOrdenes;
import com.maabi.myapplication.models.PreOrdenesDetalles;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class ResumenOrdenesFragment extends Fragment {
    private GridView gridViewEstablecimientosResumen;
    private Spinner spFormaDePago;
    private Button btnDireccionDeEntrega;
    private Button btnPedirAhora;
    private Switch stSeleccionHora;
    private TextView txtHoraEntrega;
    private TextView txtTotalImporte;
    private TextView txtTotalEntrega;
    private TextView txtTotalAPagar;
    private MainActivity mainActivity;
    private View rootView;

    PreOrdenes preOrdenes;
    int hour,minute;
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
        rootView= inflater.inflate(R.layout.fragment_resumen_ordenes, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mainActivity=(MainActivity) getActivity();
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
                Button btnDireccionDeEntrega=(Button) view.findViewById(R.id.btnDireccionDeEntrega);
                if(!preOrdenes.getEntrega_referencia().equals("")){
                    btnDireccionDeEntrega.setText(preOrdenes.getEntrega_referencia());
                }
                stSeleccionHora=(Switch) view.findViewById(R.id.stSeleccionHora);
                stSeleccionHora.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(!isChecked){
                            TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectHour, int selectMinute) {
                                    hour=selectHour;
                                    minute=selectMinute;
                                    txtHoraEntrega=(TextView) view.findViewById(R.id.txtHoraEntrega);
                                    txtHoraEntrega.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
                                }
                            };
                            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),onTimeSetListener,hour,minute,true);
                            timePickerDialog.show();
                        }
                    }
                });
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
                }
            }
            String[] formasDePago={"Pago en efectivo al recibir la orden","Pago con tarjeta al recibir la orden"};
            spFormaDePago=(Spinner) view.findViewById(R.id.spFormaDePago);
            spFormaDePago.setAdapter(new ArrayAdapter<String>(view.getContext(),R.layout.spinner,formasDePago));

            gridViewEstablecimientosResumen=view.findViewById(R.id.gridViewEstablecimentosResumen);
            EstablecimientosResumenAdaptador establecimientosResumenAdaptador=new EstablecimientosResumenAdaptador(listaEstablecimientos,listaPreOrdenesDetalle,getContext());
            gridViewEstablecimientosResumen.setAdapter(establecimientosResumenAdaptador);
            btnDireccionDeEntrega=(Button)view.findViewById(R.id.btnDireccionDeEntrega);
            btnDireccionDeEntrega.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.direccionDeEntregaFragment);
                }
            });
            btnPedirAhora=(Button) view.findViewById(R.id.btnPedirAhora);
            btnPedirAhora.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                }
            });
            txtTotalImporte=(TextView)view.findViewById(R.id.txtTotalImporte);
            txtTotalEntrega=(TextView)view.findViewById(R.id.txtHoraEntrega);
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

            double totalImporte=0;
            for(int i=0;i<dataset.size();i++){
                totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
            }
            txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
            txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte)));

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

                    double totalImporte=0;
                    for(int i=0;i<dataset.size();i++){
                        totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
                    }
                    txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
                    txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte)));
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

                        double totalImporte=0;
                        for(int i=0;i<dataset.size();i++){
                            totalImporte+=(dataset.get(i).getCantidad()*dataset.get(i).getPrecio_unitario_pen());
                        }
                        txtTotalImporte.setText("S/."+String.valueOf(decFor.format(totalImporte)));
                        txtTotalAPagar.setText("S/."+String.valueOf(decFor.format(totalImporte)));
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