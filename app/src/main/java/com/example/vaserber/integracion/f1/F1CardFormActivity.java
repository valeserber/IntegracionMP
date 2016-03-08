package com.example.vaserber.integracion.f1;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaserber.integracion.IntegracionApplication;
import com.example.vaserber.integracion.MainActivity;
import com.example.vaserber.integracion.R;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class F1CardFormActivity extends AppCompatActivity {

    private MercadoPago mercadoPago;
    private EditText mCardNumber;
    private EditText mExpiryMonth;
    private EditText mExpiryYear;
    private EditText mName;
    private Spinner mIdType;
    private EditText mIdNumber;
    private EditText mSecurityCode;
    private FrameLayout mContinueButton;

    private String mPaymentMethodId;
    private Long mIssuerId;
    private PaymentMethod mPaymentMethod;

    private ArrayAdapter<String> adapterTypeId;
    private List<String> idTypeList;
    private List<IdentificationType> mIdentificationTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mercadoPago = new MercadoPago.Builder()
                .setContext(this)
                .setPublicKey(IntegracionApplication.DUMMY_MERCHANT_PUBLIC_KEY)
                .build();

        Intent intent = getIntent();
        mPaymentMethodId = intent.getStringExtra("payment_method_id");
        if (intent.hasExtra("issuer_id")) {
            mIssuerId = intent.getLongExtra("issuer_id", -1);
        }
        mPaymentMethod = JsonUtil.getInstance().fromJson(
                this.getIntent().getStringExtra("payment_method"), PaymentMethod.class);
        setLayout();
        setListeners();
        getIdTypes();
    }

    public void setLayout() {
        setContentView(R.layout.activity_card_form);
        mCardNumber = (EditText) findViewById(R.id.activity_card_form_card_number);
        mExpiryMonth = (EditText) findViewById(R.id.activity_card_form_e_month);
        mExpiryYear = (EditText) findViewById(R.id.activity_card_form_e_year);
        mName = (EditText) findViewById(R.id.activity_card_form_name);
        mIdType = (Spinner) findViewById(R.id.activity_card_form_id_type);
        mIdNumber = (EditText) findViewById(R.id.activity_card_form_id_number);
        mSecurityCode = (EditText) findViewById(R.id.activity_card_form_security_code);
        mContinueButton = (FrameLayout) findViewById(R.id.activity_card_form_continue_button);
    }

    public void setListeners() {
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = mCardNumber.getText().toString();
                String expiryMonth = mExpiryMonth.getText().toString();
                String expiryYear = mExpiryYear.getText().toString();
                String cardholderName = mName.getText().toString();
                Integer idTypePosition = mIdType.getSelectedItemPosition();
                String idNumber = mIdNumber.getText().toString();
                String securityCode = mSecurityCode.getText().toString();

                CardToken cardToken = new CardToken(cardNumber, Integer.valueOf(expiryMonth),
                        Integer.valueOf(expiryYear), securityCode, cardholderName,
                        idTypeList.get(idTypePosition), idNumber);

                validateCardToken(cardToken, idTypePosition);
            }
        });
    }

    public void getIdTypes() {
        mercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes, Response response) {
                mIdentificationTypes = identificationTypes;
                idTypeList = new ArrayList<String>();
                for (IdentificationType i : identificationTypes) {
                    idTypeList.add(i.getId());
                }
                adapterTypeId = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, idTypeList);
                adapterTypeId.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mIdType.setAdapter(adapterTypeId);
            }

            @Override
            public void failure(RetrofitError error) {
                if ((error.getResponse() != null) && (error.getResponse().getStatus() == 404)) {

                    // No identification type for this country
                    mIdType.setVisibility(View.GONE);

                    // Set form "Go" button
//                    if (mIdNumber.getVisibility() == View.GONE) {
//                        setFormGoButton(mCardHolderName);
//                    }
                    LayoutUtil.showRegularLayout(getParent());

                } else {
                    ApiUtil.finishWithApiException(getParent(), error);
                }

            }
        });
    }

    public void validateCardToken(CardToken cardToken, int idTypePosition) {
        try {
            cardToken.validateCardNumber(this, mPaymentMethod);
            cardToken.validateSecurityCode(this, mPaymentMethod);
            if (!cardToken.validateExpiryDate())
                throw new Exception("invalid expiry date");
            if (!cardToken.validateCardholderName())
                throw new Exception("invalid cardholder name");
            if (!cardToken.validateIdentificationNumber(mIdentificationTypes.get(idTypePosition)))
                throw new Exception("invalid id type");
            continueFlow(cardToken);
        } catch(Exception e) {
            showErrorToast(e.getMessage());
        }
    }

    public void showErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void continueFlow(CardToken cardToken) {
        Intent intent = new Intent(getApplicationContext(), F1InstallmentsActivity.class);
        intent.putExtra("payment_method_id", mPaymentMethodId);
        if (mIssuerId != null) {
            intent.putExtra("issuer_id", mIssuerId);
        }
        intent.putExtra("payment_method",  JsonUtil.getInstance().toJson(mPaymentMethod));
        intent.putExtra("card_token", JsonUtil.getInstance().toJson(cardToken));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, MainActivity.F1_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }


}
