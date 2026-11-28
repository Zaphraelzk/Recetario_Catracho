package com.ucenm.recetario_catracho;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ucenm.recetario_catracho.activities.AgregarRecetaActivity;
import com.ucenm.recetario_catracho.activities.RecetasActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnVerRecetas;
    private Button btnAgregarReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar botones
        btnVerRecetas = findViewById(R.id.btnVerRecetas);
        btnAgregarReceta = findViewById(R.id.btnAgregarReceta);

        // Abrir la lista de recetas
        btnVerRecetas.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecetasActivity.class
            );
            startActivity(intent);
        });

        // Abrir el formulario para agregar recetas
        btnAgregarReceta.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AgregarRecetaActivity.class
            );
            startActivity(intent);
        });
    }
}
