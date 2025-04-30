package com.example.buscaminasbr.model;

import android.widget.TextView;
import android.widget.Toast;

import com.example.buscaminasbr.Inicio_sesion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MicroserviceExecutor {
    private ExecutorService executorService;
    public MicroserviceExecutor(){
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void llamarMicroservicio(String str_url, TextView tv_respuesta, JSONObject data) {
        executorService.execute(() -> {
            String result = "";
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(str_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("SET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true); // Permite enviar datos

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    result = response.toString();

                } else {
                    tv_respuesta.post(()->Toast.makeText(tv_respuesta.getContext(), "no ok: "+statusCode, Toast.LENGTH_LONG).show());
                    System.out.println("Error en la conexión. Código: " + statusCode);
                }

            } catch (Exception e) {
                tv_respuesta.post(()->Toast.makeText(tv_respuesta.getContext(), "error error: "+e, Toast.LENGTH_LONG).show());
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            String nombre = "";
            if(!result.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    nombre = jsonObject.optString("nombre", "not found");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            String finalResult = nombre;
            tv_respuesta.post(()->{
                tv_respuesta.setText(finalResult);
            });
        });
    }

    public void llamarMicroservicioPost(String str_url, Inicio_sesion inicio_sesion, JSONObject data) {
        executorService.execute(() -> {
            String result = "";
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(str_url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true); // Permite enviar datos

                String jsonInputString = "{\"nombre\": \""+data.optString("id", "noName")+"\", \"pas\": 123}";//data.toString();
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                    os.flush();
                }catch (Exception ex){
                    inicio_sesion.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(inicio_sesion, "error: "+ex, Toast.LENGTH_LONG).show();
                        }
                    });
                    ex.printStackTrace();
                    return;
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    result = response.toString();

                } else {
                    inicio_sesion.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(inicio_sesion, "no ok: "+statusCode, Toast.LENGTH_LONG).show();
                        }
                    });
                    System.out.println("Error en la conexión. Código: " + statusCode);
                }

            } catch (Exception e) {
                inicio_sesion.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(inicio_sesion, "error error: "+e, Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            String nombre = "";
            if(!result.isEmpty()){
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    nombre = jsonObject.optString("nombre", "not found");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            String finalResult = nombre;
            inicio_sesion.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    inicio_sesion.inicio_sesion_exitoso(finalResult);
                }
            });
        });
    }

    // Siempre es buena práctica cerrar el executor cuando ya no lo necesitas
    public void cerrarExecutor() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}