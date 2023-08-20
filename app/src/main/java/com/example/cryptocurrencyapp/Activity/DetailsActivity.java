package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    private TextView price, percent, name,name2,marketcap,volume24h,dominance,percentchange30d,totalsupply;
    private ImageView image,detailbtn;
    private ImageButton exit;
    private AppCompatButton btn15dk,btn1h,btn4h,btn1d,btn1w,btn1m;
    private WebView detailWV;
    private static DecimalFormat df2=new DecimalFormat("#.#########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ModelCrypto modelCrypto = (ModelCrypto) bundle.getSerializable("coin");

            price = findViewById(R.id.detailPriceTextView);
            percent = findViewById(R.id.detailChangeTextView);
            name = findViewById(R.id.detailSymbolTextView);
            image = findViewById(R.id.detailImageView);
            btn15dk=findViewById(R.id.button5);
            btn1h=findViewById(R.id.button4);
            btn4h=findViewById(R.id.button3);
            btn1d = findViewById(R.id.button2);
            btn1w=findViewById(R.id.button1);
            btn1m=findViewById(R.id.button);
            detailWV = findViewById(R.id.detaillChartWebView);
            exit=findViewById(R.id.backStackButton);
            detailbtn=findViewById(R.id.detailChangeImageView);
            name2=findViewById(R.id.name2);
            volume24h=findViewById(R.id.volume24h);
            marketcap=findViewById(R.id.marketcap);
            dominance=findViewById(R.id.dominance);
            percentchange30d=findViewById(R.id.percentchange30d);
            totalsupply=findViewById(R.id.totalsupply);








            WebSettings webSettings = detailWV.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            // Veriyi kullanma
            if (modelCrypto != null) {
                price.setText("$ "+String.format(String.valueOf(df2.format(modelCrypto.getPrice()))));
                percent.setText(String.format(String.valueOf(modelCrypto.getPercent()))+" %");
                name.setText(modelCrypto.getName());
                name2.setText(modelCrypto.getName());
                marketcap.setText("$ "+String.format(String.valueOf(modelCrypto.getMarketcap())));
                volume24h.setText(String.format(String.valueOf(modelCrypto.getVolume24h())));
                dominance.setText("%"+String.format(String.valueOf(modelCrypto.getDominance())));
                percentchange30d.setText("%"+String.format(String.valueOf(modelCrypto.getPercentChange30d())));
                totalsupply.setText(String.format(String.valueOf(modelCrypto.getTotalSupply())));

                if (modelCrypto.getPercent()>0){
                    detailbtn.setImageResource(R.drawable.ic_caret_up);
                    percent.setTextColor(ContextCompat.getColor(this, R.color.green));
                }else {
                    detailbtn.setImageResource(R.drawable.ic_caret_down);
                    percent.setTextColor(ContextCompat.getColor(this, R.color.red));
                }

                Glide.with(image).load(
                        "https://s2.coinmarketcap.com/static/img/coins/64x64/" + modelCrypto.getId() + ".png"
                ).into(image);

                detailWV.loadUrl(
                        "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                );

                btn15dk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackgroundResource(R.drawable.active_button);
                        btn1h.setBackground(null);
                        btn4h.setBackground(null);
                        btn1d.setBackground(null);
                        btn1w.setBackground(null);
                        btn1m.setBackground(null);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=15&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });

                btn1h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackground(null);
                        btn1h.setBackgroundResource(R.drawable.active_button);
                        btn4h.setBackground(null);
                        btn1d.setBackground(null);
                        btn1w.setBackground(null);
                        btn1m.setBackground(null);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=1H&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });

                btn4h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackground(null);
                        btn1h.setBackground(null);
                        btn4h.setBackgroundResource(R.drawable.active_button);
                        btn1d.setBackground(null);
                        btn1w.setBackground(null);
                        btn1m.setBackground(null);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=4H&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });

                btn1d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackground(null);
                        btn1h.setBackground(null);
                        btn4h.setBackground(null);
                        btn1d.setBackgroundResource(R.drawable.active_button);
                        btn1w.setBackground(null);
                        btn1m.setBackground(null);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });

                btn1w.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackground(null);
                        btn1h.setBackground(null);
                        btn4h.setBackground(null);
                        btn1d.setBackground(null);
                        btn1w.setBackgroundResource(R.drawable.active_button);
                        btn1m.setBackground(null);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=W&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });

                btn1m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn15dk.setBackground(null);
                        btn1h.setBackground(null);
                        btn4h.setBackground(null);
                        btn1d.setBackground(null);
                        btn1w.setBackground(null);
                        btn1m.setBackgroundResource(R.drawable.active_button);
                        detailWV.loadUrl(
                                "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol="+modelCrypto.getSymbol()+"USD&interval=M&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
                        );
                    }
                });
            }
        }
    }







}