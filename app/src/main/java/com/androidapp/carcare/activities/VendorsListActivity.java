package com.androidapp.carcare.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

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
import com.androidapp.carcare.adapters.VendorsListAdapter;
import com.androidapp.carcare.datamodels.VendorItem;
import com.androidapp.carcare.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.androidapp.carcare.utils.Constants.GETVENDORSFORSERVICE;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.UNDEFINED;
import static com.androidapp.carcare.utils.Constants.db_vendorcity;
import static com.androidapp.carcare.utils.Constants.db_vendoremailid;
import static com.androidapp.carcare.utils.Constants.db_vendorid;
import static com.androidapp.carcare.utils.Constants.db_vendorimgurl;
import static com.androidapp.carcare.utils.Constants.db_vendorloclat;
import static com.androidapp.carcare.utils.Constants.db_vendorloclong;
import static com.androidapp.carcare.utils.Constants.db_vendormobnum;
import static com.androidapp.carcare.utils.Constants.db_vendorname;
import static com.androidapp.carcare.utils.Constants.db_vendortitle;

public class VendorsListActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.vendors_recview)
    RecyclerView v_VendorsRecyclerView;

    @BindView(R.id.search_view)
    SearchView searchView;

    ArrayList<VendorItem> VendorsArrList;

    VendorsListAdapter vendorsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_list);
        initOBJECTS();
        setClickListeners();
        getVendorsListData();
    }



    private void initOBJECTS() {
        ButterKnife.bind(this);
        context = VendorsListActivity.this;
    }

    private void setClickListeners() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                try{


                if(VendorsArrList==null || vendorsListAdapter==null) return false;
                    vendorsListAdapter.getFilter().filter(query);
                return false;
                }catch (Exception e){
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(VendorsArrList==null || vendorsListAdapter==null) return false;
                    vendorsListAdapter.getFilter().filter(newText);

                return false;
            }
        });

    }


    private void getVendorsListData() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = SERVER_ADDRESS + GETVENDORSFORSERVICE;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("info", "Request from mob");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("vollrescat" + response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equalsIgnoreCase("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("alldata");

                            VendorsArrList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsObj = jsonArray.getJSONObject(i);
                                String vendorid = jsObj.getString(db_vendorid);
                                String vendorname = jsObj.getString(db_vendorname);
                                String vendortitle = jsObj.getString(db_vendortitle);

                                String vendorimgurl = jsObj.getString(db_vendorimgurl);
                                String vendormobnum = jsObj.getString(db_vendormobnum);
                                String vendoremailid = jsObj.getString(db_vendoremailid);

                                String vendorloclat = jsObj.getString(db_vendorloclat);
                                String vendorloclong = jsObj.getString(db_vendorloclong);
                                String vendorcity = jsObj.getString(db_vendorcity);


                                if (vendorid.equalsIgnoreCase("")) vendorid = UNDEFINED;
                                if (vendorname.equalsIgnoreCase("")) vendorname = UNDEFINED;
                                if (vendortitle.equalsIgnoreCase("")) vendortitle = UNDEFINED;

                                if (vendorimgurl.equalsIgnoreCase("")) vendorimgurl = UNDEFINED;
                                if (vendormobnum.equalsIgnoreCase("")) vendormobnum = UNDEFINED;
                                if (vendoremailid.equalsIgnoreCase("")) vendoremailid = UNDEFINED;

                                if (vendorloclat.equalsIgnoreCase("")) vendorloclat = UNDEFINED;
                                if (vendorloclong.equalsIgnoreCase("")) vendorloclong = UNDEFINED;
                                if (vendorcity.equalsIgnoreCase("")) vendorcity = UNDEFINED;

                                VendorItem vendorItem = new VendorItem();

                                vendorItem.setVendorid(vendorid);
                                vendorItem.setVendorname(vendorname);
                                vendorItem.setVendortitle(vendortitle);

                                vendorItem.setVendorimgurl(vendorimgurl);
                                vendorItem.setVendormobnum(vendormobnum);
                                vendorItem.setVendoremailid(vendoremailid);

                                vendorItem.setVendorloclat(vendorloclat);
                                vendorItem.setVendorloclong(vendorloclong);
                                vendorItem.setVendorcity(vendorcity);

                                VendorsArrList.add(vendorItem);

                                System.out.println("vollrescatobj3"+vendorItem.getVendorid());

                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    nowShowTheAvailableVendorsList();
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

    private void nowShowTheAvailableVendorsList() {

        v_VendorsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //v_VendorsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        v_VendorsRecyclerView.setLayoutManager(linearLayoutManager);


        vendorsListAdapter = new VendorsListAdapter(VendorsArrList,context);
        v_VendorsRecyclerView.setAdapter(vendorsListAdapter);
    }
}
