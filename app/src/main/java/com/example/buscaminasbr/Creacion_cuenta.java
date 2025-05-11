package com.example.buscaminasbr;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Creacion_cuenta extends AppCompatActivity {

    EditText edt_usuario;
    EditText edt_password;
    EditText edt_nombre;
    EditText edt_server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creacion_cuenta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edt_nombre = findViewById(R.id.cc_edt_nombre);
        edt_usuario = findViewById(R.id.cc_edt_usuario);
        edt_password = findViewById(R.id.cc_edt_password);
        edt_server = findViewById(R.id.cc_edt_host_ip);
    }

    public void crear_cuenta(View v){}
}