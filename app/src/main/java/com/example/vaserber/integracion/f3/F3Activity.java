package com.example.vaserber.integracion.f3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.vaserber.integracion.IntegracionApplication;
import com.mercadopago.core.MercadoPago;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class F3Activity extends AppCompatActivity {

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
        startVaultActivity();
    }


    public void startVaultActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setAmount(BigDecimal.valueOf(100))
                .setSupportedPaymentTypes(supportedPaymentTypes)
                .startVaultActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MercadoPago.VAULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                setResult(Activity.RESULT_OK, data);
                finish();

            } else {
                if ((data != null) && (data.getStringExtra("apiException") != null)) {
                    Toast.makeText(getApplicationContext(),
                            data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
