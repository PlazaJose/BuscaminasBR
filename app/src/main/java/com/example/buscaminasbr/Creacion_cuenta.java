package com.example.buscaminasbr;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Struct;

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

    public void crear_cuenta(View v){
        if(!all_fields_fill()){
            return;
        }

        String host = edt_server.getText().toString();
        String name = edt_nombre.getText().toString();
        String user = edt_usuario.getText().toString();
        String pass = edt_password.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonInputString = String.format(
                            "{\"id\":\"%s\", \"pas\":\"%s\", \"nombre\":\"%s\"}"
                            ,user, pass, name);
                    String url = "http://"+host+":5101/cuenta/crear";
                    String default_respones = "{ \"entrada\": false, \"estado\": 'usuario no creado' }";
                    String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_respones);
                    JSONObject json_response = new JSONObject(response);
                    if(json_response.getBoolean("entrada")){
                        exit();
                    }else {
                        clean();
                    }
                    System.out.println("response on create account: "+response);
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private boolean all_fields_fill(){
        boolean filled = true;
        if(edt_server.getText().toString().isEmpty()){
            filled = false;
            edt_server.setBackgroundColor(Color.RED);
        }
        if(edt_nombre.getText().toString().isEmpty()){
            filled = false;
            edt_nombre.setBackgroundColor(Color.RED);
        }
        if(edt_usuario.getText().toString().isEmpty()){
            filled = false;
            edt_usuario.setBackgroundColor(Color.RED);
        }
        if(edt_password.getText().toString().isEmpty()){
            filled = false;
            edt_password.setBackgroundColor(Color.RED);
        }
        return filled;
    }
    void exit(){
        clean();
        finish();
    }
    void clean(){
        edt_password.setText("");
        edt_usuario.setText("");
        edt_nombre.setText("");
        edt_server.setText("");
        edt_password.setBackgroundColor(Color.TRANSPARENT);
        edt_usuario.setBackgroundColor(Color.TRANSPARENT);
        edt_nombre.setBackgroundColor(Color.TRANSPARENT);
        edt_server.setBackgroundColor(Color.TRANSPARENT);
    }
}