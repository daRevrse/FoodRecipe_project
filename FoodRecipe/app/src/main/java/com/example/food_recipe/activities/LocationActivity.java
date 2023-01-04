package com.example.pj_off.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.pj_off.R;
import com.example.pj_off.apCompact.MyApp;
import com.example.pj_off.databinding.ActivityLocationBinding;
import com.example.pj_off.map.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LocationActivity extends MyApp {

    ActivityLocationBinding locationBinding;
    com.google.android.gms.location.LocationRequest locationRequest;
    public static double latitude, longitude, accuracy, altitude;
    MapView mMapView;
    Context context;
    int check = 0;
    SweetAlertDialog sweetAlertDialogLoading;
    private GoogleMap googleMap;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationBinding = DataBindingUtil.setContentView(this, R.layout.activity_location);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        dialog = new ProgressDialog(this);
        dialog.setTitle(getResources().getString(R.string.please_w8));
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        getCurrentLocation();
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        context = getApplicationContext();
        sweetAlertDialogLoading = new SweetAlertDialog(LocationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialogLoading.setTitleText("Fetching Latest Feed..");
        sweetAlertDialogLoading.setContentText("Please Wait...");
        sweetAlertDialogLoading.setCancelable(false);
        sweetAlertDialogLoading.show();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                com.google.android.gms.maps.model.LatLng sydney;
                Log.e("-ppp","loc: "+ Constants.location);
                if (Constants.location!=null) {
                    sydney = new com.google.android.gms.maps.model.LatLng(Constants.location.getLatitude(), Constants.location.getLongitude());
                }
                else {
                    //Récupère les positions
                    SharedPreferences sharedPref = context.getSharedPreferences("location", 0);
                    String lat = sharedPref.getString("lat", ""); //0 est la valeur par défaut
                    String lng = sharedPref.getString("lng", "");
                    Double lati =Double.valueOf(lat);
                    Double longi =Double.valueOf(lng);
                    sydney = new com.google.android.gms.maps.model.LatLng(lati, longi);
                }
                // Pour zoomer dans la map automatiquement
                com.google.android.gms.maps.model.CameraPosition cameraPosition = new com.google.android.gms.maps.model.CameraPosition.Builder().target(sydney).zoom(15).build();
                googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(cameraPosition));

                MarkerOptions options = new MarkerOptions().position(sydney);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mMap.addMarker(options);


                if (sweetAlertDialogLoading.isShowing()){
                    sweetAlertDialogLoading.cancel();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(LocationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(this)
                            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(LocationActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LocationActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Pas de localisation trouvé
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                accuracy = locationResult.getLastLocation().getAccuracy();
                altitude = locationResult.getLastLocation().getAltitude();
                Log.d("Location", latitude + "," + longitude);

                locationBinding.longitude.setText(""+longitude);
                locationBinding.latitude.setText(""+latitude);
                dialog.dismiss();
            }
        }
    };
}