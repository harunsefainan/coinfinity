package com.example.cryptocurrencyapp.Adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterCryptoHome extends RecyclerView.Adapter<AdapterCryptoHome.ViewHolder> {

    private ArrayList<ModelCrypto> modelCryptos;
    private Context context;
    private static DecimalFormat df2=new DecimalFormat("#.##");

    public AdapterCryptoHome(ArrayList<ModelCrypto> modelCryptos, Context context) {
        this.modelCryptos = modelCryptos;
        this.context = context;
    }

    public void filterList(ArrayList<ModelCrypto> filteredList){
        modelCryptos=filteredList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public AdapterCryptoHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.crypto_home_item,parent,false);
        return new AdapterCryptoHome.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelCrypto modelCrypto= modelCryptos.get(position);
        holder.currencyNameTv.setText(modelCrypto.getName());
        holder.rateTv.setText("$ "+df2.format(modelCrypto.getPrice()));

        Glide.with(holder.image).load(
                "https://s2.coinmarketcap.com/static/img/coins/64x64/"+modelCrypto.getId()+".png"
        ).into(holder.image);

    }



    @Override
    public int getItemCount() {
        return modelCryptos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView currencyNameTv,rateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyNameTv=itemView.findViewById(R.id.idTVCurrencyNameHome);
            rateTv=itemView.findViewById(R.id.idTVCurrencyRateHome);
            image=itemView.findViewById(R.id.image2);
        }
    }
}
