package com.ucenm.recetario_catracho;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ucenm.recetario_catracho.activities.RecetasActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnVerRecetas;
    private Button btnAgregarReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnVerRecetas = findViewById(R.id.btnVerRecetas);
        btnAgregarReceta = findViewById(R.id.btnAgregarReceta);

        btnVerRecetas.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecetasActivity.class
            );

            startActivity(intent);
        });

        btnAgregarReceta.setOnClickListener(v -> {
            android.widget.Toast.makeText(
                    MainActivity.this,
                    "El formulario estará disponible próximamente.",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        });
    }
}
