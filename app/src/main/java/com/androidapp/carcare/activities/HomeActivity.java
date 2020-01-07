package com.androidapp.carcare.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidapp.carcare.R;
import com.androidapp.carcare.adapters.ServicesAdapter;
import com.androidapp.carcare.adapters.SlidingImage_Adapter;
import com.androidapp.carcare.datamodels.ServiceItem;
import com.androidapp.carcare.utils.ViewPagerCustomDuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

import static com.androidapp.carcare.utils.Constants.GETSERVICELIST;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.UNDEFINED;
import static com.androidapp.carcare.utils.Constants.db_serviceid;
import static com.androidapp.carcare.utils.Constants.db_serviceimgurl;
import static com.androidapp.carcare.utils.Constants.db_servicename;
import static com.androidapp.carcare.utils.Constants.userCARBRAND;
import static com.androidapp.carcare.utils.Constants.userCARFUELTYPE;
import static com.androidapp.carcare.utils.Constants.userCARNAME;
import static com.androidapp.carcare.utils.Constants.userCARYEAR;
import static com.androidapp.carcare.utils.Constants.userLOCcity;
import static com.androidapp.carcare.utils.Constants.userLOClat;
import static com.androidapp.carcare.utils.Constants.userLOClong;
import static com.androidapp.carcare.utils.StatusBarColor.setLightStatusBar;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.car_detail_txt)
    TextView CarDetailTXT;

    @BindView(R.id.loc_detail_txt)
    TextView LocDetailTXT;

    @BindView(R.id.cart_clk_lay)
    LinearLayout cartCLK;

    @BindView(R.id.big_search_clk)
    ImageView searchCLK;

    @BindView(R.id.chat_clk_lay)
    LinearLayout chatCLK;

    @BindView(R.id.profile_clk_lay)
    LinearLayout profileCLK;

    RecyclerView ServicesrecyclerView;

    Context context;

    Handler handler;

    private static ViewPagerCustomDuration mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    ArrayList<ServiceItem> serviceItems;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    String CarBrand, CarName, CarFuel, CarYear;
    String LocLat, LocLong, LocCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initOBJECTS();

        setViewIDs();
        setClickListeners();

        serviceItems = new ArrayList<>();

        GetUserCarDetail();
        GetUserLocationDetail();

        getTheServiceListData();
        initBanners();

    }

    private void setClickListeners() {
        CarDetailTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CarDetail.class);
                startActivity(intent);
            }
        });

        LocDetailTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GetLocationActivity.class);
                startActivity(intent);
            }
        });


        cartCLK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CartActivity.class);
                startActivity(intent);
            }
        });

        searchCLK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });

        chatCLK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                startActivity(intent);
            }
        });

        profileCLK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initOBJECTS() {
        ButterKnife.bind(this);
        context = HomeActivity.this;
        handler = new Handler();
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        try {
            setLightStatusBar(getWindow().getDecorView(), HomeActivity.this);
        } catch (Exception e) {

        }
    }

    private void GetUserCarDetail() {
        CarBrand = preferences.getString(userCARBRAND, null);
        CarName = preferences.getString(userCARNAME, null);
        CarFuel = preferences.getString(userCARFUELTYPE, null);
        CarYear = preferences.getString(userCARYEAR, null);

        if (CarBrand == null || CarName == null || CarFuel == null || CarYear == null) {
            Intent intent = new Intent(context, CarDetail.class);
            startActivity(intent);
            finish();
            return;
        }

        try {
            CarDetailTXT.setText(CarBrand + " " + CarName);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "cannot get car detail", Toast.LENGTH_LONG).show();
        }


    }

    private void GetUserLocationDetail() {
        LocLat = preferences.getString(userLOClat, null);
        LocLong = preferences.getString(userLOClong, null);
        LocCity = preferences.getString(userLOCcity, null);

        if (LocCity == null) {
            if ((LocLat == null || LocLong == null)) {
                Intent intent = new Intent(context, GetLocationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            LocDetailTXT.setText(LocLat + "," + LocLong);
            return;
        }

        LocDetailTXT.setText(LocCity);
    }

    private void showTheServiceListData() {
        ServicesAdapter servicesAdapter = new ServicesAdapter(serviceItems, HomeActivity.this);

        ServicesrecyclerView.setHasFixedSize(true);
        ServicesrecyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 2));
        ServicesrecyclerView.setAdapter(servicesAdapter);

    }


    private void getTheServiceListData() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = SERVER_ADDRESS + GETSERVICELIST;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("info", "Request from mob");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("TAG ",response);
                    System.out.println("vollrescat" + response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("alldata");

                            serviceItems = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsObj = jsonArray.getJSONObject(i);
                                String SERVICEname = jsObj.getString(db_servicename);
                                String SERVICEid = jsObj.getString(db_serviceid);
                                String SERVICEimgURL = jsObj.getString(db_serviceimgurl);

                                if (SERVICEname.equalsIgnoreCase("")) SERVICEname = UNDEFINED;
                                if (SERVICEid.equalsIgnoreCase("")) SERVICEid = UNDEFINED;
                                if (SERVICEimgURL.equalsIgnoreCase("")) SERVICEimgURL = UNDEFINED;

                                ServiceItem serviceItem = new ServiceItem();
                                serviceItem.setServiceName(SERVICEname);
                                serviceItem.setServiceImageURL(SERVICEimgURL);
                                serviceItem.setServiceid(SERVICEid);

                                serviceItems.add(serviceItem);

                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    showTheServiceListData();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    //return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    return super.parseNetworkResponse(response);
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setViewIDs() {
        ServicesrecyclerView = findViewById(R.id.services_rec_view);

    }


    private void initBanners() {
        Collections.addAll(ImagesArray, IMAGES);

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(HomeActivity.this, ImagesArray));

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        NUM_PAGES = IMAGES.length;

        Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                    mPager.setCurrentItem(currentPage++, false);
                } else
                    mPager.setCurrentItem(currentPage++, true);

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this, 6000);
            }
        };

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(Update, 2000);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

    }

}
