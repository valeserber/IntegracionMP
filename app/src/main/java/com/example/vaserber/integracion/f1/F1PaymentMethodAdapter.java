package com.example.vaserber.integracion.f1;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaserber.integracion.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.ArrayList;
import java.util.List;

public class F1PaymentMethodAdapter extends RecyclerView.Adapter<F1PaymentMethodAdapter.PaymentViewHolder> {

    private Context mContext;
    private List<PaymentMethod> mPaymentList;

    public F1PaymentMethodAdapter(Context context) {
        this.mContext = context;
        this.mPaymentList = new ArrayList<>();
    }

    public void addResults(List<PaymentMethod> list) {
        mPaymentList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mPaymentList.clear();
        notifyDataSetChanged();
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.adapter_payment_method, parent, false);
        PaymentViewHolder viewHolder = new PaymentViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        PaymentMethod method = mPaymentList.get(position);
        int imageResource = MercadoPagoUtil.getPaymentMethodIcon(mContext, method.getId());

        holder.mNameTextView.setText(method.getName());
        holder.mImageView.setImageResource(imageResource);
    }

    public PaymentMethod getItem(int position) {
        return mPaymentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mPaymentList.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mNameTextView;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.adapter_payment_method_image);
            mNameTextView = (TextView) itemView.findViewById(R.id.adapter_payment_method_name);
        }
    }
}
