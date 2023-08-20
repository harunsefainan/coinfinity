package com.example.cryptocurrencyapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptocurrencyapp.Activity.HomeActivity;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterCryptoPercent extends RecyclerView.Adapter<AdapterCryptoPercent.ViewHolder> {

    private ArrayList<ModelCrypto> modelCryptos;
    private View.OnClickListener onClickListener;
    HomeActivity homeActivity;
    private static DecimalFormat df2 = new DecimalFormat("#.####");

    public AdapterCryptoPercent(ArrayList<ModelCrypto> modelCryptos) {
        this.modelCryptos = modelCryptos;

    }

    public void filterList(ArrayList<ModelCrypto> filteredList) {
        modelCryptos = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AdapterCryptoPercent.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crypto_percent_item, parent, false);
        return new AdapterCryptoPercent.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelCrypto modelCrypto = modelCryptos.get(position);
        holder.currencyNameTv.setText(modelCrypto.getName());
        holder.symbolTv.setText(modelCrypto.getSymbol());
        holder.percentTv.setText(df2.format(modelCrypto.getPercent())+" %");
        holder.rateTv.setText("$ " + df2.format(modelCrypto.getPrice()));

        Glide.with(holder.image).load(
                "https://s2.coinmarketcap.com/static/img/coins/64x64/" + modelCrypto.getId() + ".png"
        ).into(holder.image);

        Glide.with(holder.image2).load(
                "https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + modelCrypto.getId() + ".png"
        ).into(holder.image2);

        if (modelCrypto.getPercent()>0){
            holder.percentTv.setTextColor(ContextCompat.getColor(holder.percentTv.getContext(), R.color.green));
        }else {
            holder.percentTv.setTextColor(ContextCompat.getColor(holder.percentTv.getContext(), R.color.red));
        }




    }

    @Override
    public int getItemCount() {
        return modelCryptos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, image2;
        TextView currencyNameTv, symbolTv, rateTv, percentTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyNameTv = itemView.findViewById(R.id.idTVCurrencyNamePercent);
            symbolTv = itemView.findViewById(R.id.idTVSymbolPercent);
            rateTv = itemView.findViewById(R.id.idTVCurrencyRatePercent);
            image = itemView.findViewById(R.id.image1);
            percentTv = itemView.findViewById(R.id.idTVCurrencyPercent);
            image2 = itemView.findViewById(R.id.image2);

        }
    }
}
