package com.androidapp.carcare.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.androidapp.carcare.datamodels.CarBrandItem;
import com.androidapp.carcare.datamodels.CarNameItem;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.androidapp.carcare.utils.Constants.GETAUSERINFO;
import static com.androidapp.carcare.utils.Constants.GETCARSLIST;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.userCARBRAND;
import static com.androidapp.carcare.utils.Constants.userCARFUELTYPE;
import static com.androidapp.carcare.utils.Constants.userCARNAME;
import static com.androidapp.carcare.utils.Constants.userCARYEAR;

public class CarDetail extends AppCompatActivity {



    @BindView(R.id.spnr_car_brand)
    Spinner spnr_car_brand;

    @BindView(R.id.spnr_car_name)
    Spinner spnr_car_name;

    @BindView(R.id.spnr_car_fuel)
    Spinner spnr_car_fuel;

    @BindView(R.id.spnr_car_year)
    Spinner spnr_car_year;

    @BindView(R.id.next_card_butn)
    CardView NextButn;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    ArrayList<CarBrandItem> carbrandsArr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        initOBJECTS();
        setClickListeners();

        getCarListFromServer();

        initCarSpinners();

    }

    private void initCarSpinners() {

        /*String[] brand_items = new String[]{"BMW","TOYOTA","MARUTHI","TATA"};
        ArrayAdapter<String> brand_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, brand_items);
        spnr_car_brand.setAdapter(brand_adapter);

        String[] name_items = new String[]{"Swift","Polo","Indica","Innova"};
        ArrayAdapter<String> name_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, name_items);
        spnr_car_name.setAdapter(name_adapter);*/

        String[] fuel_items = new String[]{"Petrol","Diesel"};
        ArrayAdapter<String> fuel_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fuel_items);
        spnr_car_fuel.setAdapter(fuel_adapter);


        int endyear = Calendar.getInstance().get(Calendar.YEAR);
        int startyear = 1990;
        if(endyear<startyear) endyear = startyear+1;

        ArrayList<String>  years = new ArrayList<>();

        for(int i=0;i<=endyear-startyear; i++){
            years.add(Integer.toString(startyear+i));
        }

        ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spnr_car_year.setAdapter(year_adapter);
        try{
            spnr_car_year.setSelection(year_adapter.getCount()-1);
        }catch (Exception e){

        }


    }

    private void getCarListFromServer() {

            RequestQueue requestQueue = Volley.newRequestQueue(CarDetail.this);
            String URL = SERVER_ADDRESS+ GETCARSLIST;
            JSONObject jsonBody = new JSONObject();
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("vollrescat"+response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        if(!jsonObject.getString("result").equalsIgnoreCase("success")){
                            return;
                        }

                        JSONArray allData = jsonObject.getJSONArray("alldata");
                        carbrandsArr = new ArrayList<>();


                        for(int i=0; i<allData.length();i++){
                            JSONObject carBranditemJSON = allData.getJSONObject(i);
                            CarBrandItem carBrandItem = new CarBrandItem();
                            carBrandItem.carNameItems = new ArrayList<>();

                            JSONArray carNamesArr  = carBranditemJSON.getJSONArray("carnamesinbrand");
                            for(int j=0; j < carNamesArr.length(); j++){
                                String carname = carNamesArr.getJSONObject(j).getString("carname");
                                CarNameItem carNameItem = new CarNameItem();
                                carNameItem.carName = carname;
                                carBrandItem.carNameItems.add(carNameItem);
                            }

                            carBrandItem.carBrandName = carBranditemJSON.getString("carbrandname");
                            carbrandsArr.add(carBrandItem);
                        }

                        loadCarsListNow();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

    }

    private void loadCarsListNow() {
        ArrayList<String> carbrandslistSTRNG = new ArrayList<>();
        for(int k=0; k<carbrandsArr.size(); k++){
            carbrandslistSTRNG.add(carbrandsArr.get(k).carBrandName);
        }

        if(carbrandsArr.size() == 0){
            carbrandslistSTRNG.add("select");
        }
        ArrayAdapter<String> brand_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, carbrandslistSTRNG);
        spnr_car_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> carnamesarr = new ArrayList<>();
                ArrayList<CarNameItem> carnameItemsArr = new ArrayList<>();
                if(carbrandsArr.size()!=0)
                    carnameItemsArr = carbrandsArr.get(i).carNameItems;

                for(int m=0;m<carnameItemsArr.size();m++){
                    CarNameItem carNameItem = carnameItemsArr.get(m);
                    String carname = carNameItem.carName;
                    carnamesarr.add(carname);
                }

                if(carnameItemsArr.size() == 0){
                    carnamesarr.add("select");
                }

                ArrayAdapter<String> name_adapter = new ArrayAdapter<>(CarDetail.this, android.R.layout.simple_spinner_dropdown_item, carnamesarr);
                spnr_car_name.setAdapter(name_adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnr_car_brand.setAdapter(brand_adapter);




    }

    private void setClickListeners() {
        NextButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString(userCARBRAND,spnr_car_brand.getSelectedItem().toString());
                editor.putString(userCARNAME,spnr_car_name.getSelectedItem().toString());
                editor.putString(userCARFUELTYPE,spnr_car_fuel.getSelectedItem().toString());
                editor.putString(userCARYEAR,spnr_car_year.getSelectedItem().toString());
                editor.commit();

                Intent intent = new Intent(CarDetail.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initOBJECTS() {
        ButterKnife.bind(this);

        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();
    }
}
