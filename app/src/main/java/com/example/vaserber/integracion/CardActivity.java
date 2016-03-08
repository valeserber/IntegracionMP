package com.example.vaserber.integracion;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;


public class CardActivity extends AppCompatActivity {

    private FrameLayout mBaseCard;
    private GradientDrawable mBaseDrawableCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_form);

        mBaseCard = (FrameLayout) findViewById(R.id.activity_new_card_form_color_card);
        mBaseDrawableCard = (GradientDrawable) mBaseCard.getBackground();

        changeBaseCardColor();
    }

    public void changeBaseCardColor() {
        mBaseDrawableCard.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
}
