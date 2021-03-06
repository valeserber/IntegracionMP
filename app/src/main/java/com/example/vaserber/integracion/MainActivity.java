package com.example.vaserber.integracion;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.vaserber.integracion.f1.F1PaymentMethodActivity;
import com.example.vaserber.integracion.f2.F2Activity;
import com.example.vaserber.integracion.f3.F3Activity;
import com.mercadopago.NewCardActivity;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;


public class MainActivity extends AppCompatActivity {

    private FrameLayout f1Button;
    private FrameLayout f2Button;
    private FrameLayout f3Button;
    private FrameLayout f4Button;

    public static int F1_REQUEST = 1;
    public static int F2_REQUEST = 2;
    public static int F3_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f1Button = (FrameLayout) findViewById(R.id.activity_main_f1);
        f2Button = (FrameLayout) findViewById(R.id.activity_main_f2);
        f3Button = (FrameLayout) findViewById(R.id.activity_main_f3);
        f4Button = (FrameLayout) findViewById(R.id.activity_main_f4);

        f1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), F1PaymentMethodActivity.class);
                startActivityForResult(intent, F1_REQUEST);
            }
        });

        f2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), F2Activity.class);
                startActivityForResult(intent, F2_REQUEST);
            }
        });

        f3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), F3Activity.class);
                startActivityForResult(intent, F3_REQUEST);
            }
        });
        f4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == F1_REQUEST) {

            onF1Result(resultCode, data);

        } else if (requestCode == F2_REQUEST) {

            onF2Result(resultCode, data);

        } else if (requestCode == F3_REQUEST) {

            onF3Result(resultCode, data);

        }
    }

    private void onF1Result(int resultCode, Intent data) {
        onF2Result(resultCode, data);
    }

    private void onF2Result(int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {

            String paymentTypeId = data.getStringExtra("payment_type_id");
            String paymentMethodId = data.getStringExtra("payment_method_id");
            String issuer;
            if (data.hasExtra("issuer_id")) {
                Long issuerId = data.getLongExtra("issuer_id", -1);
                issuer = String.valueOf(issuerId);
            } else {
                issuer = "not required";
            }
            Integer installments = null;
            if (data.hasExtra("installments")) {
                installments = data.getIntExtra("installments", -1);
            }
            String token = data.getStringExtra("token");

            showResults(paymentTypeId, paymentMethodId, issuer, String.valueOf(installments), token);

        }
    }

    private void onF3Result(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Integer installments = (data.getStringExtra("installments") != null)
                    ? Integer.parseInt(data.getStringExtra("installments")) : null;

            String token = data.getStringExtra("token");

            PaymentMethod paymentMethod = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            String issuer;
            if (paymentMethod.isIssuerRequired()) {
                Long issuerId = (data.getStringExtra("issuerId") != null)
                        ? Long.parseLong(data.getStringExtra("issuerId")) : null;
                issuer = String.valueOf(issuerId);
            } else {
                issuer = "not required";
            }

           showResults(paymentMethod.getPaymentTypeId(), paymentMethod.getId(),
                   issuer, String.valueOf(installments), token);
        }
    }

    private void showResults(String paymentTypeId, String paymentMethodId, String issuer,
                             String installments, String token) {
        Toast.makeText(getApplicationContext(),
                "Payment type: " + paymentTypeId +
                        ", Payment method: " + paymentMethodId +
                        ", Issuer: " + issuer +
                        ", Installments: " + installments +
                        ", Token: " + token, Toast.LENGTH_LONG).show();
    }


}
