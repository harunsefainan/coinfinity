package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptocurrencyapp.Adapter.AdapterCryptoHome;
import com.example.cryptocurrencyapp.Adapter.AdapterCryptoPercent;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private RecyclerView currenciesRVHome,RVPercent;
    private ArrayList<ModelCrypto> modelCryptos;

    private ArrayList<ModelCrypto> yukselen;
    private ArrayList<ModelCrypto> dusen;

    private AdapterCryptoHome adapterCryptoHome;
    private AdapterCryptoPercent adapterCryptoPercent;

    private BottomNavigationView nav;

    LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;
    private long backPressedTime;

    private TextView tvYukselen,tvDusen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        VideoView vv=findViewById(R.id.vide_view);
        vv.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.crypto);
        MediaController med=new MediaController(this);
        vv.setMediaController(med);
        med.setAnchorView(vv);
        vv.start();

        //init_screen();

        firebaseAuth = FirebaseAuth.getInstance();


        tvYukselen=findViewById(R.id.yukselentxt);
        tvDusen=findViewById(R.id.dusentxt);



        currenciesRVHome = findViewById(R.id.cryptohome);
        progressBar = findViewById(R.id.progressBar);
        modelCryptos = new ArrayList<>();
        adapterCryptoHome = new AdapterCryptoHome(modelCryptos, this);

        linearLayoutManager=new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.HORIZONTAL,false);
        currenciesRVHome.setLayoutManager(linearLayoutManager);
        currenciesRVHome.setAdapter(adapterCryptoHome);
        getCurrencyData();


        RVPercent=findViewById(R.id.percent);
        yukselen=new ArrayList<>();
        dusen=new ArrayList<>();

        tvYukselen.setBackgroundTintList(ContextCompat.getColorStateList(
                HomeActivity.this,R.color.black));
        adapterCryptoPercent=new AdapterCryptoPercent(yukselen);
        linearLayoutManager=new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false);
        RVPercent.setLayoutManager(linearLayoutManager);
        RVPercent.setAdapter(adapterCryptoPercent);


        tvYukselen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvYukselen.setBackgroundTintList(ContextCompat.getColorStateList(
                        HomeActivity.this,R.color.black));
                adapterCryptoPercent=new AdapterCryptoPercent(yukselen);
                linearLayoutManager=new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false);
                RVPercent.setLayoutManager(linearLayoutManager);
                RVPercent.setAdapter(adapterCryptoPercent);
            }
        });

        tvDusen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDusen.setBackgroundTintList(ContextCompat.getColorStateList(
                        HomeActivity.this,R.color.black));
                adapterCryptoPercent=new AdapterCryptoPercent(dusen);
                linearLayoutManager=new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false);
                RVPercent.setLayoutManager(linearLayoutManager);
                RVPercent.setAdapter(adapterCryptoPercent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav=findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_home);

        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    return true;
                case R.id.nav_market:
                    startActivity(new Intent(getApplicationContext(),CryptoActivity.class));
                    finish();
                    return true;
                case R.id.nav_user:
                    startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
                    finish();
                    return true;
            }
            return false;
        });

    }

    private void filterCurrencies(String currency) {
        ArrayList<ModelCrypto> filteredList = new ArrayList<>();
        for (ModelCrypto item : modelCryptos) {
            if (item.getName().toLowerCase().contains(currency.toLowerCase())) {
                filteredList.add(item);
            }
            adapterCryptoHome.filterList(filteredList);
        }

    }

    private void getCurrencyData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://api.coinmarketcap.com/data-api/v3/cryptocurrency/listing?start=1&limit=500";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray dataArray = data.getJSONArray("cryptoCurrencyList");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        String id = dataObj.getString("id");
                        double totalSupply = dataObj.getDouble("totalSupply");

                        // Döngü kullanarak quotes JSONArray'inin içindeki verileri alın
                        JSONArray quotesArray = dataObj.getJSONArray("quotes");
                        for (int j = 0; j < quotesArray.length(); j++) {
                            JSONObject quoteObj = quotesArray.getJSONObject(j);
                            double price = quoteObj.getDouble("price");
                            double percent = quoteObj.getDouble("percentChange24h");
                            double marketcap = quoteObj.getDouble("marketCap");
                            double volume24h = quoteObj.getDouble("volume24h");
                            double dominance = quoteObj.getDouble("dominance");
                            double percentChange30d = quoteObj.getDouble("percentChange30d");
                            modelCryptos.add(new ModelCrypto(id, name, symbol, price, percent,marketcap, volume24h, dominance, percentChange30d, totalSupply));
                            if (percent>0){
                                yukselen.add(new ModelCrypto(id, name, symbol, price, percent,marketcap, volume24h, dominance, percentChange30d, totalSupply));
                            }else{
                                dusen.add(new ModelCrypto(id, name, symbol, price, percent,marketcap, volume24h, dominance, percentChange30d, totalSupply));
                            }
                        }

                    }

                    adapterCryptoHome.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "Json verilerini çıkaramadı...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Veriler alınmıyor.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("X-CMC_PRO_API_KEY", "9a4c8dcf-2247-41a1-913b-f980c66d1b3c");
                return header;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    //eski api
    /*private void getCurrencyData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        String id = dataObj.getString("id");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");
                        double percent = USD.getDouble("percent_change_24h");
                        modelCryptos.add(new ModelCrypto(id, name, symbol, price,percent));
                        if (percent>0){
                            yukselen.add(new ModelCrypto(id, name, symbol, price,percent));
                        }else{
                            dusen.add(new ModelCrypto(id, name, symbol, price,percent));
                        }
                    }
                    adapterCryptoHome.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "Json verilerini çıkaramadı...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Veriler alınmıyor.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("X-CMC_PRO_API_KEY", "9a4c8dcf-2247-41a1-913b-f980c66d1b3c");
                return header;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }*/

    //restart activity
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
    }


    //geri basıldığında

    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            System.exit(0);
        } else {
            Toast.makeText(this, "Uygulamadan çıkmak için tekrar yapın.", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }

    private void init_screen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View view = getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(flags);
                }
            }
        });
    }


}

