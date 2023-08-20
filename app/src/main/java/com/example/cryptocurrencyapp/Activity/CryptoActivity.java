package com.example.cryptocurrencyapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptocurrencyapp.Adapter.AdapterCrypto;
import com.example.cryptocurrencyapp.Model.ModelCrypto;
import com.example.cryptocurrencyapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CryptoActivity extends AppCompatActivity {

    private EditText searchEdt;
    private RecyclerView currenciesRV;
    private ArrayList<ModelCrypto> modelCryptos;
    private AdapterCrypto adapterCrypto;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    BottomNavigationView nav;

    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);

        //init_screen();

        firebaseAuth = FirebaseAuth.getInstance();

        searchEdt = findViewById(R.id.idEdtSearch);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        progressBar = findViewById(R.id.progressBar);
        modelCryptos = new ArrayList<>();
        adapterCrypto = new AdapterCrypto(modelCryptos, this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(adapterCrypto);
        getCurrencyData();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCurrencies(s.toString());
            }
        });


        adapterCrypto.setOnCoinClickListener(coin -> {
            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra("coin", coin);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        });


        nav=findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_market);

        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                    return true;
                case R.id.nav_market:
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
            } else if (item.getSymbol().toLowerCase().contains(currency.toLowerCase())) {
                filteredList.add(item);
            }
            adapterCrypto.filterList(filteredList);
        }

    }

    //data çektiğimiz yer
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
                            double percent=quoteObj.getDouble("percentChange24h");
                            double marketcap = quoteObj.getDouble("marketCap");
                            double volume24h = quoteObj.getDouble("volume24h");
                            double dominance = quoteObj.getDouble("dominance");
                            double percentChange30d = quoteObj.getDouble("percentChange30d");
                            modelCryptos.add(new ModelCrypto(id, name, symbol, price, percent,marketcap, volume24h, dominance, percentChange30d, totalSupply));
                        }
                    }

                    adapterCrypto.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CryptoActivity.this, "Json verilerini çıkaramadı...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CryptoActivity.this, "Veriler alınmıyor.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("start&limit", "1&500");
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
                        modelCryptos.add(new ModelCrypto(id, name, symbol, price, percent));
                    }
                    adapterCrypto.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CryptoActivity.this, "Json verilerini çıkaramadı...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CryptoActivity.this, "Veriler alınmıyor.", Toast.LENGTH_SHORT).show();
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


}