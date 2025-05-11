package com.example.buscaminasbr.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.buscaminasbr.controller.MiniBM_controlador;

import java.security.SecureRandom;
import java.util.Random;

public class MiniBM extends androidx.appcompat.widget.AppCompatTextView {
    private Paint paint;
    int width_map;
    int height_map;
    int width_cuad;
    int height_cuad;
    float c_w;
    float c_h;
    SecureRandom random;
    int[][] colors;
    int [] state_colors = {Color.GREEN, Color.BLACK};
    int[][] map;
    public MiniBM(@NonNull Context context) {
        super(context);
        init();
    }
    private void init(){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        width_map = 10;
        height_map = 10;
        width_cuad = 100;
        height_cuad = 100;
        c_w = (float) width_cuad /width_map;
        c_h = (float) height_cuad /height_map;
        random = new SecureRandom();
        set_up();

        setOnTouchListener(new MiniBM_controlador(this));
    }

    private void set_up(){
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = width_cuad;
        params.height = height_cuad;
        params.bottomMargin = 2;
        params.leftMargin = 2;
        setLayoutParams(params);
        colors = new int[width_map][height_map];
        map = new int[width_map][height_map];
        String states_str = "";
        for(int i = 0; i < width_map; i++){
            for(int j = 0; j < height_map; j++){
                int i_state = random.nextInt(10);
                map[i][j] = 1;//(i_state<8?1:0);
                colors[i][j]= Color.argb(150, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            }
        }
        randomize_mines();
    }

    public void randomize_mines(){
        for(int i = 0; i < width_map; i++){
            for(int j = 0; j < height_map; j++){
                map[i][j] = 1;
            }
        }
        for(int i = 0; i<10; i++){
            map[random.nextInt(10)][random.nextInt(10)] = 0;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawLine(0,0, getWidth(), getHeight(), paint);
        for(int i = 0; i < width_map; i++){
            for(int j = 0; j < height_map; j++){
                //paint.setColor(colors[i][j]);
                paint.setColor(state_colors[map[i][j]]);
                canvas.drawRect((c_w*i),(c_h*j), c_w*(i+1), c_h*(j+1), paint);

                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(1);

                canvas.drawLine(c_w * i, c_h * j, c_w * (i + 1), c_h * j, paint); // Línea superior
                canvas.drawLine(c_w * (i + 1), c_h * j, c_w * (i + 1), c_h * (j + 1), paint); // Derecha
                canvas.drawLine(c_w * i, c_h * (j + 1), c_w * (i + 1), c_h * (j + 1), paint); // Inferior
                canvas.drawLine(c_w * i, c_h * j, c_w * i, c_h * (j + 1), paint); // Izquierda
            }
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);

        canvas.drawLine(0, 0, getWidth(), 0, paint); // Línea superior
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paint); // Derecha
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint); // Inferior
        canvas.drawLine(0, 0, 0, getHeight(), paint); // Izquierda
    }
}
