package com.example.buscaminasbr;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Menu_juego extends AppCompatActivity {

    String id_player = "";
    String name = "";
    int mmr = -1;
    String host = "localhost";
    Button bt_normal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_juego);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id_player = getIntent().getStringExtra("id_player");
        name = getIntent().getStringExtra("name");
        mmr = getIntent().getIntExtra("mmr", -1);
        host = getIntent().getStringExtra("host");
        Toast.makeText(this, id_player+" user: "+name+" -> mmr: "+mmr, Toast.LENGTH_SHORT).show();
        bt_normal = findViewById(R.id.mj_bt_normal);
    }

    public void normal(View v){
        String jsonPlayerData = String.format("{\"id_player\":\"%s\",\"name\":\"%s\", \"mmr\":%s}", id_player, name, mmr);
        String jsonInputString = "{\"player_data\": "+jsonPlayerData+", \"num_players\": "+2+", \"tipo_cola\":0}";
        String url = "http://"+host+":5103/cola/unirse";
        String default_response = "{\"message\": 'cola rechazada', \"id_cola\": -1}";

        Toast.makeText(this, "url: "+url, Toast.LENGTH_SHORT).show();
        System.out.println("mi json: "+jsonInputString);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_response);

                    esperar_cola(response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    private void esperar_cola(String data){
        int id_cola = -1;
        try {
            id_cola = new JSONObject(data).getInt("id_cola");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        int finalId_cola = id_cola;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://"+host+":5103/cola/status/"+finalId_cola;
                String default_response = "{\"ready\": false, \"message\": \"cola no encontrada\"}";
                String response = OKHttpMicroserviceExecutor.get(url, default_response);
                try {
                    JSONObject responseJson = new JSONObject(response);
                    boolean ready = responseJson.getBoolean("ready");
                    while (!ready){
                        sleep(10000);
                        ready = new JSONObject(OKHttpMicroserviceExecutor.get(url, default_response)).getBoolean("ready");
                    }
                    esperar_match();
                } catch (JSONException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void esperar_match(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                int match_id = -1;
                String url = "http://"+host+":5104/match/find/"+id_player;
                String default_response = "{\"state\": false, \"message\": \"jugadr o cola no encontrada: \"}";
                String response = OKHttpMicroserviceExecutor.get(url, default_response);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(bt_normal.getContext(), "respuesta recibida: "+response, Toast.LENGTH_SHORT).show();
                        }
                    });
                    System.out.println("wtf respuesta en esperar match: "+response);
                    JSONObject responseJson = new JSONObject(response);
                    boolean state = responseJson.getBoolean("state");
                    while (!state){
                        sleep(10000);
                        responseJson = new JSONObject(OKHttpMicroserviceExecutor.get(url, default_response));
                        state = responseJson.getBoolean("state");
                    }
                    match_id = responseJson.getInt("match");
                    start_match(match_id);
                } catch (JSONException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void start_match(int id_match){
        System.out.println("partida iniciada");
        //data = { mines: mineArray, moves:[]}
        Intent intent = new Intent(this, Buscaminas_MV.class);
        intent.putExtra("id_player", id_player);
        intent.putExtra("name", name);
        intent.putExtra("mmr", mmr);
        intent.putExtra("host", host);
        intent.putExtra("id_match", id_match);
        //intent.putExtra("match_data", data.toString());
        startActivity(intent);
    }
}