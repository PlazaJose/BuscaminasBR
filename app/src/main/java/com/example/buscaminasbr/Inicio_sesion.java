package com.example.buscaminasbr;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio_sesion extends AppCompatActivity {

    EditText edt_usuario;
    EditText edt_password;
    TextView tv_sign_up;
    TextView tv_forgot_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edt_usuario = findViewById(R.id.is_edt_usuario);
        edt_password = findViewById(R.id.is_edt_password);
        tv_sign_up = findViewById(R.id.is_tv_sign_up);
        tv_forgot_pass = findViewById(R.id.is_tv_forgot_pass);
        setListeners();
    }
    private void setListeners(){
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_up();
            }
        });
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot_pass();
            }
        });
    }

    private void sign_up(){
        Toast.makeText(this, "Botón sign up pressed", Toast.LENGTH_SHORT).show();
    }
    private void forgot_pass(){
        Toast.makeText(this, "Botón forgot password pressed", Toast.LENGTH_SHORT).show();
    }
    public void sign_in(View v){
        Toast.makeText(this, "botón iniciar sesión presionado", Toast.LENGTH_SHORT).show();
    }
}