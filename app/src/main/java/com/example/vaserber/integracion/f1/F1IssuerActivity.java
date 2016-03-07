package com.example.vaserber.integracion.f1;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.vaserber.integracion.IntegracionApplication;
import com.example.vaserber.integracion.R;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class F1IssuerActivity extends AppCompatActivity {

    private MercadoPago mercadoPago;
    private RecyclerView mView;
    private F1IssuerAdapter mAdapter;
    private String mPaymentMethodId;
    private PaymentMethod mPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        setContentView(R.layout.activity_issuer_f1);
        mView = (RecyclerView) findViewById(R.id.activity_issuer_f1_view);
        mAdapter = new F1IssuerAdapter(this);
        mView.setAdapter(mAdapter);
        mView.setLayoutManager(new LinearLayoutManager(this));
        mView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("lala", String.valueOf(position));
                        Log.d("lala", mAdapter.getItem(position).getName());
                        continueFlow(mAdapter.getItem(position));
                    }
                }));
        Intent intent = getIntent();
        mPaymentMethodId = intent.getStringExtra("payment_method_id");
        mPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("payment_method"), PaymentMethod.class);
        getIssuersList();
    }

    public void getIssuersList() {
        mercadoPago.getIssuers(mPaymentMethodId, new Callback<List<Issuer>>() {
            @Override
            public void success(List<Issuer> issuers, Response response) {

                mAdapter.addResults(issuers);

            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });
    }

    public void continueFlow(Issuer issuer) {
        Intent intent = new Intent(getApplicationContext(), F1CardFormActivity.class);
        intent.putExtra("payment_method_id", mPaymentMethodId);
        intent.putExtra("issuer_id", issuer.getId());
        intent.putExtra("payment_method",  JsonUtil.getInstance().toJson(mPaymentMethod));
        startActivity(intent);
    }
}
