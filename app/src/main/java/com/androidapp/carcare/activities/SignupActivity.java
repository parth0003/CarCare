package com.androidapp.carcare.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.androidapp.carcare.utils.Constants.IsRegisteredUser;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.REGISTERUSER;
import static com.androidapp.carcare.utils.Constants.NULLDATA;
import static com.androidapp.carcare.utils.Constants.SELECT_PHOTO;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.STOREPROPIC;
import static com.androidapp.carcare.utils.Constants.cUSEREMAIL;
import static com.androidapp.carcare.utils.Constants.cUSERID;
import static com.androidapp.carcare.utils.Constants.cUSERIMGURL;
import static com.androidapp.carcare.utils.Constants.cUSERMOBNUM;
import static com.androidapp.carcare.utils.Constants.cUSERNAME;

public class SignupActivity extends AppCompatActivity {

    Context context;
    Activity thisactivity;
    Uri selectedImageURI = null;
    public Bitmap yourSelectedImage;

    byte[] byteArrayImage;
    byte[] fileData;
    String fileName = ""; // The name of the file you are uploading
    String contentType = ""; // The content type of the file

    Validator validator;

    @BindView(R.id.img_pro_pic)
    CircleImageView proPic;

    @BindView(R.id.profile_pic_lay)
    RelativeLayout ProPicLay;


    @NotEmpty
    @BindView(R.id.full_name)
    EditText FullName;

    @NotEmpty
    @BindView(R.id.mobile_number)
    EditText MobileNumber;

    @Email
    @BindView(R.id.email_address)
    EditText EmailAddress;


    @BindView(R.id.password)
    EditText Password;

    @BindView(R.id.enterpass_card)
    CardView PasswordCard;

    @BindView(R.id.reenter_card)
    CardView ReenterCard;

    @BindView(R.id.confirm_password)
    EditText ConfirmPassword;

    @BindView(R.id.signup_card_butn)
    CardView SignUpButn;


    Intent intent;

    boolean isSocialSignup;

    String USERID="unset", USERNAME, USEREMAIL, USERPHNO, USERPICURL;


