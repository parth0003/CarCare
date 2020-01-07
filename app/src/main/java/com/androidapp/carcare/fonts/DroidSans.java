package com.androidapp.carcare.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class DroidSans extends TextView {

    public DroidSans(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DroidSans(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DroidSans(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Okomito-Medium.ttf");
        setTypeface(tf , Typeface.NORMAL);

    }
}