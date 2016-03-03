package com.example.vaserber.integracion;


import android.app.Application;
import android.content.Context;

import com.mercadopago.core.MercadoPago;

public class IntegracionApplication extends Application {

    public static IntegracionApplication sApplication;
    public static Context sContext;
    public static MercadoPago mercadoPago;

    public static String DUMMY_MERCHANT_PUBLIC_KEY = "444a9ef5-8a6b-429f-abdf-587639155d88";


    public IntegracionApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        sContext = getApplicationContext();
        mercadoPago = new MercadoPago.Builder()
                .setContext(sContext)
                .setPublicKey(DUMMY_MERCHANT_PUBLIC_KEY)
                .build();
    }
}
