package com.example.vaserber.integracion.f2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.example.vaserber.integracion.IntegracionApplication;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class F2Activity extends AppCompatActivity {

    private PaymentMethod mPaymentMethod;
    private Issuer mIssuer;
    private CardToken mCardToken;
    private List<PayerCost> mPayerCosts;
    private String mBin;
    private PayerCost mSelectedPayerCost;
    private Token mToken;


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


    public void showErrorToast(Intent data) {
        if ((data != null) && (data.getStringExtra("apiException") != null)) {
            Toast.makeText(getApplicationContext(),
                    data.getStringExtra("apiException"), Toast.LENGTH_LONG).show();
        }
    }

    public void pay() {
        Intent intent = new Intent();
        intent.putExtra("payment_type_id", mPaymentMethod.getPaymentTypeId());
        intent.putExtra("payment_method_id", mPaymentMethod.getId());
        if (mIssuer != null) {
            intent.putExtra("issuer_id", mIssuer.getId());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {

            onPaymentMethodResult(resultCode, data);

        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {

            onIssuersResult(resultCode, data);

        } else if (requestCode == MercadoPago.NEW_CARD_REQUEST_CODE) {

            onNewCardResult(resultCode, data);

        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {

            onInstallmentsResult(resultCode, data);

        }
    }

    public void onPaymentMethodResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            mPaymentMethod = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            if(MercadoPagoUtil.isCardPaymentType(mPaymentMethod.getPaymentTypeId())) {

                if (mPaymentMethod.isIssuerRequired()) {
                    startIssuersActivity();
                } else {
                    startNewCardActivity();
                }
            } else {
                pay();
            }

        } else {
            showErrorToast(data);
        }
    }

    public void onIssuersResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            mIssuer = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra("issuer"), Issuer.class);

            startNewCardActivity();

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

    public void onNewCardResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mCardToken = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra("cardToken"), CardToken.class);

            mBin = mCardToken.getCardNumber().substring(0, 6);

            if (mCardToken.validateSecurityCode()) {
                createTokenAsync();
            }

        } else {
            if (data != null) {
                if (data.getStringExtra("apiException") != null) {
                    showErrorToast(data);
                }
            }
        }
    }


    public void onInstallmentsResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            mSelectedPayerCost = JsonUtil.getInstance()
                    .fromJson(data.getStringExtra("payerCost"), PayerCost.class);

            pay();

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                showErrorToast(data);
            }
        }
    }


    public void startPaymentMethodsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setSupportedPaymentTypes(supportedPaymentTypes)
                .startPaymentMethodsActivity();
    }

    public void startIssuersActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setPaymentMethod(mPaymentMethod)
                .startIssuersActivity();
    }

    public void startNewCardActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setPaymentMethod(mPaymentMethod)
                .startNewCardActivity();
    }

    public void startInstallmentsActivity() {
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPayerCosts(mPayerCosts)
                .startInstallmentsActivity();
    }

    private void createTokenAsync() {
        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .setContext(this)
                .build();

        mercadoPago.createToken(mCardToken, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                mToken = token;
                getPayerCosts();
            }

            @Override
            public void failure(RetrofitError error) {
                ApiUtil.finishWithApiException(getParent(), error);
            }
        });
    }

    public void getPayerCosts() {
        Long issuerId = (mIssuer != null ? mIssuer.getId() : null);
        new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .build()
                .getInstallments(mBin,
                        BigDecimal.valueOf(100),
                        issuerId,
                        mPaymentMethod.getId(),
                        new Callback<List<Installment>>() {
                            @Override
                            public void success(List<Installment> installments, Response response) {

                                if ((installments.size() > 0)
                                        && (installments.get(0).getPayerCosts().size() > 0)) {
                                    //unico plan de cuotas para ese banco y ese metodo de pago
                                    mPayerCosts = installments.get(0).getPayerCosts();
                                    startInstallmentsActivity();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                ApiUtil.finishWithApiException(getParent(), error);
                            }
                        });
    }

}