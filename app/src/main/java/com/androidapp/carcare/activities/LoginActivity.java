package com.androidapp.carcare.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.androidapp.carcare.utils.Constants.GETAUSERINFO;
import static com.androidapp.carcare.utils.Constants.IsRegisteredUser;
import static com.androidapp.carcare.utils.Constants.NULLDATA;
import static com.androidapp.carcare.utils.Constants.PERMISSION_ACCESS_COARSE_LOCATION;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;


public class LoginActivity extends AppCompatActivity {
    public Dialog dp;

    Context context;
    private EditText EDT_userid,EDT_password;
    RelativeLayout progressBar;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ProgressDialog progressDialog;

    LinearLayout BTN_Google, BTN_FaceBook;

    private FirebaseAuth mAuth;
    private LoginManager fbLoginMan;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    String USERID, USERNAME, USEREMAIL, USERPHNO, USERPICURL;

    LinearLayout signupLinLay;
    CardView Login_Lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initOBJECTS();


        setViewIDs();

        initView();

        autoLogin();
        thiscontext = LoginActivity.this;
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    Activity thiscontext;
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(thiscontext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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

    private void initOBJECTS() {
        context = LoginActivity.this;
        FacebookSdk.setApplicationId(getString(R.string.facebook_appid));
        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        dp=new ProgressDialog(LoginActivity.this);
        ((ProgressDialog) dp).setMessage("please wait..");
//        dp.show();
//        dp.setCancelable(false);


    }

    private void initView() {
        FacebookSdk.sdkInitialize(LoginActivity.this);
        fbLoginMan = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    private void setViewIDs(){
        EDT_userid = (EditText)findViewById(R.id.userid);
        EDT_password = (EditText)findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.GONE);

        BTN_Google = findViewById(R.id.ll_google);
        BTN_FaceBook = findViewById(R.id.ll_facebook);

        BTN_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        BTN_FaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFB();
            }
        });
        signupLinLay = findViewById(R.id.signup_LinLay);
        signupLinLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                intent.putExtra("SOCIAL",false);
                startActivity(intent);
                finish();
            }
        });

        Login_Lay = findViewById(R.id.log_in_button);
        Login_Lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EDT_userid.getText().toString().equals("") || EDT_password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"please enter valid email and password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(EDT_userid.getText().toString(),EDT_password.getText().toString()).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();


                            USERID = user.getUid();
                            USERNAME = user.getDisplayName();
                            USEREMAIL = user.getEmail();
                            USERPHNO = user.getPhoneNumber();
                            USERPICURL = user.getPhotoUrl()!=null? user.getPhotoUrl().toString(): null;

                            gocheckusernow(1, user);


                        }
                        else Toast.makeText(getApplicationContext(),"Failed to Sign In",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void loginFB() {
        fbLoginMan.getInstance().logOut();
        fbLoginMan.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email",
                "user_birthday", "public_profile"));
        progressDialog = ProgressDialog.show(LoginActivity.this, "Signing in..", "Connecting...", true, false);

        fbLoginMan.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = loginResult.getAccessToken();
                        //Profile profile = Profile.getCurrentProfile();
                        handleFacebookAccessToken(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(),"Error Logging in.",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
        fbLoginMan.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, final GraphResponse response) {
                        // Application code
                        //    Methods.showToast(LoginActivity.this, "" + response.toString());
                        try {
                            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                // Sign in success, update UI with the signed-in user's information
//                                                Log.d(TAG, "signInWithCredential:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
//
                                                USERID = user.getUid();
                                                USERNAME = user.getDisplayName();
                                                USEREMAIL = user.getEmail();
                                                USERPHNO = user.getPhoneNumber();
                                                USERPICURL = user.getPhotoUrl()!=null? user.getPhotoUrl().toString(): null;

                                                gocheckusernow(0,user);



                                            } else {
                                                progressDialog.dismiss();
                                                // If sign in fails, display a message to the user.
//                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();

                                            }

                                            // ...
                                        }
                                    });

                        } catch (Exception e) {
//                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();

    }


    private void gocheckusernow(int firebaseDEL, FirebaseUser user) {
        System.out.println("userdata:"+USERNAME+USERID+USEREMAIL+USERPICURL+USERPHNO);
        if(USERNAME==null) USERNAME = NULLDATA;
        if(USEREMAIL==null) USEREMAIL = NULLDATA;
        if(USERID==null) USERID = NULLDATA;
        if(USERPICURL==null) USERPICURL = NULLDATA;
        if(USERPHNO==null) USERPHNO = NULLDATA;

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = SERVER_ADDRESS+ GETAUSERINFO;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("uid", USERID);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("vollrescat"+response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("desc").equalsIgnoreCase("new user")){


                                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                intent.putExtra("SOCIAL",true);
                                intent.putExtra("USERID",USERID);
                                intent.putExtra("USERNAME",USERNAME);
                                intent.putExtra("USEREMAIL",USEREMAIL);
                                intent.putExtra("USERPHNO",USERPHNO);
                                intent.putExtra("USERPICURL",USERPICURL);
                                startActivity(intent);
                                finish();






                        }
                        else{
                            Intent intent = new Intent(context,RestoreAccount.class);
                            intent.putExtra("USERID",USERID);
                            startActivity(intent);
                            finish();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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
                        System.out.println(responseString+"serverres");
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                handleSignInResult(task);
                System.out.println("userdata google signed in");

            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //  getIdToken
            if (account != null) {
                firebaseAuthWithGoogle(account);
                System.out.println("userdata firebase signed in");
            }

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("", "signInResult:failed code=" + e.getStatusCode());

        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog = ProgressDialog.show(LoginActivity.this, "Signing in..", "Connecting...", true, false);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            System.out.println("userdatanotfetchedyet");

                            USERID = user.getUid();
                            USERNAME = user.getDisplayName();
                            USEREMAIL = user.getEmail();
                            USERPHNO = user.getPhoneNumber();
                            USERPICURL = user.getPhotoUrl()!=null? user.getPhotoUrl().toString(): null;

                            gocheckusernow(0,user);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Sign In Failed, check Credentials",Toast.LENGTH_LONG).show();
                            // If sign in fails, display a message to the user.
                            /*AppLogs.w(TAG, "signInWithEmail:failure", task.getException());
                            if(task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                Methods.showToast(LoginActivity.this, "Password is incorrect, please enter correct password or reset the password");
                            }else if(task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                Methods.showToast(LoginActivity.this, "Unknown Username");
                            }*/
                        }
                        // ...
                    }

                });
    }


    private void autoLogin(){
        boolean isRegistered = preferences.getBoolean(IsRegisteredUser,false);


        if(isRegistered){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


    }


}

