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

        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        // Center the GridLayout using layout_centerInParent
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        // Add the GridLayout to the RelativeLayout
        relativeLayout.addView(gridLayout);

        // Set the RelativeLayout as the content view of the activity
        //setContentView(relativeLayout);
        return relativeLayout;
    }
}