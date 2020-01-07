package com.androidapp.carcare.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.carcare.Location.LocationService;
import com.androidapp.carcare.R;
import com.androidapp.carcare.utils.LocationTrack;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.androidapp.carcare.utils.Constants.PERMISSION_ACCESS_COARSE_LOCATION;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.cSELECT;
import static com.androidapp.carcare.utils.Constants.userLOCcity;
import static com.androidapp.carcare.utils.Constants.userLOClat;
import static com.androidapp.carcare.utils.Constants.userLOClong;

public class GetLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int LATLONGdata = 1;
    public static final int CITYdata  = 2;
    
    public double LATval = 0;
    public double LONGval =0;
    public String CITYval = cSELECT;

    @BindView(R.id.spnr_city_loc)
    Spinner spnr_city_loc;

    @BindView(R.id.get_loc_button)
    Button get_loc_button;

    @BindView(R.id.skip)
    TextView skip;
    Activity thiscontext;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    LocationService appLocationService;
    Geocoder geocoder;
    List<Address> addresses;
    String city1;
    double latitude ,longitude;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        initOBJECTS();
        setClickListeners();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        appLocationService= new LocationService(GetLocationActivity.this);

        Location networkLocation= appLocationService.getLocation                                                                                                              (LocationManager.NETWORK_PROVIDER);
        if (networkLocation!= null)
        {
             latitude = networkLocation.getLatitude();
             longitude = networkLocation.getLongitude();
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                city1 = addresses.get(0).getLocality();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }else
        {
            Toast.makeText(GetLocationActivity.this, "Turn On Location", Toast.LENGTH_SHORT).show();

        }

        String[] city_items = new String[]{cSELECT,"Hyderabad","Banglore","Mumbai","Delhi","Gurugram","Pune","Chennai","Noida"};
        ArrayAdapter<String> brand_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, city_items);
        spnr_city_loc.setAdapter(brand_adapter);

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thiscontext,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setClickListeners() {
        spnr_city_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String city = spnr_city_loc.getSelectedItem().toString();
                if(city.equalsIgnoreCase(cSELECT)) return;
                saveLocationDataWith(CITYdata);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        get_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(thiscontext);

                LatLng sydney = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title(city1));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (locationTrack.canGetLocation()) {
                            Toast.makeText(thiscontext, "Location Fetched Succsessfully", Toast.LENGTH_SHORT).show();
                            LATval= locationTrack.getLatitude();
                            LONGval = locationTrack.getLongitude();
                            saveLocationDataWith(LATLONGdata);
                        } else {

                            locationTrack.showSettingsAlert();
                        }
                    }
                },5000);


            }
        });
    }

    private void saveLocationDataWith(int locdatatype) {
        if(locdatatype == LATLONGdata){
            editor.putString(userLOClat,Double.toString(latitude));
            editor.putString(userLOClong,Double.toString(longitude));
            editor.putString(userLOCcity, spnr_city_loc.getSelectedItem().toString());
            editor.commit();
        }

        if(locdatatype == CITYdata){
            editor.putString(userLOClat,Double.toString(latitude));
            editor.putString(userLOClong,Double.toString(longitude));
            editor.putString(userLOCcity, spnr_city_loc.getSelectedItem().toString());
            editor.commit();
        }

        Intent intent = new Intent(thiscontext,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void initOBJECTS() {
        ButterKnife.bind(this);
        thiscontext = GetLocationActivity.this;

        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }





    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;



    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(thiscontext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(locationTrack!=null)
            locationTrack.stopListener();
        }catch (Exception e){

        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        String location = city1;
//        Geocoder gc = new Geocoder(GetLocationActivity.this);
//        List<Address> addresses= null; // get the found Address Objects
//        try {
//            addresses = gc.getFromLocationName(location, 1);
//            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
//            for(Address a : addresses){
//                if(a.hasLatitude() && a.hasLongitude()){
//                                        lat.add(a.getLatitude());
//                                        lng.add(a.getLongitude());
//                                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));


//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
