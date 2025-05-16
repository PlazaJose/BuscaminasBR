package com.example.buscaminasbr.view;

import android.content.Context;
import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.GridLayout.LayoutParams;

import com.example.buscaminasbr.controller.Cuadricula_controlador;
import com.example.buscaminasbr.model.Map;

public class Cuadricula extends androidx.appcompat.widget.AppCompatButton {
    public static final int ESTADO_CERRADO=0;
    public static final int ESTADO_EXPLOTADO=1;
    public static final int ESTADO_BANDERA=2;
    public static final int ESTADO_ABIERTO=3;
    public static final int ESTADO_INTERROGANTE=4;
    public static final int ESTADO_BULNERADO=5;
    public static final int ESTADO_ERRADO=6;
    public static final int TIPO_SEGURO=0;
    public static final int TIPO_MINADO=1;

    int iEstado=-1;
    int iTipo=-1;
    int iMinas=-1;

    int row = -1;
    public int column = -1;
    int width;
    int height;
    Map map;
/**
 * El objeto principal con el que se interactúa en el juego.
 * @param context the context. container.
 * @param row the row.
 * @param column the column.
 * @param map the map
 */
    public Cuadricula(Context context, int row, int column, int width, int height, Map map){
        super(context);
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
        this.map = map;

        set_up();
    }

    private void set_up(){
        LayoutParams params = new LayoutParams();
        params.width = this.width;
        params.height = this.height;
        setLayoutParams(params);
        setOnTouchListener(new Cuadricula_controlador(this));
        reset();
    }

    public void reset(){

        change_collor(100, 100, 100);
        setiEstado(Cuadricula.ESTADO_CERRADO);
        setiTipo(Cuadricula.TIPO_SEGURO);
        setiMinas(0);

        setText("");
    }

    public void change_collor(int r, int g, int b){
        setBackgroundColor(Color.argb(150, r, g, b));
    }

    public void activate(){
        switch (getiEstado()){
            case Cuadricula.ESTADO_ABIERTO:
                map.update_vecinos(row, column);
                break;
            case Cuadricula.ESTADO_CERRADO:
                if (!map.first_update(row, column)) {
                    String texto = (getiTipo()==Cuadricula.TIPO_MINADO?"*":""+getiMinas());
                    setText(texto);
                    setiEstado((getiTipo()==Cuadricula.TIPO_MINADO?Cuadricula.ESTADO_EXPLOTADO:Cuadricula.ESTADO_ABIERTO));
                    map.update_vecinos(row, column);
                    map.check_win(row, column);
                }
                break;
            case Cuadricula.ESTADO_BANDERA:
                setText("?");
                setiEstado(Cuadricula.ESTADO_INTERROGANTE);
                break;
            case Cuadricula.ESTADO_INTERROGANTE:
                setText("");
                setiEstado(Cuadricula.ESTADO_CERRADO);
                break;
            case Cuadricula.ESTADO_EXPLOTADO:
                break;
            case -1:
                setiEstado(Cuadricula.ESTADO_CERRADO);
                break;
            default:
                setiEstado(Cuadricula.ESTADO_CERRADO);
                break;
        }
        map.plan_a_push_move(row, column, getiEstado());

    }
    public void update(int mines){
        setiMinas(mines);
        switch (getiEstado()){
            case Cuadricula.ESTADO_ABIERTO:
                String texto = (getiTipo()==Cuadricula.TIPO_MINADO?"*":""+getiMinas());
                setText(texto);
                break;
            case Cuadricula.ESTADO_CERRADO:
                setText("");
                break;
            case Cuadricula.ESTADO_BANDERA:
                setText("S");
                break;
            case Cuadricula.ESTADO_INTERROGANTE:
                setText("?");
                break;
            default:
                break;
        }
    }
    public void setiEstado(int new_estado){
        this.iEstado = new_estado;
        switch (getiEstado()){
            case Cuadricula.ESTADO_ABIERTO:
                change_collor(100, 50, 100);
                break;
            case Cuadricula.ESTADO_CERRADO:
                change_collor(100, 100, 100);
                break;
            case Cuadricula.ESTADO_BANDERA:
                change_collor(100, 150, 150);
                setText("S");
                break;
            case Cuadricula.ESTADO_INTERROGANTE:
                change_collor(100, 150, 100);
                break;
            case Cuadricula.ESTADO_EXPLOTADO:
                change_collor(100, 0, 0);
                break;
            case -1:
                change_collor(110, 110, 100);
                break;
            default:
                change_collor(100, 110, 100);
                break;
        }
    }
    public int getiEstado(){
        return this.iEstado;
    }
    /**
     * Sets the new type and returns true if it was diferent, false if it was the same.
     *
     * @param new_tipo el nuevo tipo
     * @return Retorna si hubo cambio o no.
     */
    public Boolean setiTipo(int new_tipo){
        Boolean retorno = this.iTipo != new_tipo;
        this.iTipo = new_tipo;
        return retorno;
    }
    public int getiTipo(){
        return this.iTipo;
    }

    public void setiMinas(int iMinas) {
        this.iMinas = iMinas;
    }

    public int getiMinas() {
        return iMinas;
    }
}
