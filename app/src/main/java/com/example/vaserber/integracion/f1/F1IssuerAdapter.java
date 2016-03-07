package com.example.vaserber.integracion.f1;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaserber.integracion.R;
import com.mercadopago.model.Issuer;

import java.util.ArrayList;
import java.util.List;

public class F1IssuerAdapter extends RecyclerView.Adapter<F1IssuerAdapter.IssuerViewHolder> {

    private Context mContext;
    private List<Issuer> mIssuerList;

    public F1IssuerAdapter(Context context) {
        this.mContext = context;
        this.mIssuerList = new ArrayList<>();
    }

    public void addResults(List<Issuer> list) {
        mIssuerList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mIssuerList.clear();
        notifyDataSetChanged();
    }

    @Override
    public IssuerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View adapterView = inflater.inflate(R.layout.adapter_issuer, parent, false);
        IssuerViewHolder viewHolder = new IssuerViewHolder(adapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IssuerViewHolder holder, int position) {
        Issuer issuer = mIssuerList.get(position);

        holder.mNameTextView.setText(issuer.getName());
    }

    public Issuer getItem(int position) {
        return mIssuerList.get(position);
    }

    @Override
    public int getItemCount() {
        return mIssuerList.size();
    }

    public static class IssuerViewHolder extends RecyclerView.ViewHolder {

        public TextView mNameTextView;

        public IssuerViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.adapter_issuer_name);
        }
    }

}
