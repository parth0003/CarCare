package com.androidapp.carcare.activities;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidapp.carcare.Location.LocationService;
import com.androidapp.carcare.R;
import com.androidapp.carcare.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;

public class SearchActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationService appLocationService;
    Geocoder geocoder;
    List<Address> addresses;
    double latitude ,longitude;
    String city1;
    private GoogleMap mMap;
    ArrayList<String> lat,lng;
    ArrayList<String> name,title,city,rating,lat1,url;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();
        lat=new ArrayList<>();
        lat1=new ArrayList<>();
        lng=new ArrayList<>();
        name=new ArrayList<>();
        title=new ArrayList<>();
        city=new ArrayList<>();
        rating=new ArrayList<>();
        url=new ArrayList<>();
        appLocationService= new LocationService(SearchActivity.this);

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
            Toast.makeText(SearchActivity.this, "Turn On Location", Toast.LENGTH_SHORT).show();

        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SERVER_ADDRESS+Constants.GETVENDORSFORSERVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("TAG R",response);
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getString("result").equals("success"))
                            {
                                JSONArray jsonArray=jsonObject.getJSONArray("alldata");
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                            lat1.add(jsonObject1.getString("vendorloclat"));
                                            lat.add(jsonObject1.getString("vendorloclat"));
                                            lng.add(jsonObject1.getString("vendorloclong"));
                                            city.add(jsonObject1.getString("vendorcity"));
                                            name.add(jsonObject1.getString("vendorname"));
                                            title.add(jsonObject1.getString("vendortitle"));
                                            rating.add(jsonObject1.getString("vendoroverallrating"));
                                            url.add(jsonObject1.getString("vendorimgurl"));


                                }

                            }else {
                                Toast.makeText(SearchActivity.this, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG e",error.getMessage());
                Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                for (int i=0;i<lat.size();i++)
                {

                    if (lat1.get(i).isEmpty())
                    {
                        if(Geocoder.isPresent()){
                            try {
                                String location = city.get(i);
                                Geocoder gc = new Geocoder(SearchActivity.this);
                                List<Address> addresses= gc.getFromLocationName(location, 1); // get the found Address Objects

                                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                for(Address a : addresses){
                                    if(a.hasLatitude() && a.hasLongitude()){
//                                        lat.add(a.getLatitude());
//                                        lng.add(a.getLongitude());
//                                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                        LatLng customMarkerLocationOne = new LatLng(a.getLatitude(), a.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).
                                                icon(BitmapDescriptorFactory.fromBitmap(
                                                        createCustomMarker(SearchActivity.this,url.get(i),name.get(i),title.get(i),rating.get(i))))).setTitle(title.get(i));
                                    }
                                }
                            } catch (IOException e) {
                                // handle the exception
                            }
                        }
                    }else
                    {
//                        lat.add(jsonObject1.getDouble("vendorloclat"));
//                        lat.add(jsonObject1.getDouble("vendorloclong"));
                        LatLng customMarkerLocationOne = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i)));
                        mMap.addMarker(new MarkerOptions().position(customMarkerLocationOne).
                                icon(BitmapDescriptorFactory.fromBitmap(
                                        createCustomMarker(SearchActivity.this,url.get(i),name.get(i),title.get(i),rating.get(i))))).setTitle(title.get(i));
                    }
                }


                LatLng customMarkerLocationOne = new LatLng(Double.parseDouble(lat.get(0)), Double.parseDouble(lng.get(0)));
                LatLng customMarkerLocationTwo = new LatLng(Double.parseDouble(lat.get(lat.size()-1)), Double.parseDouble(lng.get(lng.size()-1)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(customMarkerLocationOne); //Taking Point A (First LatLng)
                builder.include(customMarkerLocationTwo); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
//                mMap.moveCamera(cu);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
            }
        });

//        String location = city1;
//        Geocoder gc = new Geocoder(SearchActivity.this);
//        List<Address> addresses= null; // get the found Address Objects
//        try {
//            addresses = gc.getFromLocationName(location, 1);
//            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
//            for(Address a : addresses){
//                if(a.hasLatitude() && a.hasLongitude()){
//                                        lat.add(a.getLatitude());
//                                        lng.add(a.getLongitude());
//                                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    LatLng sydney = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(city1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        // Add a marker in Sydney and move the camera

    }
    public Bitmap createCustomMarker(Context context, String resource, String _name, String _title, String rating) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
//
        final String imgurl = SERVER_ADDRESS+resource;
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);

        URL url = null;
        try {
            url = new URL(imgurl);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            markerImage.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);
        TextView txt_title = (TextView)marker.findViewById(R.id.title);
        txt_title.setText(_title);
        RatingBar ratingBar=marker.findViewById(R.id.vendor_ratingbar);
        ratingBar.setRating(Float.parseFloat(rating));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
