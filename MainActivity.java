package com.example.init_app;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    String SERVER_URL = "http://194.176.114.21:8050/";
    TextInputEditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (TextInputEditText) findViewById(R.id.login_name);
    }

    public class Register {
        public String action = "register";
        public String nickname = username.getText().toString();
        public Register(){
        }
    }

    public void next_activity(View view){
        new Thread(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                Register register = new Register();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Scanner in = null;
                try {
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    OutputStream out = urlConnection.getOutputStream();
                    out.write(gson.toJson(register).getBytes());
                    in = new Scanner(urlConnection.getInputStream());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                int token;
                if (in.hasNext()) {
                    String response = in.nextLine();
                    JSONObject resp = null;
                    try {
                        resp = new JSONObject(response);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("STATUS",resp.toString());
                    try {
                        if (resp.get("status").equals("ok")){
                            token = resp.getInt("token");
                            intent.putExtra("token",token);
                            startActivity(intent);
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
