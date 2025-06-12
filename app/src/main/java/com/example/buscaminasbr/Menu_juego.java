package com.example.buscaminasbr;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Menu_juego extends AppCompatActivity {

    String id_player = "";
    String name = "";
    int mmr = -1;
    String host = "localhost";
    int number_players = 2;
    Button bt_normal;
    Button bt_rank;
    Button bt_ranking;
    Spinner spn_nplayers;
    Integer[] n_players = {2, 4, 8};
    LinearLayout ll_ranking;
    boolean debug_mode = false;
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
        debug_mode = getIntent().getBooleanExtra("debug_mode", false);
        Toast.makeText(this, id_player+" user: "+name+" -> mmr: "+mmr, Toast.LENGTH_SHORT).show();
        bt_normal = findViewById(R.id.mj_bt_normal);
        bt_rank = findViewById(R.id.mj_bt_rank);
        bt_ranking = findViewById(R.id.mj_bt_ranking);
        spn_nplayers = findViewById(R.id.mj_spn_nplayers);
        ArrayAdapter<Integer> ad_nplayers =new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, n_players);
        spn_nplayers.setAdapter(ad_nplayers);
        ll_ranking = findViewById(R.id.mj_ll_ranking);
        update_ranking();
    }

    public void normal(View v){
        try_match(1);
    }
    public void rank(View v){
        try_match(0);
    }

    public void ranking(View v){
        update_ranking();
    }

    int rank_size = 10;
    private void update_ranking(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://"+host+":5106/ranking/top/"+rank_size;
                String default_response = "{\"result\": false, \"data\": \"ERROR CONNECTION\"}";
                String response = OKHttpMicroserviceExecutor.get(url, default_response);
                String url_user = "http://"+host+":5106/ranking/player/"+id_player;
                String response_user = OKHttpMicroserviceExecutor.get(url_user, default_response);
                try {
                    JSONObject json_respuesta = new JSONObject(response);
                    JSONObject json_my_rank = new JSONObject(response_user);
                    boolean result = json_respuesta.getBoolean("result");
                    if(result){
                        JSONArray array_players = json_respuesta.getJSONArray("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                add_ranker(array_players, json_my_rank);
                            }
                        });
                    }
                } catch (JSONException je) {
                    throw new RuntimeException(je);
                }
            }
        }).start();
    }

    void add_ranker(JSONArray data, JSONObject my_rank){
        try {
            if (ll_ranking.getChildCount()>1){
                ll_ranking.removeViews(1, ll_ranking.getChildCount()-1);
            }
            for(int i = 0; i< data.length(); i++){
                JSONObject player = data.getJSONObject(i);
                TextView tv_player = new TextView(bt_normal.getContext());
                ll_ranking.addView(tv_player);
                String p_nombre = player.getString("nombre");
                if(player.getString("id_usuario").equals(id_player)){
                    p_nombre = "TÚ";
                    tv_player.setPaintFlags(tv_player.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
                int p_mmr = player.getInt("mmr");
                tv_player.setText(String.format("%s-%s: %s", i+1, p_nombre, p_mmr));
            }
            //JSONObject jsonObject_my_rank = my_rank.getJSONObject("data");
            if(my_rank.getBoolean("result")){
                JSONObject jo_rd = my_rank.getJSONObject("data");
                int position = jo_rd.getInt("position");
                if(position>rank_size){
                    TextView tv_player = new TextView(bt_normal.getContext());
                    ll_ranking.addView(tv_player);
                    String p_nombre = "TÚ";
                    int p_mmr = jo_rd.getInt("mmr");
                    tv_player.setPaintFlags(tv_player.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv_player.setText(String.format("%s-%s: %s", position, p_nombre, p_mmr));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void try_match(int type){
        if (debug_mode){
            start_match(-1);
            return;
        }
        waiting_dialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //verificar si ya se había unido a una partida
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
                    System.out.println("wtf respuesta en buscar match existente: "+response);
                    //volver a la partida si la había
                    JSONObject responseJson = new JSONObject(response);
                    boolean state = responseJson.getBoolean("state");
                    if(state){
                        match_id = responseJson.getInt("match");
                        start_match(match_id);
                    }else{
                        //nuscar una cola en caso de no tener partida iniciada
                        buscar_cola(type);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void  buscar_cola(int type){
        number_players = (Integer) spn_nplayers.getSelectedItem();
        String jsonPlayerData = String.format("{\"id_player\":\"%s\",\"name\":\"%s\", \"mmr\":%s}", id_player, name, mmr);
        String jsonInputString = "{\"player_data\": "+jsonPlayerData+", \"num_players\": "+number_players+", \"tipo_cola\":"+type+"}";
        String url = "http://"+host+":5103/cola/unirse";
        String default_response = "{\"message\": 'cola rechazada', \"id_cola\": -1}";

        //Toast.makeText(this, "url: "+url, Toast.LENGTH_SHORT).show();
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
        intent.putExtra("number_players", number_players);
        intent.putExtra("debug_mode", debug_mode);
        //intent.putExtra("match_data", data.toString());
        startActivity(intent);
    }
    void waiting_dialog(){
        new AlertDialog.Builder(this)
                .setTitle("wainting match")
                .setMessage("Do you wanna get out")
                .setNegativeButton("CANCELAR", (dialog, which) ->  {
                    //detener busqueda
                    bt_normal.setFocusable(true);
                }).show();
    }
}