package com.example.buscaminasbr;

import android.content.Intent;
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

import com.example.buscaminasbr.model.MicroserviceExecutor;
import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;

import org.json.JSONException;
import org.json.JSONObject;

public class Inicio_sesion extends AppCompatActivity {

    EditText edt_usuario;
    EditText edt_password;
    TextView tv_sign_up;
    TextView tv_forgot_pass;
    TextView tv_title;
    MicroserviceExecutor microserviceExecutor;
    OKHttpMicroserviceExecutor okHttpMicroserviceExecutor;
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
        tv_title = findViewById(R.id.tv_title);
        setListeners();
        microserviceExecutor = new MicroserviceExecutor();
        okHttpMicroserviceExecutor = new OKHttpMicroserviceExecutor();
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
        JSONObject datos = new JSONObject();
        try {
            datos.put("id", edt_usuario.getText().toString());
            datos.put("pas", edt_password.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonInputString = "{\"id\": \""+datos.optString("id", "noName")+"\", \"pas\": "+datos.optString("pas", "nopass")+"}";
        //microserviceExecutor.llamarMicroservicioPost("http://192.168.1.12:5101/cuenta/iniciar", this, datos);
        okHttpMicroserviceExecutor.llamarMicroservicioPost("http://192.168.1.12:5101/cuenta/iniciar", jsonInputString, this);
    }

    public void inicio_sesion_exitoso(String data){
        boolean entrada = false;
        String nombre = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            entrada = jsonObject.optBoolean("entrada", false);
            nombre  = jsonObject.optString("nombre", "");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (entrada) {
            Intent intent = new Intent(this, Menu_juego.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "error : "+data, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        microserviceExecutor.cerrarExecutor();
        super.onDestroy();
    }
}