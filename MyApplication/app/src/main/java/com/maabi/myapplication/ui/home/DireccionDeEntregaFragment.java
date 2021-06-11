package com.maabi.myapplication.ui.home;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maabi.myapplication.MainActivity;
import com.maabi.myapplication.R;
import com.maabi.myapplication.models.PreOrdenes;
import com.maabi.myapplication.models.PreOrdenesDetalles;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DireccionDeEntregaFragment extends Fragment implements OnMapReadyCallback {
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1000; /* 2 sec */
    Menu menuAntojos;
    Marker marker;
    MainActivity mainActivity;
    PreOrdenes preOrdenes;
    GoogleMap mMap;
    Location location;
    float zoomLevel = 19.0f;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button btnHecho;
    public DireccionDeEntregaFragment() {

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        menuAntojos=menu;
        inflater.inflate(R.menu.top_nav_menu_address, menu);
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search_address:
                //menuAntojos.findItem(R.id.menu_item_location_address).setVisible(false);
                SearchManager searchManager = (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);

                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

                SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                    public boolean onQueryTextChange(String newText) {
                        Log.i(TAG,"onResponse onQueryTextChange"+newText);
                        return true;
                    }

                    public boolean onQueryTextSubmit(String query) {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(query, 1);
                            if(addresses!=null){
                                if(addresses.size()==0){
                                    mensajeReferenciaNoRetornaResultado();
                                    return true;
                                }
                                Address address=addresses.get(0);
                                LatLng latLng = new LatLng((Double)address.getLatitude(), (Double)address.getLongitude());
                                marker.setPosition(latLng);
                                preOrdenes.setEntrega_lat((Double)latLng.latitude);
                                preOrdenes.setEntrega_lng((Double)latLng.longitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel));

                                return true;
                            }else{
                                mensajeReferenciaNoRetornaResultado();
                                return true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                };
                searchView.setOnQueryTextListener(queryTextListener);
                break;
            case R.id.menu_item_location_address:
                Location location=this.location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                marker.setPosition(latLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel));
                preOrdenes.setEntrega_lat(location.getLatitude());
                preOrdenes.setEntrega_lng(location.getLongitude());
                break;
            default:
                NavHostFragment.findNavController(this).navigate(R.id.resumenOrdenesFragment);
                break;
        }
        return false;
    }

    private void mensajeReferenciaNoRetornaResultado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Mensaje de Antojos App");
        builder.setMessage("La referencia a buscar no retorna ningun resultado, vuelva a intentarlo");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Hacer cosas aqui al hacer clic en el boton de aceptar
            }
        });
        builder.show();
    }

    public void onLocationChanged(Location location) {
        this.location=location;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_direccion_de_entrega, container, false);
        mainActivity = ((MainActivity) getActivity());
        mainActivity.getSupportActionBar().setTitle("Lugar de entrega");
        BottomNavigationView bottomNavigationView=(BottomNavigationView) mainActivity.findViewById(R.id.nav_view);
        if(bottomNavigationView.getVisibility()==View.VISIBLE){
            bottomNavigationView.setVisibility(View.GONE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnHecho=(Button) rootView.findViewById(R.id.btnHechoDireccionDeEntrega);
        btnHecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String referencia=getCompleteAddressString(preOrdenes.getEntrega_lat(),preOrdenes.getEntrega_lng());
                preOrdenes.setEntrega_referencia(referencia);
                SharedPreferences preferences= view.getContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String json = new Gson().toJson(preOrdenes);
                editor.putString("proOrdenes", json);
                editor.commit();
                Navigation.findNavController(view).navigate(R.id.resumenOrdenesFragment);
            }
        });
        startLocationUpdates();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        preOrdenes=new PreOrdenes();
        // Add a marker in Sydney and move the camera
        SharedPreferences preferences= getContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
        String ordenesJSON=preferences.getString("proOrdenes","");
        LatLng paracas = new LatLng(-13.832901032328493, -76.24846476447372);
        if(!ordenesJSON.equals("")){
            JsonParser jsonParser=new JsonParser();
            Object obj=jsonParser.parse(ordenesJSON);
            JsonObject jsonObject=(JsonObject) obj;
            preOrdenes=new Gson().fromJson(jsonObject,PreOrdenes.class);
            paracas= new LatLng(preOrdenes.getEntrega_lat(),preOrdenes.getEntrega_lng());
            Log.i(TAG,"posicion:"+preOrdenes.getEntrega_lat()+","+preOrdenes.getEntrega_lng());
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(paracas)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_RED))
                .title("Paracas"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paracas,zoomLevel));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng centerOfMap = mMap.getCameraPosition().target;
                marker.setPosition(centerOfMap);
                preOrdenes.setEntrega_lat((Double)centerOfMap.latitude);
                preOrdenes.setEntrega_lng((Double)centerOfMap.longitude);
                btnHecho.setText(preOrdenes.getEntrega_lat()+","+preOrdenes.getEntrega_lng());
            }
        });
    }



    private String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.i(TAG,"My Current loction address"+ strReturnedAddress.toString());
            } else {
                Log.i(TAG,"My Current loction address No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG,"My Current loction address Canont get Address!");
        }
        return strAdd;
    }
}