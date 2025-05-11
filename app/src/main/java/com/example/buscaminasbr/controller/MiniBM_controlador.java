package com.example.buscaminasbr.controller;

import android.view.MotionEvent;
import android.view.View;

import com.example.buscaminasbr.view.MiniBM;

public class MiniBM_controlador implements View.OnTouchListener {
    MiniBM miniBM;
    public MiniBM_controlador(MiniBM miniBM){
        this.miniBM = miniBM;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        miniBM.randomize_mines();
        return false;
    }
}
