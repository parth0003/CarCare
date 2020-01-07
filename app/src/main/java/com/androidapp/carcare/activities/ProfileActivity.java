package com.androidapp.carcare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.androidapp.carcare.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.androidapp.carcare.utils.Constants.NULLDATA;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.cUSEREMAIL;
import static com.androidapp.carcare.utils.Constants.cUSERID;
import static com.androidapp.carcare.utils.Constants.cUSERIMGURL;
import static com.androidapp.carcare.utils.Constants.cUSERMOBNUM;
import static com.androidapp.carcare.utils.Constants.cUSERNAME;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.img_pro_pic)
    CircleImageView proPic;

    @BindView(R.id.profile_pic_lay)
    RelativeLayout ProPicLay;

    @BindView(R.id.full_name)
    EditText FullName;


    @BindView(R.id.mobile_number)
    EditText MobileNumber;

    @Email
    @BindView(R.id.email_address)
    EditText EmailAddress;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String USERID="unset", USERNAME, USEREMAIL, USERPHNO, USERPICURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initOBJECTS();
        loadSocialElements();
    }

    private void initOBJECTS() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();
    }


    private void loadSocialElements() {
        USERNAME = preferences.getString(cUSERNAME,NULLDATA);
        USEREMAIL = preferences.getString(cUSEREMAIL,NULLDATA);
        USERID = preferences.getString(cUSERID,NULLDATA);
        USERPHNO = preferences.getString(cUSERMOBNUM,NULLDATA);
        USERPICURL = preferences.getString(cUSERIMGURL,NULLDATA);

        if(!USERNAME.equalsIgnoreCase(NULLDATA)) FullName.setText(USERNAME);
        if(!USEREMAIL.equalsIgnoreCase(NULLDATA)) EmailAddress.setText(USEREMAIL);

        if(!USERPHNO.equalsIgnoreCase(NULLDATA)) MobileNumber.setText(USERPHNO);
        if(!USERPICURL.equalsIgnoreCase(NULLDATA))
            Glide.with(ProfileActivity.this).load(SERVER_ADDRESS+USERPICURL).apply(new RequestOptions().placeholder(R.drawable.profile)
            ).into(proPic);


    }
}
