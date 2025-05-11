package com.example.buscaminasbr.model;

import android.widget.Toast;

import com.example.buscaminasbr.Inicio_sesion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class OKHttpMicroserviceExecutor {
    public OKHttpMicroserviceExecutor(){

    }
    private static final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json");

    public void llamarMicroservicioPost(String url, String jsonData, Inicio_sesion inicio_sesion) {
        RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    System.out.println("Response: " + result);
                    inicio_sesion.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inicio_sesion.inicio_sesion_exitoso(result);
                        }
                    });
                } else {
                    System.err.println("Request failed with code: " + response.code());
                    inicio_sesion.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(inicio_sesion, "error code: "+response.code(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("response: "+response);
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        }catch (Exception e){
            System.out.println("error: "+e);
            return "{\"entrada\":"+false+", \"nombre\": \""+e+"\"}";
        }
    }

}
