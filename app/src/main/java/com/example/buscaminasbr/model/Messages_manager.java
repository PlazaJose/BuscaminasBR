package com.example.buscaminasbr.model;

import com.example.buscaminasbr.Buscaminas_MV;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class Messages_manager {
    String host = "localhost";
    int id_match = -1;
    String id_player = "";
    String name = "";
    private final Socket mSocket;
    Buscaminas_MV buscaminas_mv;
    public Messages_manager(Buscaminas_MV buscaminas_mv, String host, int id_match, String id_player, String name){
        this.buscaminas_mv = buscaminas_mv;
        this.host = host;
        this.id_match = id_match;
        this.id_player = id_player;
        this.name = name;
        try {
            String url = "http://"+host+":3000";
            System.out.println("url in message manager: "+url);
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        start_listeners();
        connect();
        join_room();
        //send_message("holiwis");
    }

    public void connect(){
        mSocket.connect();
    }

    public void attempt_send(String event, String data) {
        if(data.isEmpty()){
            return;
        }
        try {
            JSONObject json_data = new JSONObject(data);
            mSocket.emit(event, json_data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void join_room(){
        String data = String.format("{\"name\":%s, \"room\":%s}", name, "sala"+id_match);
        attempt_send("join_room", data);
    }
    public void send_message(String message){
        String data = String.format("{\"message\":\"%s\"}", message);
        String event = "chat_message";
        attempt_send(event, data);
    }

    private void start_listeners(){
        Emitter.Listener on_message = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                buscaminas_mv.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        buscaminas_mv.on_message(data);
                    }
                });
            }
        };

        mSocket.on("chat_message", on_message);
    }
}
