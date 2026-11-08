package com.ucenm.recetario_catracho.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.adapter.RecetaAdapter;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.repository.RecetaRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetasActivity extends AppCompatActivity {

    private RecyclerView recyclerRecetas;
    private EditText edtBuscar;
    private Button btnBuscar;
    private Button btnAgregar;
    private ProgressBar progreso;
    private TextView txtEstado;

    private RecetaAdapter adapter;
    private RecetaRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        recyclerRecetas = findViewById(R.id.recyclerRecetas);
        edtBuscar = findViewById(R.id.edtBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        progreso = findViewById(R.id.progreso);
        txtEstado = findViewById(R.id.txtEstado);

        repository = new RecetaRepository();

        recyclerRecetas.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RecetaAdapter(receta -> {

            Intent intent = new Intent(
                    RecetasActivity.this,
                    DetalleActivity.class
            );

            intent.putExtra("RECETA_ID", receta.getId());

            startActivity(intent);

        });


        recyclerRecetas.setAdapter(adapter);

        btnBuscar.setOnClickListener(v ->
                cargarRecetas(edtBuscar.getText().toString().trim())
        );

        btnAgregar.setOnClickListener(v -> {
            // Próximamente abriremos el formulario.
            android.widget.Toast.makeText(
                    this,
                    "Formulario próximamente",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        });

        cargarRecetas("");
    }

    private void cargarRecetas(String busqueda) {

        progreso.setVisibility(View.VISIBLE);
        txtEstado.setVisibility(View.GONE);

        repository.obtenerRecetas(
                busqueda,
                new Callback<List<Receta>>() {

                    @Override
                    public void onResponse(
                            Call<List<Receta>> call,
                            Response<List<Receta>> response) {

                        progreso.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Receta> recetas = response.body();

                            adapter.actualizarLista(recetas);

                            if (recetas.isEmpty()) {
                                mostrarEstado("No hay recetas registradas.");
                            }

                        } else {
                            mostrarEstado(
                                    "Error al obtener recetas. Código: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Receta>> call,
                            Throwable t) {

                        progreso.setVisibility(View.GONE);

                        mostrarEstado(
                                "No se pudo conectar con el servidor. "
                                        + "Verifica la API y tu conexión."
                        );
                    }
                }
        );
    }

    private void mostrarEstado(String mensaje) {
        txtEstado.setText(mensaje);
        txtEstado.setVisibility(View.VISIBLE);
    }
}
