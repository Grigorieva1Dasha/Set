package com.example.init_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GameActivity extends AppCompatActivity {

    int my_token;
    String[] cards;
    ListView card_list;
    String SERVER_URL = "http://194.176.114.21:8050/";
    GameActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        Intent intent = getIntent();
        my_token = intent.getIntExtra("token",0);
        card_list = findViewById(R.id.card_list);
    }
    public class GetCards {
        public String action = "fetch_cards";
        public int token = my_token;
        public GetCards(){
        }
    }
    public void show_cards(View view){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GameActivity.this, android.R.layout.simple_list_item_1, cards);
        card_list.setAdapter(adapter);
    }
    public void get_cards(View view){
        new Thread(new Runnable() {
            public void run() {
                GetCards getcards = new GetCards();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Scanner in = null;
                try {
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    OutputStream out = urlConnection.getOutputStream();
                    out.write(gson.toJson(getcards).getBytes());
                    in = new Scanner(urlConnection.getInputStream());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (in.hasNext()) {
                    String response = in.nextLine();
                    JSONObject resp = null;
                    try {
                        resp = new JSONObject(response);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String status = "";
                    JSONArray result;
                    try {
                        status = resp.getString("status");
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status.equals("ok")){
                        try {
                            result = resp.getJSONArray("cards");
                            cards = new String[result.length()];
                            for (int i = 0; i < result.length(); i++){
                                cards[i] = result.get(i).toString();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("CARDS",resp.toString());
                }
            }
        }).start();
    }
}
