package com.holidevs.weatherapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.holidevs.weatherapp.R;
import com.holidevs.weatherapp.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private AppCompatButton btnPronostico;
    private LatLng location;

    //Widget
    private SearchView mSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initComponents();
        inflateGoogleMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mSearchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location2 = mSearchText.getQuery().toString();
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(MapsActivity.this);

                try {
                    addressList = geocoder.getFromLocationName(location2, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert addressList != null;
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(location2));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                location = latLng;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        setOnClickListeners();
    }

    private void setOnClickListeners() {
        btnPronostico.setOnClickListener(v -> {
            // Iniciar MainActivity y pasar las coordenadas como extras
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            intent.putExtra("latitude", location.latitude);
            intent.putExtra("longitude", location.longitude);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setResult(RESULT_OK, intent);
            Log.i("intentMaps", location.toString());

            // Finalizar MapsActivity si no deseas volver a ella
            finish();
        });

        Log.i("intentMaps", location.toString());
    }


    private void initComponents() {

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mSearchText = findViewById(R.id.inputSearchLocation);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        location = new LatLng(latitude, longitude);

        btnPronostico = findViewById(R.id.btnVerPronostico);
    }

    private void inflateGoogleMap() {
        Log.d("MapsActivity", "Layout inflated successfully");
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;


        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        //mMap.getUiSettings().setZoomGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        float zoomLevel = 12.0f;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(zoomLevel)
                .build();

        mMap.setOnMapClickListener(this);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        location = latLng;


    }

}
