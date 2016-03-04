package com.example.vaserber.integracion.f1;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vaserber.integracion.IntegracionApplication;
import com.example.vaserber.integracion.R;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class F1PaymentMethodActivity extends AppCompatActivity {

    private MercadoPago mercadoPago;
    private RecyclerView mView;
    private F1PaymentMethodAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        setContentView(R.layout.activity_payment_method_f1);

        mView = (RecyclerView) findViewById(R.id.activity_payment_method_f1_view);
        mAdapter = new F1PaymentMethodAdapter(this);
        mView.setAdapter(mAdapter);
        mView.setLayoutManager(new LinearLayoutManager(this));
        mView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("lala", String.valueOf(position));
                        Log.d("lala", mAdapter.getItem(position).getName());
                    }
                }));
        getPaymentMethods();
    }

    public void getPaymentMethods() {
        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods, Response response) {

                mAdapter.addResults(paymentMethods);
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });
    }

    public void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


}
