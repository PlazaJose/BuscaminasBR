package com.example.buscaminasbr.model;

import android.content.Context;
import android.widget.Toast;

import com.example.buscaminasbr.view.Cuadricula;

import java.util.Random;

public class Map {
    int width;
    int height;
    int mines;
    Cuadricula[][] cuadriculas;

    int cuadricula_size = 100;
    int opened = 0;
    boolean game_started = false;
    public Map(Context context, int width, int height, int mines){
        this.width = width;
        this.height = height;
        int area = width*height;
        this.mines = correct_position(mines, (int) Math.floor((10 * area) / 100.0),(int) Math.floor((40 * area) / 100.0));
        this.cuadriculas = new Cuadricula[width][height];
        set_up(context);
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
        update_cuadriculas();
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

    private void set_mines(int si, int sj){
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
                cuadriculas[i][j].setiMinas(mines);
            }
        }
    }

    private Cuadricula[][] get_vecinos(int i, int j){
        Cuadricula[][] vecinos = new Cuadricula[3][3];
        for(int vi = -1; vi < 2; vi++){
            for(int vj = -1; vj < 2; vj++){
                int ni = i+vi;
                int nj = j+vj;
                boolean validate = is_betwin(ni, -1, this.width)&&is_betwin(nj, -1, this.height);
                vecinos[vi+1][vj+1] = (validate?cuadriculas[ni][nj]:null);
            }
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

    public void first_update(int i, int j){
        if(!game_started){
            start_game(i, j);
        }
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
            reset();
            return true;
        }
        return false;
    }
    private void win(){
        opened++;
        if(opened+mines==width*height){
            Toast.makeText(cuadriculas[0][0].getContext(), "You Won", Toast.LENGTH_SHORT).show();
            reset();
        }
    }

    public void check_win(int i, int j){
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
