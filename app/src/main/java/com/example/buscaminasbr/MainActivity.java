package com.example.buscaminasbr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.Map;

public class MainActivity extends AppCompatActivity {

    GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initiate();
    }

    private void initiate(){
        //Intent intent = new Intent(this, Menu_juego.class);
        Intent intent = new Intent(this, Inicio_sesion.class);
        startActivity(intent);
    }
}