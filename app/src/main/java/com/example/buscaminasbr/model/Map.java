package com.example.buscaminasbr.model;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.text.style.DynamicDrawableSpan;
import android.widget.Toast;

import com.example.buscaminasbr.view.Cuadricula;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class Map {
    int width;
    int height;
    int mines;
    Cuadricula[][] cuadriculas;

    int cuadricula_size = 100;
    int opened = 0;
    boolean game_started = false;
    String id_player = "";
    String name = "";
    int mmr = -1;
    String host = "localhost";
    int id_match = -1;
    public Map(Context context, int width, int height, int mines, int id_match, String id_player, String name, int mmr, String host){
        this.width = width;
        this.height = height;
        this.id_match = id_match;
        this.id_player = id_player;
        this.name = name;
        this.mmr = mmr;
        this.host = host;
        int area = width*height;
        this.mines = correct_position(mines, (int) Math.floor((10 * area) / 100.0),(int) Math.floor((40 * area) / 100.0));
        this.cuadriculas = new Cuadricula[width][height];
        set_up(context);
        //set_game_started();
        //update_map();
    }

    private void set_up(Context context){
        create_cuadriculas(context);
        //start_game();
    }
    private void reset(){
        game_started = false;
        reset_cuadriculas();
        //set_mines();
        //update_cuadriculas();
        //opened = 0;
    }

    public void start_game(int i, int j){
        set_mines(i, j);
        //update_cuadriculas();
        game_started = true;
        opened = 0;
    }

    private void create_cuadriculas(Context context){
        for (int i = 0; i<this.width; i++){
            for (int j = 0; j<this.height; j++){
                Cuadricula cell = new Cuadricula(context, i, j, cuadricula_size,cuadricula_size, this);
                putCuadricula(cell, i, j);
            }
        }
    }

    private void reset_cuadriculas(){
        for (int i = 0; i<this.width; i++){
            for (int j = 0; j<this.height; j++){
                cuadriculas[i][j].reset();
            }
        }
    }

    private void set_mines_offline(int si, int sj){
        for(int i = 0; i< this.mines; i++){
            Random random = new Random();
            int counter = 0;
            int ni = random.nextInt(this.width);
            int nj = random.nextInt(this.height);
            while (ni==si&&nj==sj){
                ni = random.nextInt(this.width);
                nj = random.nextInt(this.height);
            }
            while (!cuadriculas[ni][nj].setiTipo(Cuadricula.TIPO_MINADO)
                    && counter<30){
                counter++;
            }
        }
    }

    private void set_mines(int si, int sj){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json_map = get_map(si, sj);
                try {
                    assert json_map != null;
                    JSONArray mines = json_map.getJSONArray("mines");
                    for(int i = 0; i<mines.length();i++){
                        JSONObject mine = mines.getJSONObject(i);
                        cuadriculas[mine.getInt("row")][mine.getInt("column")].setiTipo(Cuadricula.TIPO_MINADO);
                    }
                    //update cuadriculas tras finalizar la generación
                    update_cuadriculas();
                    //update first
                    String texto = (cuadriculas[si][sj].getiTipo()==Cuadricula.TIPO_MINADO?"*":""+cuadriculas[si][sj].getiMinas());
                    cuadriculas[si][sj].setText(texto);
                    cuadriculas[si][sj].setiEstado((cuadriculas[si][sj].getiTipo()==Cuadricula.TIPO_MINADO?Cuadricula.ESTADO_EXPLOTADO:Cuadricula.ESTADO_ABIERTO));
                    update_vecinos(si, sj);
                    check_win(si, sj);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private JSONObject get_map_old(int si, int sj){
        String url = "http://"+host+":5104/match/map/"+id_match+"/"+id_player;
        System.out.println("url en get map: "+url);
        String default_response = "{\"state\": false, \"message\": \"jugadr o cola no encontrada\"}";
        String response = OKHttpMicroserviceExecutor.get(url, default_response);
        JSONObject json_respuesta = null;
        try {
            json_respuesta = new JSONObject(response);
            if(json_respuesta.getBoolean("state")){
                return json_respuesta.getJSONObject("map");
                //create_map(json_respuesta.getJSONArray("map"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private JSONObject get_map(int si, int sj){
        String url = "http://"+host+":5104/match/generate";
        System.out.println("url en genenrate map: "+url);
        String default_response = "{\"state\": false, \"message\": \"jugadr o cola no encontrada\"}";
        String jsonInputString = String.format("{\"id_match\":\"%s\", \"id_player\":\"%s\", \"si\":%s, \"sj\":%s}", id_match, id_player, si, sj);
        try {
            String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_response);
            JSONObject json_respuesta = null;
            json_respuesta = new JSONObject(response);
            if(json_respuesta.getBoolean("state")){
                return json_respuesta.getJSONObject("map");
                //create_map(json_respuesta.getJSONArray("map"));
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    void set_game_started(){
        String url = "http://"+host+":5104/match/started/"+id_player;
        String default_response = "{\"state\": false, \"message\": \"jugadr o cola no encontrada: \"}";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = OKHttpMicroserviceExecutor.get(url, default_response);
                try {
                    JSONObject responseJson = new JSONObject(response);
                    boolean state = responseJson.getBoolean("state");
                    if(state){
                        game_started = responseJson.getBoolean("data");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private void update_map(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://"+host+":5104/match/map/"+id_match+"/"+id_player;
                System.out.println("url en get map: "+url);
                String default_response = "{\"state\": false, \"message\": \"jugadr o cola no encontrada\"}";
                try {
                    while(true){
                        String response = OKHttpMicroserviceExecutor.get(url, default_response);
                        JSONObject json_respuesta = null;
                        try {
                            json_respuesta = new JSONObject(response);
                            if(json_respuesta.getBoolean("state")){
                                JSONObject jsonObjectMap =  json_respuesta.getJSONObject("map");
                                //set map
                                JSONArray mines = jsonObjectMap.getJSONArray("mines");
                                for(int i = 0; i<mines.length();i++){
                                    JSONObject mine = mines.getJSONObject(i);
                                    cuadriculas[mine.getInt("row")][mine.getInt("column")].setiTipo(Cuadricula.TIPO_MINADO);
                                }

                                JSONArray moves = jsonObjectMap.getJSONArray("moves");
                                for(int i = 0; i<moves.length();i++){
                                    JSONObject move = moves.getJSONObject(i);
                                    if(move.getInt("state")==3){
                                        cuadriculas[move.getInt("row")][move.getInt("column")].activate();
                                    }else{
                                        cuadriculas[move.getInt("row")][move.getInt("column")].setiEstado(move.getInt("state"));
                                    }
                                }
                                //set_map(jsonObjectMap.toString());
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        sleep(5000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void plan_a_push_move(int row, int column, int state){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonInputString = String.format(
                            "{\"id_match\": %s, \"id_player\":\"%s\", \"move\":{\"row\":%s,\"column\":%s,\"state\":%s}}"
                            ,id_match, id_player, row, column, state);
                    String url = "http://"+host+":5104/match/move";
                    String default_respones = "{\"state\":"+false+", \"message\": \""+"e"+"\"}";
                    String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_respones);
                    System.out.println("response on push move: "+response);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void plan_b_push_move(){}

    private void update_cuadriculas(){
        for (int i = 0; i<this.width; i++){
            for (int j = 0; j<this.height; j++){
                Cuadricula[][] vecinos = get_vecinos(i, j);
                int mines = 0;
                for(int vi = 0; vi < 3; vi++){
                    for(int vj = 0; vj < 3; vj++){
                        if(vecinos[vi][vj] != null){
                            if(vecinos[vi][vj].getiTipo()==Cuadricula.TIPO_MINADO)mines++;
                        }
                    }
                }
                cuadriculas[i][j].update(mines);
            }
        }
    }

    private Cuadricula[][] get_vecinos(int i, int j){
        Cuadricula[][] vecinos = new Cuadricula[3][3];
        for(int vi = -1; vi < 2; vi++)
            for (int vj = -1; vj < 2; vj++) {
                int ni = i + vi;
                int nj = j + vj;
                boolean validate = is_betwin(ni, -1, this.width) && is_betwin(nj, -1, this.height);
                vecinos[vi + 1][vj + 1] = (validate ? cuadriculas[ni][nj] : null);
            }
        return vecinos;
    }
    private int correct_position(int x,int li,int ls){
        if(x<li)return li;
        if(x>ls)return ls;
        return x;
    }

    private Boolean is_betwin(int x, int li, int ls){
        return (li<x&&x<ls);
    }

    public boolean first_update(int i, int j){
        if(!game_started){
            start_game(i, j);
            return true;
        }
        return false;
    }
    public void update_vecinos(int i, int j){
        Cuadricula[][] vecinos = get_vecinos(i, j);
        if(!is_safe(i, j, vecinos))return;

        for(int vi = 0; vi < 3; vi++){
            for(int vj = 0; vj < 3; vj++){
                if(!game_started)return;
                if(vecinos[vi][vj] != null){
                    if(vecinos[vi][vj].getiEstado()==Cuadricula.ESTADO_CERRADO)vecinos[vi][vj].activate();
                }
            }
        }
    }

    private boolean is_safe(int i, int j, Cuadricula[][] vecinos){
        int v_mines = cuadriculas[i][j].getiMinas();
        if(v_mines == 0)return true;
        int banderas = 0;
        for(int vi = 0; vi < 3; vi++){
            for(int vj = 0; vj < 3; vj++){
                if(vecinos[vi][vj] != null){
                    if(vecinos[vi][vj].getiEstado()==Cuadricula.ESTADO_BANDERA)banderas++;
                }
            }
        }
        return banderas == v_mines;
    }

    private boolean lose(int i, int j){
        if(cuadriculas[i][j].getiTipo()==Cuadricula.TIPO_MINADO){
            Toast.makeText(cuadriculas[0][0].getContext(), "You lost", Toast.LENGTH_SHORT).show();
            //reset();
            return true;
        }
        return false;
    }
    private void win(){
        opened++;
        if(opened+mines==width*height){
            Toast.makeText(cuadriculas[0][0].getContext(), "You Won", Toast.LENGTH_SHORT).show();
            //reset();
        }
    }

    public void check_win(int i, int j){
        //cuadriculas[0][0].setText(""+opened+":"+mines);
        if(!lose(i, j))win();
    }
    public Cuadricula[][] getCuadriculas() {
        return cuadriculas;
    }

    public void setCuadriculas(Cuadricula[][] cuadriculas) {
        this.cuadriculas = cuadriculas;
    }

    public void setCuadricula_size(int cuadricula_size) {
        this.cuadricula_size = cuadricula_size;
    }

    public Cuadricula getCuadricula(int i, int j){
        return cuadriculas[i][j];
    }

    public void putCuadricula(Cuadricula cuadricula, int i, int j){
        this.cuadriculas[i][j] = cuadricula;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
