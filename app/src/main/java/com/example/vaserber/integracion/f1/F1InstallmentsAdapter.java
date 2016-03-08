package com.example.vaserber.integracion.f1;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaserber.integracion.R;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;

import java.util.ArrayList;
import java.util.List;

public class F1InstallmentsAdapter extends
        RecyclerView.Adapter<F1InstallmentsAdapter.InstallmentsViewHolder> {

    private Context mContext;
    private List<PayerCost> mInstallmentsList;

    public F1InstallmentsAdapter(Context context) {
        this.mContext = context;
        this.mInstallmentsList = new ArrayList<>();
    }

    public void addResults(List<PayerCost> list) {
        mInstallmentsList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mInstallmentsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public InstallmentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.adapter_installments, parent, false);
        InstallmentsViewHolder viewHolder = new InstallmentsViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InstallmentsViewHolder holder, int position) {
        PayerCost payerCost = mInstallmentsList.get(position);

        holder.mTextView.setText(payerCost.getRecommendedMessage());
    }

    public PayerCost getItem(int position) {
        return mInstallmentsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mInstallmentsList.size();
    }

    public static class InstallmentsViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public InstallmentsViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.adapter_installments_text);
        }
    }

}