    public Dialog dp;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initiateObjs();
        setClickListeners();
    }

    private void initiateObjs() {
        context = SignupActivity.this;
        thisactivity = SignupActivity.this;
        ButterKnife.bind(this);
        validator = new Validator(this);
        mAuth = FirebaseAuth.getInstance();

        intent = getIntent();
        isSocialSignup = intent.getBooleanExtra("SOCIAL",false);
        if(isSocialSignup)
        loadSocialElements();

        dp=new ProgressDialog(context);
        ((ProgressDialog) dp).setMessage("please wait..");

        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

    }

    private void loadSocialElements() {
        USERNAME = intent.getStringExtra("USERNAME");
        USEREMAIL = intent.getStringExtra("USEREMAIL");
        USERID = intent.getStringExtra("USERID");
        USERPHNO = intent.getStringExtra("USERPHNO");
        USERPICURL = intent.getStringExtra("USERPICURL");

        if(!USERNAME.equalsIgnoreCase(NULLDATA)) FullName.setText(USERNAME);
        if(!USEREMAIL.equalsIgnoreCase(NULLDATA)) EmailAddress.setText(USEREMAIL);

        if(!USERPHNO.equalsIgnoreCase(NULLDATA)) MobileNumber.setText(USERPHNO);
        if(!USERPICURL.equalsIgnoreCase(NULLDATA))
        Glide.with(SignupActivity.this).load(USERPICURL).into(proPic);


        PasswordCard.setVisibility(View.GONE);
        ReenterCard.setVisibility(View.GONE);


    }


    private void setClickListeners() {

        ProPicLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        SignUpButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.setValidationListener(new Validator.ValidationListener() {
                    @Override
                    public void onValidationSucceeded() {
                        onFieldsValidated();
                    }

                    @Override
                    public void onValidationFailed(List<ValidationError> errors) {
                        String error = errors.get(0).getCollatedErrorMessage(getApplicationContext());
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                        View view1 = errors.get(0).getView();

                        view1.startAnimation(AnimationUtils.loadAnimation(SignupActivity.this,R.anim.shake));
                        view1.requestFocus();

                    }
                });
                validator.validate();


            }
        });
    }

    private FirebaseAuth mAuth;
    private void onFieldsValidated() {

        if(isSocialSignup){

            pushpropic pushpropic = new pushpropic();
            pushpropic.execute();



        }else {
            PasswordCard.setVisibility(View.VISIBLE);
            ReenterCard.setVisibility(View.VISIBLE);

            if(!Password.getText().toString().equalsIgnoreCase("")){
                if(Password.getText().toString().equals(ConfirmPassword.getText().toString())){

                }else{
                    Toast.makeText(getApplicationContext(),"passwords din't match",Toast.LENGTH_LONG).show();
                    return;
                }
            }else{
                Password.startAnimation(AnimationUtils.loadAnimation(SignupActivity.this,R.anim.shake));
                Password.requestFocus();
                Toast.makeText(getApplicationContext(),"password shouldn't be empty",Toast.LENGTH_LONG).show();
                return;
            }

            dp.show();
            dp.setCancelable(false);
            mAuth.createUserWithEmailAndPassword(EmailAddress.getText().toString(), Password.getText().toString()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "failed: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    dp.dismiss();
                }
            }).addOnCompleteListener(thisactivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();

                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        USERID = uid;


                        dp.dismiss();
                        pushpropic pushpropic = new pushpropic();
                        pushpropic.execute();
                    }
                }


            });

        }


    }


    private File bitmaptoFile(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // connect to the backend
    class pushpropic extends AsyncTask<Void, Void, Void> {

        // replace this upload url and direct it to Node.js server and save it in shared perferences


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dp.show();
            if(yourSelectedImage==null){
                yourSelectedImage = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_profile);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String STORE_PROFILE_PIC_URL = SERVER_ADDRESS + STOREPROPIC;

            File file = bitmaptoFile(yourSelectedImage, "profileImage");


            RequestParams params = new RequestParams();
            params.put("userid", USERID);
            try {
                params.put("icon", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            SyncHttpClient client = new SyncHttpClient();
            client.setTimeout(6000000);
            // client.addHeader("Content-Type","application/x-www-form-urlencoded");
            client.post(STORE_PROFILE_PIC_URL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {

                    try {
                        if (file != null) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("Res for store propic:", response.toString());

                    try {
                        boolean success = response.getString("result").equals("success");
                        if (success) {
                            String pppurl = response.getString("pictureURL");
                            dp.dismiss();
                            registerUsernow(pppurl);
                        } else {
                            dp.dismiss();
                            System.out.println("server is unable to save the  picture");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        if (file != null) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    try {
                        if (file != null) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }



    }

    private void registerUsernow(String propicURL) {
        dp.show();
       try {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String URL = SERVER_ADDRESS+ REGISTERUSER;
                JSONObject jsonBody = new JSONObject();
                jsonBody.put(cUSERID, USERID);
                jsonBody.put(cUSERNAME, FullName.getText().toString());
                jsonBody.put(cUSERMOBNUM, MobileNumber.getText().toString());
                jsonBody.put(cUSEREMAIL, EmailAddress.getText().toString());
                jsonBody.put(cUSERIMGURL,propicURL);



                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dp.dismiss();
                        System.out.println("vollrescat"+response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String result = jsonObject.getString("result");

                            if(result.equalsIgnoreCase("success")){

                                editor.putString(cUSERID, USERID);
                                editor.putString(cUSERNAME, FullName.getText().toString());
                                editor.putString(cUSERMOBNUM, MobileNumber.getText().toString());
                                editor.putString(cUSEREMAIL, EmailAddress.getText().toString());
                                editor.putString(cUSERIMGURL,propicURL);

                                editor.putBoolean(IsRegisteredUser,true);

                                editor.commit();


                                Toast.makeText(getApplicationContext(),"Profile Saved",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else
                                Toast.makeText(getApplicationContext(),"Failed to save profile",Toast.LENGTH_LONG).show();

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    selectedImageURI = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageURI);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap decoded = BitmapFactory.decodeStream(imageStream);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    decoded.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    yourSelectedImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

                        proPic.setImageBitmap(yourSelectedImage);

                    byteArrayImage = out.toByteArray();

                    fileData = byteArrayImage;

                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
                    String currenttime=simpleDateFormat.format(new Date());

                    fileName=USERID+currenttime+".jpg";
                    contentType="image/jpeg";

                    //pppurl="https://static."+SERVER_ADDRESS+"/file/app-media/"+useridd+currenttime+".jpg";

                    //String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                }
        }


    }
}
