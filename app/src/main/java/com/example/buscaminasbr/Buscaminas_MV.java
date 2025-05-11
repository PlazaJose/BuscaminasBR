package com.example.buscaminasbr;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.buscaminasbr.model.Map;
import com.example.buscaminasbr.view.MiniBM;

public class Buscaminas_MV extends AppCompatActivity {

    GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscaminas_mv);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(get_map());
    }

    public RelativeLayout get_map(){
        int map_width = 10;
        int map_height = 10;

        gridLayout = new GridLayout(this);
        gridLayout.setRowCount(map_width);
        gridLayout.setColumnCount(map_height);

        Map map = new Map(this, map_width, map_height, 10);
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                gridLayout.addView(map.getCuadricula(i, j));
            }
        }


        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        //GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        // Center the GridLayout using layout_centerInParent
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.topMargin = 250;

        //create enemy viewa
        MiniBM miniBM = new MiniBM(this);
        GridLayout gridLayout_enemys = new GridLayout(this);
        gridLayout_enemys.setRowCount(3);
        gridLayout_enemys.setColumnCount(3);
        gridLayout_enemys.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams layoutParams_enemy = (RelativeLayout.LayoutParams) gridLayout_enemys.getLayoutParams();
        layoutParams_enemy.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams_enemy.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams_enemy.bottomMargin = 250;

        for(int i = 0; i < 9; i++){
            MiniBM mbm = new MiniBM(this);
            gridLayout_enemys.addView(mbm);
        }

        //gridLayout_enemys.addView(miniBM);
        //add enemy views to relativelayout
        relativeLayout.addView(gridLayout_enemys);
        // Add the GridLayout to the RelativeLayout
        relativeLayout.addView(gridLayout);

        return relativeLayout;
    }
}