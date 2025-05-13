package com.example.buscaminasbr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.MicroserviceExecutor;
import com.example.buscaminasbr.model.OKHttpMicroserviceExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Inicio_sesion extends AppCompatActivity {

    EditText edt_usuario;
    EditText edt_password;
    TextView tv_sign_up;
    TextView tv_forgot_pass;
    TextView tv_title;
    SwitchCompat sc_debug_mode;
    MicroserviceExecutor microserviceExecutor;
    OKHttpMicroserviceExecutor okHttpMicroserviceExecutor;
    String url = "localhost";
    String host = "localhost";
    Boolean debug_mode = false;
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
        edt_usuario = findViewById(R.id.cc_edt_usuario);
        edt_password = findViewById(R.id.cc_edt_password);
        tv_sign_up = findViewById(R.id.cc_tv_sign_up);
        tv_forgot_pass = findViewById(R.id.cc_tv_forgot_pass);
        tv_title = findViewById(R.id.tv_cc_title);
        sc_debug_mode = findViewById(R.id.is_sc_debug_mode);
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
        sc_debug_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            debug_mode = isChecked;
        });
    }

    private void sign_up(){
        Toast.makeText(this, "Botón sign up pressed", Toast.LENGTH_SHORT).show();
    }
    private void forgot_pass(){
        Toast.makeText(this, "Botón forgot password pressed", Toast.LENGTH_SHORT).show();
    }
    public void sign_in(View v) throws IOException {
        if(debug_mode){
            inicio_rapido();
            return;
        }
        JSONObject datos = new JSONObject();
        try {
            datos.put("id", edt_usuario.getText().toString());
            datos.put("pas", edt_password.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonInputString = "{\"id\": \""+datos.optString("id", "noName")+"\", \"pas\": \""+datos.optString("pas", "nopass")+"\"}";
        //microserviceExecutor.llamarMicroservicioPost("http://192.168.1.12:5101/cuenta/iniciar", this, datos);
        EditText edt_host = findViewById(R.id.cc_edt_host_ip);
        host = edt_host.getText().toString();
        //okHttpMicroserviceExecutor.llamarMicroservicioPost("http://"+host+":5101/cuenta/iniciar", jsonInputString, this);
        url = "http://"+host+":5101/cuenta/iniciar";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String default_respones = "{\"entrada\":"+false+", \"nombre\": \""+"e"+"\"}";
                    String response = OKHttpMicroserviceExecutor.post(url, jsonInputString, default_respones);
                    System.out.println("respuesta: "+response);
                    inicio_sesion_exitoso(response);
                } catch (IOException e) {
                    System.out.println("error en try: "+e);
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public boolean inicio_sesion_exitoso(String data){
        boolean entrada = false;
        String nombre = "";
        JSONObject player_data = new JSONObject();
        String id_player = "";
        String name = "";
        int mmr = -1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            entrada = jsonObject.optBoolean("entrada", false);
            nombre  = jsonObject.optString("nombre", "");
            if(entrada){
                player_data = jsonObject.optJSONObject("player_data");//algo pasa aquí
                //Toast.makeText(this, "player data json: "+jsonObject.toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "player data text: "+data, Toast.LENGTH_SHORT).show();
                assert player_data != null;
                //Toast.makeText(this, "player data: "+player_data.toString(), Toast.LENGTH_SHORT).show();
                name = player_data.optString("name");
                id_player = player_data.optString("id_player");
                mmr = player_data.optInt("mmr");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (entrada) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edt_usuario.setBackgroundColor(Color.TRANSPARENT);
                    edt_password.setBackgroundColor(Color.TRANSPARENT);
                    edt_password.setText("");
                    edt_usuario.setText("");
                }
            });
            Intent intent = new Intent(this, Menu_juego.class);
            intent.putExtra("id_player", id_player);
            intent.putExtra("name", name);
            intent.putExtra("mmr", mmr);
            intent.putExtra("host", host);
            startActivity(intent);
        }else{
            System.out.println("error :"+nombre);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edt_usuario.setBackgroundColor(Color.RED);
                    edt_password.setBackgroundColor(Color.RED);
                }
            });
            //Toast.makeText(this, "error : "+nombre, Toast.LENGTH_SHORT).show();
        }
        return entrada;
    }

    private void inicio_rapido(){
        Intent intent = new Intent(this, Menu_juego.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        microserviceExecutor.cerrarExecutor();
        super.onDestroy();
    }
}