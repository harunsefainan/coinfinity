package com.example.cryptocurrencyapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterCrypto extends RecyclerView.Adapter<AdapterCrypto.ViewHolder> {

    private ArrayList<ModelCrypto> modelCryptos;
    private Context context;
    private static DecimalFormat df2=new DecimalFormat("#.######");

    private OnCoinClickListener onCoinClickListener;

    public interface OnCoinClickListener {
        void onCoinClick( ModelCrypto modelCrypto);
    }

    public void setOnCoinClickListener(OnCoinClickListener listener) {
        this.onCoinClickListener = listener;
    }

    public AdapterCrypto(ArrayList<ModelCrypto> modelCryptos, Context context) {
        this.modelCryptos = modelCryptos;
        this.context = context;
    }

    public void filterList(ArrayList<ModelCrypto> filteredList){
        modelCryptos=filteredList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public AdapterCrypto.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.crypto_rv_item,parent,false);
        return new AdapterCrypto.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelCrypto modelCrypto= modelCryptos.get(position);
        holder.currencyNameTv.setText(modelCrypto.getName());
        holder.symbolTv.setText(modelCrypto.getSymbol());
        holder.rateTv.setText("$ "+df2.format(modelCrypto.getPrice()));
        holder.percentTv.setText(df2.format(modelCrypto.getPercent())+" %");

        Glide.with(holder.image2).load(
                "https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + modelCrypto.getId() + ".png"
        ).into(holder.image2);

        Glide.with(holder.image).load(
                "https://s2.coinmarketcap.com/static/img/coins/64x64/"+modelCrypto.getId()+".png"
        ).into(holder.image);

        if (modelCrypto.getPercent()>0){
            holder.percentTv.setTextColor(ContextCompat.getColor(holder.percentTv.getContext(), R.color.green));
        }else {
            holder.percentTv.setTextColor(ContextCompat.getColor(holder.percentTv.getContext(), R.color.red));
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemId=modelCrypto.getId();

                if (onCoinClickListener != null) {
                    onCoinClickListener.onCoinClick(modelCrypto);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return modelCryptos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image,image2;
        TextView currencyNameTv,symbolTv,rateTv,percentTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyNameTv=itemView.findViewById(R.id.idTVCurrencyNamePercent);
            symbolTv=itemView.findViewById(R.id.idTVSymbolPercent);
            rateTv=itemView.findViewById(R.id.idTVCurrencyRatePercent);
            percentTv= itemView.findViewById(R.id.idTVCurrencyPercent);
            image =itemView.findViewById(R.id.image1);
            image2=itemView.findViewById(R.id.image2);

        }
    }
}
