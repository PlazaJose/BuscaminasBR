package com.example.buscaminasbr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.Map;
import com.example.buscaminasbr.model.Messages_manager;
import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;
import com.example.buscaminasbr.view.MiniBM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class Buscaminas_MV extends AppCompatActivity {

    GridLayout gridLayout;

    String id_player = "";
    String name = "";
    int mmr = -1;
    String host = "localhost";
    int id_match;
    int number_players = 2;
    MiniBM[] enemies;
    Messages_manager messages_manager;
    LinearLayout mv_ll_messages;
    RelativeLayout mv_relative_layout;
    FloatingActionButton mv_fab_add_message;
    boolean debug_mode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscaminas_mv);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id_player = getIntent().getStringExtra("id_player");
        name = getIntent().getStringExtra("name");
        mmr = getIntent().getIntExtra("mmr", -1);
        id_match = getIntent().getIntExtra("id_match", -1);
        host = getIntent().getStringExtra("host");
        number_players = getIntent().getIntExtra("number_players",2);
        debug_mode = getIntent().getBooleanExtra("debug_mode", false);
//        String data_str = getIntent().getStringExtra("match_data");
//        try {
//            assert data_str != null;
//            match_data = new JSONObject(data_str);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
        Toast.makeText(this, id_player+" user: "+name+" -> mmr: "+mmr, Toast.LENGTH_SHORT).show();

        mv_ll_messages = findViewById(R.id.mv_ll_messages);
        mv_relative_layout = findViewById(R.id.mv_relative_layout);
        mv_fab_add_message = findViewById(R.id.mv_fab_add_message);
        mv_fab_add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emotes = {"GG", "GJ", "WP"};
                String message = emotes[new Random().nextInt(emotes.length)];
                mensaje(message);
            }
        });

        //setContentView(get_map());
        mv_relative_layout.addView(get_map());
        if(!debug_mode)messages_manager = new Messages_manager(this, host, id_match, id_player, name);
        update_enemies();

    }

    public RelativeLayout get_map(){
        int map_width = 10;
        int map_height = 10;

        gridLayout = new GridLayout(this);
        gridLayout.setRowCount(map_width);
        gridLayout.setColumnCount(map_height);

        Map map = new Map(this, map_width, map_height, 10, id_match, id_player, name, mmr, host);
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                gridLayout.addView(map.getCuadricula(i, j));
            }
        }
        //map.set_map(match_data);


        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        //GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        // Center the GridLayout using layout_centerInParent
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.topMargin = 250;

        //create enemy viewa
        //MiniBM miniBM = new MiniBM(this);
        GridLayout gridLayout_enemys = new GridLayout(this);
        gridLayout_enemys.setRowCount(3);
        gridLayout_enemys.setColumnCount(3);
        gridLayout_enemys.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams layoutParams_enemy = (RelativeLayout.LayoutParams) gridLayout_enemys.getLayoutParams();
        layoutParams_enemy.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams_enemy.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams_enemy.bottomMargin = 250;

        enemies = new MiniBM[number_players-1];
        for(int i = 0; i < number_players-1; i++){
            MiniBM mbm = new MiniBM(this);
            enemies[i] = mbm;
            gridLayout_enemys.addView(mbm);
        }

        Button send_emote = new Button(this);
        send_emote.setText("(-.-)");
        //gridLayout_enemys.addView(send_emote);
        send_emote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emotes = {"GG", "GJ", "WP"};
                String message = emotes[new Random().nextInt(emotes.length)];
                mensaje(message);
            }
        });

        //gridLayout_enemys.addView(miniBM);
        //add enemy views to relativelayout
        relativeLayout.addView(gridLayout_enemys);
        // Add the GridLayout to the RelativeLayout
        relativeLayout.addView(gridLayout);

        return relativeLayout;
    }

    void update_enemies(){
        if (debug_mode){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://"+host+":5104/match/match/"+id_match;
                System.out.println("url en get match: "+url);
                String default_response = "{\"state\": false, \"message\": \"match no encontrada: \", \"match\":null}";
                String response = OKHttpMicroserviceExecutor.get(url, default_response);
                try {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    if(jsonObjectResponse.getBoolean("state")){
                        JSONObject jsonObjectMatch = jsonObjectResponse.getJSONObject("match");
                        JSONArray jsonArrayJugadores = jsonObjectMatch.getJSONArray("jugadores");
                        int counter = 0;
                        for(int i = 0; i<jsonArrayJugadores.length();i++){
                            JSONObject jugador = jsonArrayJugadores.getJSONObject(i);
                            if(!jugador.getString("id").equals(id_player)){
                                if(counter<enemies.length){
                                    enemies[counter].set_map(jugador.getJSONObject("map").toString());
                                    enemies[counter].set_server_settings(host, jugador.getString("id"), id_match);
                                    counter++;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void on_message(JSONObject data){
        String message = "";
        try {
            message = data.getString("name")+" : "+ data.getString("message");
        } catch (JSONException e) {
            message = e.getMessage();
            throw new RuntimeException(e);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        TextView tv_message = new TextView(this);
        tv_message.setText(message);
        mv_ll_messages.addView(tv_message);
    }

    void mensaje(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        System.out.println("el mensaje enviado: "+mensaje);
        if (debug_mode){
            return;
        }
        messages_manager.send_message(mensaje);
    }

    @Override
    protected void onStop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonInputString = "{\"id_player\":\"" + id_player + "\", \"id_match\":" + id_match + "}";
                    String url = "http://"+host+":5104/match/abandonar";
                    String default_respones = "{\"state\":"+false+", \"message\": \""+"e"+"\"}";
                    String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_respones);
                    System.out.println("response on push move: "+response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        super.onStop();
    }
}