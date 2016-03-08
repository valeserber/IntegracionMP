package com.example.vaserber.integracion.f1;


import android.app.Activity;
import android.content.Intent;
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
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Installment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class F1InstallmentsActivity extends AppCompatActivity {

    private MercadoPago mercadoPago;
    private RecyclerView mView;
    private F1InstallmentsAdapter mAdapter;
    private String mPaymentMethodId;
    private PaymentMethod mPaymentMethod;
    private Long mIssuerId;
    private CardToken mCardToken;
    private List<PayerCost> mPayerCosts;
    private Token mToken;
    private PayerCost mSelectedPayerCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        setContentView(R.layout.activity_installments_f1);
        mView = (RecyclerView) findViewById(R.id.activity_installments_f1_view);
        mAdapter = new F1InstallmentsAdapter(this);
        mView.setAdapter(mAdapter);
        mView.setLayoutManager(new LinearLayoutManager(this));

        mView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mSelectedPayerCost = mPayerCosts.get(position);
                        getToken(position);
                    }
                }));

        Intent intent = getIntent();
        mPaymentMethodId = intent.getStringExtra("payment_method_id");
        if (intent.hasExtra("issuer_id")) {
            mIssuerId = intent.getLongExtra("issuer_id", -1);
        }
        mPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("payment_method"), PaymentMethod.class);
        mCardToken = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("card_token"), CardToken.class);

        getInstallments();
    }

    public void getInstallments() {
        String bin = mCardToken.getCardNumber().substring(0, 6);
        BigDecimal amount = BigDecimal.valueOf(100);
        mercadoPago.getInstallments(bin, amount, mIssuerId, mPaymentMethod.getId(),
                new Callback<List<Installment>>() {
            @Override
            public void success(List<Installment> installments, Response response) {
                if ((installments.size() > 0) && (installments.get(0).getPayerCosts().size() > 0)) {
                    // Set installments card data and visibility
                    mPayerCosts = installments.get(0).getPayerCosts();
                    //get the FIRST element of the installments list
                    mAdapter.addResults(mPayerCosts);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "invalid payment method for current amount", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });
    }

    public void getToken(int position) {
        mercadoPago.createToken(mCardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                pay();
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });
    }

    public void pay() {
        Intent intent = new Intent();
        intent.putExtra("payment_type_id", mPaymentMethod.getPaymentTypeId());
        intent.putExtra("payment_method_id", mPaymentMethod.getId());
        if (mIssuerId != null) {
            intent.putExtra("issuer_id", mIssuerId);
        }
        if (mSelectedPayerCost != null) {
            intent.putExtra("installments", mSelectedPayerCost.getInstallments());
        }
        if (mToken != null) {
            intent.putExtra("token", mToken.getId());
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
