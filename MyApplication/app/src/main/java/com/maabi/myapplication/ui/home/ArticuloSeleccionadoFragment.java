package com.maabi.myapplication.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.models.Articulos;
import com.maabi.myapplication.models.Establecimientos;
import com.maabi.myapplication.models.PreOrdenesDetalles;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ArticuloSeleccionadoFragment extends Fragment {

    private Articulos articulo;
    private ImageView articulo_seleccionado_imagen;
    private TextView txtArticuloFullDenominacion;
    private TextView txtArticuloDescripcion;
    private TextView txtArticuloPrecioUnitarioPen;
    private Button btnAgregarArticulo;
    private Button btnArticuloSeleccionadoQuitar;
    private Button btnArticuloSeleccionadoAgregar;
    private TextView txtCantidadArticuloSeleccionado;
    private EditText etSugerencia;
    private int cantidad=1;
    public ArticuloSeleccionadoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.VISIBLE){
            bottomNavigationView.setVisibility(View.GONE);
        }

        articulo=new Articulos();
        articulo.setId(getArguments().getInt("articulo_id"));
        articulo.setFull_denominacion(getArguments().getString("articulo_full_denominacion"));
        articulo.setDescripcion(getArguments().getString("articulo_descripcion"));
        articulo.setPrecio_pen(getArguments().getDouble("articulo_precio_pen"));
        articulo.setStock(getArguments().getInt("articulo_stock"));
        articulo.setTiempo_depacho_min(getArguments().getInt("articulo_tiempo_despacho_min"));
        articulo.setTiempo_aproximada_entrega_min(getArguments().getInt("articulo_tiempo_aproximada_entrega_min"));
        articulo.setImagen_url(getArguments().getString("articulo_imagen_url"));
        Establecimientos establecimiento=new Establecimientos();
        establecimiento.setId(getArguments().getInt("establecimiento_id"));
        establecimiento.setNombre_comercial(getArguments().getString("establecimiento_nombre_comercial"));
        establecimiento.setDireccion_lat(getArguments().getDouble("establecimiento_direccion_lat"));
        establecimiento.setDireccion_lng(getArguments().getDouble("establecimiento_direccion_lng"));
        articulo.setEstablecimiento(establecimiento);

        articulo_seleccionado_imagen=(ImageView) view.findViewById(R.id.articulo_seleccionado_imagen);
        Glide.with(getContext())
                .load(articulo.getImagen_url())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(articulo_seleccionado_imagen);

        txtCantidadArticuloSeleccionado=(TextView)view.findViewById(R.id.txtCantidadArticuloSeleccionado);

        txtArticuloFullDenominacion=(TextView)view.findViewById(R.id.txtArticuloFullDenominacion);
        txtArticuloFullDenominacion.setText(articulo.getFull_denominacion().toUpperCase());

        txtArticuloDescripcion=(TextView) view.findViewById(R.id.txtArticuloDescripcion);
        txtArticuloDescripcion.setText(articulo.getDescripcion());

        etSugerencia=(EditText) view.findViewById(R.id.etSugerencias);

        txtArticuloPrecioUnitarioPen=(TextView)view.findViewById(R.id.txtArticuloPrecioUnitarioPen);
        DecimalFormat decFor = new DecimalFormat("#,###.00");
        double precioUnitario=articulo.getPrecio_pen();
        txtArticuloPrecioUnitarioPen.setText("S/."+decFor.format(precioUnitario));

        double importe=articulo.getPrecio_pen()*cantidad;
        btnAgregarArticulo=(Button) view.findViewById(R.id.btnAgregarArticulo);
        btnAgregarArticulo.setText("Agregar S/."+decFor.format(importe));

        btnArticuloSeleccionadoQuitar=(Button) view.findViewById(R.id.btnArticuloSeleccionadoQuitar);
        btnArticuloSeleccionadoQuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cantidad>1){
                    cantidad--;
                    txtCantidadArticuloSeleccionado.setText(String.valueOf(cantidad));
                    DecimalFormat decFor = new DecimalFormat("#,###.00");
                    double importe=articulo.getPrecio_pen()*cantidad;
                    btnAgregarArticulo.setText("Agregar S/."+decFor.format(importe));
                }
            }
        });
        btnArticuloSeleccionadoAgregar=(Button) view.findViewById(R.id.btnArticuloSeleccionadoAgregar);
        btnArticuloSeleccionadoAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cantidad++;
                txtCantidadArticuloSeleccionado.setText(String.valueOf(cantidad));
                DecimalFormat decFor = new DecimalFormat("#,###.00");
                double importe=articulo.getPrecio_pen()*cantidad;

                btnAgregarArticulo.setText("Agregar S/."+decFor.format(importe));
            }
        });


        btnAgregarArticulo=(Button) view.findViewById(R.id.btnAgregarArticulo);
        btnAgregarArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences= getContext().getSharedPreferences("mis_preferencias",getContext().MODE_PRIVATE);
                String ordenesJSON=preferences.getString("proOrdenesDetalles","");
                SharedPreferences.Editor editor = preferences.edit();
                if(ordenesJSON.equals("")) {
                    ArrayList<PreOrdenesDetalles> listaPreOrdenesDetalles = new ArrayList<>();
                    PreOrdenesDetalles preOrdenesDetalles=new PreOrdenesDetalles();
                    preOrdenesDetalles.setArticulo_id(articulo.getId());
                    preOrdenesDetalles.setArticulo_full_denominacion(articulo.getFull_denominacion());
                    preOrdenesDetalles.setArticulo_tiempo_despacho_min(articulo.getTiempo_depacho_min());
                    preOrdenesDetalles.setCantidad(cantidad);
                    preOrdenesDetalles.setPrecio_unitario_pen(articulo.getPrecio_pen());
                    preOrdenesDetalles.setEstablecimiento_id(articulo.getEstablecimiento().getId());
                    preOrdenesDetalles.setSugerencias(String.valueOf(etSugerencia.getText()));
                    preOrdenesDetalles.setEstablecimiento_nombre_comercial(articulo.getEstablecimiento().getNombre_comercial());
                    listaPreOrdenesDetalles.add(preOrdenesDetalles);

                    String json = new Gson().toJson(listaPreOrdenesDetalles);
                    editor.putString("proOrdenesDetalles", json);
                    editor.commit();

                }else{
                    JsonParser jsonParser=new JsonParser();
                    Object obj=jsonParser.parse(ordenesJSON);
                    JsonArray jsonArray=(JsonArray) obj;
                    ArrayList<PreOrdenesDetalles> listaPreOrdenesDetalles=new ArrayList<>();
                    for(int i=0;i<jsonArray.size();i++){
                        PreOrdenesDetalles preOrdenesDetalles=new Gson().fromJson(jsonArray.get(i).getAsJsonObject(),PreOrdenesDetalles.class);
                        listaPreOrdenesDetalles.add(preOrdenesDetalles);
                    }
                    PreOrdenesDetalles preOrdenesDetalles=new PreOrdenesDetalles();
                    preOrdenesDetalles.setArticulo_id(articulo.getId());
                    preOrdenesDetalles.setArticulo_full_denominacion(articulo.getFull_denominacion());
                    preOrdenesDetalles.setArticulo_tiempo_despacho_min(articulo.getTiempo_depacho_min());
                    preOrdenesDetalles.setCantidad(cantidad);
                    preOrdenesDetalles.setSugerencias(String.valueOf(etSugerencia.getText()));
                    preOrdenesDetalles.setPrecio_unitario_pen(articulo.getPrecio_pen());
                    preOrdenesDetalles.setEstablecimiento_id(articulo.getEstablecimiento().getId());
                    preOrdenesDetalles.setEstablecimiento_nombre_comercial(articulo.getEstablecimiento().getNombre_comercial());
                    listaPreOrdenesDetalles.add(preOrdenesDetalles);

                    String json = new Gson().toJson(listaPreOrdenesDetalles);
                    editor.putString("proOrdenesDetalles", json);
                    editor.commit();
                }
                
                Navigation.findNavController(view).navigate(R.id.resumenOrdenesFragment);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articulo_seleccionado, container, false);
    }
}