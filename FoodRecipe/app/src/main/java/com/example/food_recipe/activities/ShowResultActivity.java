package com.example.pj_off.activities;

import static com.example.pj_off.service.MyApp.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pj_off.R;
import com.example.pj_off.adapter.ReceipeAdapter;
import com.example.pj_off.apCompact.MyApp;
import com.example.pj_off.api.FoodReadApi;
import com.example.pj_off.databinding.ActivityShowResultBinding;
import com.example.pj_off.intfc.FetchDataListener;
import com.example.pj_off.intfc.ViewDetail;
import com.example.pj_off.manager.LanguageManager;
import com.example.pj_off.map.Constants;
import com.example.pj_off.map.MYLocation1;
import com.example.pj_off.model.ApiResponse;
import com.example.pj_off.model.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ShowResultActivity extends MyApp implements FetchDataListener, ViewDetail {

    ActivityShowResultBinding showResultBinding;
    RecyclerView recyclerView;
    SearchView searchView;
    ReceipeAdapter adapter;
    ProgressDialog dialog;
    FirebaseAuth auth;
    String currentUserName;
    LanguageManager languageManager;
    public static int saveNumber;
    int s;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_result);
        languageManager = new LanguageManager(ShowResultActivity.this);
        dialog = new ProgressDialog(this);
        dialog.setTitle(getResources().getString(R.string.please_w8));
        dialog.setMessage(getResources().getString(R.string.fetch_the_data));


        auth = FirebaseAuth.getInstance();
        currentUserName = auth.getCurrentUser().getDisplayName();
        Log.i("tariq", "onCreate: "+currentUserName);



        FoodReadApi manager = new FoodReadApi(this);
        manager.getFood(this, null);
        SharedPreferences sharedPreferences = getSharedPreferences("db", Context.MODE_PRIVATE);
        saveNumber = sharedPreferences.getInt("number", 0);
        if (saveNumber==0)
        {

        }
        else {
            Log.i("Momo", "onCreate: " + saveNumber);
        }


        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                dialog.setTitle("Fetching recipes of " + query);
                dialog.show();
                FoodReadApi manager = new FoodReadApi(ShowResultActivity.this);
                manager.getFood(ShowResultActivity.this, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        dialog.show();

        buttonFunction();

        MYLocation1 myLocation = new MYLocation1();
        getLocation1();
        if (!hasPermissions(ShowResultActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(ShowResultActivity.this, PERMISSIONS, 150);
        }
        else {
            checkInternet();
        }
    }


    private void buttonFunction() {
        showResultBinding.signOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
        showResultBinding.currentUserName.setText(currentUserName);

        showResultBinding.languages.setOnClickListener(view -> {
            changeLanguage();
        });

        showResultBinding.location.setOnClickListener(view -> {
            Intent intent = new Intent(ShowResultActivity.this, LocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        showResultBinding.camera.setOnClickListener(view -> {
            Intent intent = new Intent(ShowResultActivity.this, UploadPicture.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

    }

    private void changeLanguage() {
        final String[] language = {"English", "French"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Choose Language");
        alertDialog.setSingleChoiceItems(language, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {

                    languageManager.updateResources("en");
                    recreate();
                }
                if (i == 1) {
                    languageManager.updateResources("fr");
                    recreate();
                }
            }
        });

        alertDialog.create();
        alertDialog.show();
    }


    @Override
    public void didFetch(ApiResponse apiResponse, String message) {
        s = apiResponse.number;
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ReceipeAdapter(this, apiResponse.results, this, this);
        recyclerView.setAdapter(adapter);

        if (apiResponse.totalResults>saveNumber) {
            Log.i("Momo", "didFetch: " + apiResponse.number);
            SharedPreferences sharedPreferences =getSharedPreferences("db", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("number", apiResponse.number);
            editor.apply();
            //Create Notification
            //Create Intent fow which class we are open
            Intent intent=new Intent(this, ShowResultActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_IMMUTABLE);

            Notification notification= new NotificationCompat.Builder(this, com.example.pj_off.service.MyApp.RECIPE_CHANNEL)
                    .setContentTitle("Recipe Service")
                    .setSubText("")
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOnlyAlertOnce(true)
                    .build();
            manager.notify(11,notification);

        }
        else if (apiResponse.totalResults==saveNumber)
        {
            Log.i("Momo", "Not New Record: ");
        }
        else
        {

        }

        dialog.dismiss();
    }

    @Override
    public void didError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewItemDetail(Result result) {
        Intent intent = new Intent(ShowResultActivity.this, ViewDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", result.getTitle());
        intent.putExtra("image", result.getImage());
        intent.putExtra("imageType", result.getImageType());
        startActivity(intent);
        Toast.makeText(this, "" + result.title, Toast.LENGTH_SHORT).show();
    }

    private void getLocation1() {
        MYLocation1.LocationResult locationResult = new MYLocation1.LocationResult() {
            @Override
            public void gotLocation(final Location location) {
                if (location != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // change UI elements here
                            Constants.location = location;
                            //progressBar.setVisibility(View.GONE);

                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("location", 0);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("lat", Double.toString(location.getLatitude()));
                            editor.putString("lng", Double.toString(location.getLongitude()));
                            editor.apply();



                        }
                    });



                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // on change les éléments ici
                            Toast.makeText(ShowResultActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        };
        MYLocation1 myLocation = new MYLocation1();
        myLocation.getLocation(this, locationResult);


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }




    void checkInternet() {
        if (isNetworkAvailable(ShowResultActivity.this)) {
            checkGPSStatus();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowResultActivity.this);
            alertDialog.setTitle("Internet Error");
            alertDialog.setMessage("Internet is not enabled! ");
            alertDialog.setPositiveButton("Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            checkInternet();
                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            System.exit(0);

                        }
                    });
            alertDialog.show();

        }
    }
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }

        } catch (Exception e) {
            Log.e("UtilsClass", "isNetworkAvailable()::::" + e.getMessage());
        }
        return false;
    }
    private void checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ShowResultActivity.this);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    ShowResultActivity.this.startActivityForResult(myIntent, 100);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        } else {
            getLocation1();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    SharedPreferences sharedPref = ShowResultActivity.this.getSharedPreferences("location", 0);
                    String lat = sharedPref.getString("lat", ""); //0 est la valeur par défaut
                    String lng = sharedPref.getString("lng", "");



                    if (!lat.equals("") && !lng.equals("")){

                    }

                }
            }, 2500);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 150:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //demande de permission
                    //   Toast.makeText(this, "Permission Granted" , Toast.LENGTH_LONG).show();
                    checkInternet();
                } else {
                    //permission with request code 1 was not granted
                    Toast.makeText(this, "Permission was not Granted" , Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkGPSStatus();
    }
}