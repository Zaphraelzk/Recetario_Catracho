
package com.ucenm.recetario_catracho.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.adapter.RecetaAdapter;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;
import com.ucenm.recetario_catracho.repository.RecetaRepository;

import java.util.ArrayList;
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

        // Inicializar componentes
        recyclerRecetas = findViewById(R.id.recyclerRecetas);
        edtBuscar = findViewById(R.id.edtBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        progreso = findViewById(R.id.progreso);
        txtEstado = findViewById(R.id.txtEstado);

        // Inicializar repositorio
        repository = new RecetaRepository();

        // Configurar RecyclerView
        recyclerRecetas.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Configurar adaptador
        adapter = new RecetaAdapter(receta -> {

            Intent intent = new Intent(
                    RecetasActivity.this,
                    DetalleActivity.class
            );

            intent.putExtra("RECETA_ID", receta.getId());

            startActivity(intent);
        });

        recyclerRecetas.setAdapter(adapter);

        // Botón buscar
        btnBuscar.setOnClickListener(v -> {
            String busqueda = edtBuscar.getText()
                    .toString()
                    .trim();

            cargarRecetas(busqueda);
        });

        // Botón agregar
        btnAgregar.setOnClickListener(v -> {
            Toast.makeText(
                    RecetasActivity.this,
                    "Formulario próximamente",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Cargar todas las recetas al iniciar
        cargarRecetas("");
    }

    // Obtener recetas desde la API
    private void cargarRecetas(String busqueda) {

        progreso.setVisibility(View.VISIBLE);
        txtEstado.setVisibility(View.GONE);

        repository.obtenerRecetas(
                busqueda,
                new Callback<RespuestaRecetas>() {

                    @Override
                    public void onResponse(
                            Call<RespuestaRecetas> call,
                            Response<RespuestaRecetas> response) {

                        progreso.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            // Obtener la lista de recetas
                            List<Receta> recetas =
                                    response.body().getRecetas();

                            if (recetas == null) {
                                recetas = new ArrayList<>();
                            }

                            // Actualizar el RecyclerView
                            adapter.actualizarLista(recetas);

                            // Mostrar mensaje si no hay resultados
                            if (recetas.isEmpty()) {
                                mostrarEstado(
                                        "No se encontraron recetas."
                                );
                            } else {
                                txtEstado.setVisibility(View.GONE);
                            }

                        } else {

                            mostrarEstado(
                                    "Error al obtener recetas. Código HTTP: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RespuestaRecetas> call,
                            Throwable t) {

                        progreso.setVisibility(View.GONE);

                        String error = t.getLocalizedMessage();

                        if (error == null) {
                            error = "Error desconocido";
                        }

                        mostrarEstado(
                                "No se pudo conectar con el servidor: "
                                        + error
                        );
                    }
                }
        );
    }

    // Mostrar mensajes en pantalla
    private void mostrarEstado(String mensaje) {

        txtEstado.setText(mensaje);
        txtEstado.setVisibility(View.VISIBLE);
    }
}
