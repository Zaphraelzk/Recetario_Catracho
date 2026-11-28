
package com.ucenm.recetario_catracho.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.adapter.RecetaAdapter;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;
import com.ucenm.recetario_catracho.repository.RecetaRepository;

import org.json.JSONObject;

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
    private Button btnReintentar;

    private ProgressBar progreso;
    private TextView txtEstado;
    private Spinner spinnerCategorias;

    private RecetaAdapter adapter;
    private RecetaRepository repository;

    private Integer categoriaSeleccionada = null;

    private boolean primeraCarga = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recetas);

        inicializarVistas();

        repository = new RecetaRepository();

        configurarRecyclerView();
        configurarBotones();
        configurarSpinner();

        cargarCategorias();
    }

    // Inicializar controles.
    private void inicializarVistas() {

        recyclerRecetas = findViewById(R.id.recyclerRecetas);
        edtBuscar = findViewById(R.id.edtBuscar);

        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnReintentar = findViewById(R.id.btnReintentar);

        progreso = findViewById(R.id.progreso);
        txtEstado = findViewById(R.id.txtEstado);

        spinnerCategorias = findViewById(R.id.spinnerCategorias);
    }

    // Configurar RecyclerView.
    private void configurarRecyclerView() {

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
    }

    // Configurar botones.
    private void configurarBotones() {

        btnBuscar.setOnClickListener(v -> {

            String busqueda = edtBuscar.getText()
                    .toString()
                    .trim();

            cargarRecetas(busqueda);
        });

        btnAgregar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecetasActivity.this,
                    AgregarRecetaActivity.class
            );

            startActivity(intent);
        });

        btnReintentar.setOnClickListener(v -> {

            btnReintentar.setVisibility(View.GONE);

            cargarCategorias();

            String busqueda = edtBuscar.getText()
                    .toString()
                    .trim();

            cargarRecetas(busqueda);
        });
    }

    // Configurar filtro de categorías.
    private void configurarSpinner() {

        spinnerCategorias.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        Categoria categoria =
                                (Categoria) parent.getItemAtPosition(position);

                        Integer nuevaCategoria =
                                categoria.getId() == 0
                                        ? null
                                        : categoria.getId();

                        if (primeraCarga) {
                            primeraCarga = false;
                            categoriaSeleccionada = nuevaCategoria;
                            return;
                        }

                        if (!java.util.Objects.equals(
                                categoriaSeleccionada,
                                nuevaCategoria
                        )) {

                            categoriaSeleccionada = nuevaCategoria;

                            String busqueda = edtBuscar.getText()
                                    .toString()
                                    .trim();

                            cargarRecetas(busqueda);
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    // Consultar categorías en el backend.
    private void cargarCategorias() {

        repository.obtenerCategorias(
                new Callback<List<Categoria>>() {

                    @Override
                    public void onResponse(
                            Call<List<Categoria>> call,
                            Response<List<Categoria>> response
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Categoria> categorias =
                                    new ArrayList<>();

                            categorias.add(
                                    new Categoria(
                                            0,
                                            "Todas las categorías"
                                    )
                            );

                            categorias.addAll(response.body());

                            ArrayAdapter<Categoria> spinnerAdapter =
                                    new ArrayAdapter<>(
                                            RecetasActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            categorias
                                    );

                            spinnerAdapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spinnerCategorias.setAdapter(spinnerAdapter);

                        } else {

                            mostrarError(
                                    "No se pudieron cargar las categorías. "
                                            + mensajeErrorHttp(response)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Categoria>> call,
                            Throwable t
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        mostrarError(
                                "No se pudieron cargar las categorías. "
                                        + "Comprueba tu conexión."
                        );
                    }
                }
        );
    }

    // Consultar recetas con búsqueda y categoría.
    private void cargarRecetas(String busqueda) {

        progreso.setVisibility(View.VISIBLE);

        txtEstado.setVisibility(View.GONE);
        btnReintentar.setVisibility(View.GONE);

        repository.obtenerRecetas(
                busqueda,
                categoriaSeleccionada,
                new Callback<RespuestaRecetas>() {

                    @Override
                    public void onResponse(
                            Call<RespuestaRecetas> call,
                            Response<RespuestaRecetas> response
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        progreso.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Receta> recetas =
                                    response.body().getRecetas();

                            if (recetas == null) {
                                recetas = new ArrayList<>();
                            }

                            adapter.actualizarLista(recetas);

                            if (recetas.isEmpty()) {

                                mostrarEstado(
                                        "No se encontraron recetas."
                                );

                            } else {

                                txtEstado.setVisibility(View.GONE);
                            }

                        } else {

                            mostrarError(
                                    "Error al obtener recetas. "
                                            + mensajeErrorHttp(response)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RespuestaRecetas> call,
                            Throwable t
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        progreso.setVisibility(View.GONE);

                        mostrarError(
                                "No se pudo conectar con el servidor. "
                                        + "Comprueba tu conexión e inténtalo nuevamente."
                        );
                    }
                }
        );
    }

    // Obtener el mensaje correspondiente al error HTTP.
    private String mensajeErrorHttp(Response<?> response) {

        String mensajeServidor = "";

        try {

            if (response.errorBody() != null) {

                String contenido = response.errorBody().string();

                JSONObject json = new JSONObject(contenido);

                mensajeServidor = json.optString("mensaje", "");
            }

        } catch (Exception ignored) {
            // Se utilizará el mensaje general del código HTTP.
        }

        String mensaje;

        switch (response.code()) {

            case 400:
                mensaje = "Solicitud incorrecta. Verifica los datos enviados.";
                break;

            case 404:
                mensaje = "No se encontró la receta o el recurso solicitado.";
                break;

            case 409:
                mensaje = "Existe un conflicto con los datos registrados.";
                break;

            default:
                mensaje = "Error del servidor. Código HTTP: "
                        + response.code();
                break;
        }

        if (!mensajeServidor.isEmpty()) {
            mensaje += "\n" + mensajeServidor;
        }

        return mensaje;
    }

    // Mostrar un mensaje informativo.
    private void mostrarEstado(String mensaje) {

        txtEstado.setText(mensaje);
        txtEstado.setVisibility(View.VISIBLE);

        btnReintentar.setVisibility(View.GONE);
    }

    // Mostrar error y habilitar reintento.
    private void mostrarError(String mensaje) {

        txtEstado.setText(mensaje);
        txtEstado.setVisibility(View.VISIBLE);

        btnReintentar.setVisibility(View.VISIBLE);
    }

    // Actualizar recetas cuando se regresa a la pantalla.
    @Override
    protected void onResume() {

        super.onResume();

        if (repository != null && edtBuscar != null) {

            String busqueda = edtBuscar.getText()
                    .toString()
                    .trim();

            cargarRecetas(busqueda);
        }
    }
}
