package com.example.buscaminasbr.controller;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.buscaminasbr.view.Cuadricula;

import java.util.Random;

public class Cuadricula_controlador implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(!is_long_press){
                Random random = new Random();
                this.cuadricula.change_collor(random.nextInt(), random.nextInt(), random.nextInt());
                this.cuadricula.activate();
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            is_long_press = false;
        }
        return false;
    }
    Cuadricula cuadricula;
    private GestureDetector gestureDetector;
    boolean is_long_press = false;
    public Cuadricula_controlador(Cuadricula cuadricula){
        this.cuadricula = cuadricula;
        gestureDetector = new GestureDetector(cuadricula.getContext(), new GestureListener());
    }

    // Define a GestureListener to handle long press events
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            // Handle long press action
            super.onLongPress(e);
            Random random = new Random();
            is_long_press = true;
            // Perform a different action on long press, such as changing the color again or activating a different functionality
            cuadricula.change_collor(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // Ensure color values are within the valid range
            cuadricula.setiEstado(Cuadricula.ESTADO_BANDERA); // Example of a different action
        }
    }

}