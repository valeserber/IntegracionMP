package com.example.vaserber.integracion.f2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.example.vaserber.integracion.IntegracionApplication;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class F2Activity extends AppCompatActivity {

    protected List<String> supportedPaymentTypes = new ArrayList<String>(){{
        add("credit_card");
        add("debit_card");
        add("prepaid_card");
        add("ticket");
        add("atm");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startPaymentMethodsActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("lala", "en payment method");

                PaymentMethod paymentMethod = JsonUtil.getInstance()
                        .fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

                Log.d("lala", paymentMethod.getName());

                if (paymentMethod.isIssuerRequired()) {
                    new MercadoPago.StartActivityBuilder()
                            .setActivity(this)
                            .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                            .setPaymentMethod(paymentMethod)
                            .startIssuersActivity();
                }

            } else {
                showErrorToast(data);
            }
        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("lala", "en issuers");

                // Set issuer selection
                Issuer issuer = JsonUtil.getInstance()
                        .fromJson(data.getStringExtra("issuer"), Issuer.class);

                Log.d("lala", issuer.getName());

            } else {
                if (data != null) {
                    if (data.getStringExtra("apiException") != null) {
                        showErrorToast(data);
                    } else if (data.getBooleanExtra("backButtonPressed", false)) {
                        startPaymentMethodsActivity();
                    }
                }
            }
        }
    }

    public void startPaymentMethodsActivity() {
        // Call payment methods activity
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setSupportedPaymentTypes(supportedPaymentTypes)
                .startPaymentMethodsActivity();
    }

    public void showErrorToast(Intent data) {
        if ((data != null) && (data.getStringExtra("apiException") != null)) {
            Toast.makeText(getApplicationContext(),
                    data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
        }
    }

    public void onPaymentMethodResult() {
        
    }

}