package com.ucenm.recetario_catracho.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.repository.RecetaRepository;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleActivity extends AppCompatActivity {

    private static final int REQUEST_EDITAR_RECETA = 2001;

    // Controles de la interfaz
    private ImageView imgDetalle;

    private TextView txtNombre;
    private TextView txtCategoria;
    private TextView txtDificultad;
    private TextView txtTiempo;
    private TextView txtPorciones;
    private TextView txtIngredientes;
    private TextView txtPasos;

    private Button btnEditar;
    private Button btnEliminar;

    // Identificador de la receta
    private int recetaId;

    // Repositorio
    private RecetaRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle);

        // Inicializar repositorio
        repository = new RecetaRepository();

        // Inicializar vistas
        inicializarVistas();

        // Configurar botones
        configurarBotones();

        // Obtener ID recibido
        recetaId = getIntent().getIntExtra("RECETA_ID", -1);

        if (recetaId <= 0) {

            Toast.makeText(
                    this,
                    "No se recibió un ID válido de la receta.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // Cargar información de la receta
        cargarDetalle();
    }

    // ==========================================
    // INICIALIZAR VISTAS
    // ==========================================

    private void inicializarVistas() {

        imgDetalle = findViewById(R.id.imgDetalle);

        txtNombre = findViewById(R.id.txtNombreDetalle);
        txtCategoria = findViewById(R.id.txtCategoriaDetalle);
        txtDificultad = findViewById(R.id.txtDificultadDetalle);
        txtTiempo = findViewById(R.id.txtTiempoDetalle);
        txtPorciones = findViewById(R.id.txtPorcionesDetalle);
        txtIngredientes = findViewById(R.id.txtIngredientesDetalle);
        txtPasos = findViewById(R.id.txtPasosDetalle);

        btnEditar = findViewById(R.id.btnEditarReceta);
        btnEliminar = findViewById(R.id.btnEliminarReceta);

        Button btnVolver = findViewById(R.id.btnVolverDetalle);

        btnVolver.setOnClickListener(v -> finish());
    }

    // ==========================================
    // CONFIGURAR BOTONES
    // ==========================================

    private void configurarBotones() {

        btnEditar.setOnClickListener(v -> abrirFormularioEdicion());

        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
    }

    // ==========================================
    // ABRIR FORMULARIO DE EDICIÓN
    // ==========================================

    private void abrirFormularioEdicion() {

        Intent intent = new Intent(
                DetalleActivity.this,
                AgregarRecetaActivity.class
        );

        intent.putExtra("MODO_EDICION", true);
        intent.putExtra("RECETA_ID", recetaId);

        startActivityForResult(
                intent,
                REQUEST_EDITAR_RECETA
        );
    }

    // ==========================================
    // CARGAR DETALLE DE LA RECETA
    // ==========================================

    private void cargarDetalle() {

        repository.obtenerReceta(
                recetaId,
                new Callback<Receta>() {

                    @Override
                    public void onResponse(
                            Call<Receta> call,
                            Response<Receta> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            mostrarReceta(response.body());

                        } else {

                            Toast.makeText(
                                    DetalleActivity.this,
                                    "No se encontró la receta. HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Receta> call,
                            Throwable t) {

                        Toast.makeText(
                                DetalleActivity.this,
                                "Error de conexión: " + mensajeError(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // ==========================================
    // MOSTRAR INFORMACIÓN DE LA RECETA
    // ==========================================

    private void mostrarReceta(Receta receta) {

        txtNombre.setText(
                textoSeguro(receta.getNombre())
        );

        txtCategoria.setText(
                "Categoría: " + textoSeguro(receta.getCategoriaNombre())
        );

        txtDificultad.setText(
                "Dificultad: " + nombreDificultad(receta.getDificultad())
        );

        txtTiempo.setText(
                "Tiempo: " + receta.getTiempoPreparacionMin()
                        + " minutos"
        );

        txtPorciones.setText(
                "Porciones: " + receta.getPorciones()
        );

        txtIngredientes.setText(
                textoSeguro(receta.getIngredientes())
        );

        txtPasos.setText(
                textoSeguro(receta.getPasos())
        );

        // Cargar fotografía de la receta
        cargarImagen();
    }

    // ==========================================
    // CARGAR IMAGEN DESDE LA API
    // ==========================================

    private void cargarImagen() {

        // Obtener dirección de la imagen desde RetrofitClient
        String urlImagen = RetrofitClient.getImagenUrl(recetaId);

        // Cargar imagen utilizando Glide
        Glide.with(this)
                .load(urlImagen)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(imgDetalle);
    }

    // ==========================================
    // CONFIRMAR ELIMINACIÓN
    // ==========================================

    private void confirmarEliminacion() {

        new AlertDialog.Builder(this)

                .setTitle("Eliminar receta")

                .setMessage(
                        "¿Estás seguro de que deseas eliminar esta receta? "
                                + "Esta acción no se puede deshacer."
                )

                .setNegativeButton(
                        "CANCELAR",
                        (dialog, which) -> dialog.dismiss()
                )

                .setPositiveButton(
                        "ELIMINAR",
                        (dialog, which) -> eliminarReceta()
                )

                .show();
    }

    // ==========================================
    // ELIMINAR RECETA
    // ==========================================

    private void eliminarReceta() {

        btnEliminar.setEnabled(false);
        btnEliminar.setText("ELIMINANDO...");

        repository.eliminarReceta(
                recetaId,
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    DetalleActivity.this,
                                    "Receta eliminada correctamente.",
                                    Toast.LENGTH_LONG
                            ).show();

                            setResult(RESULT_OK);
                            finish();

                        } else {

                            restaurarBotonEliminar();

                            Toast.makeText(
                                    DetalleActivity.this,
                                    "No se pudo eliminar la receta. HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t) {

                        restaurarBotonEliminar();

                        Toast.makeText(
                                DetalleActivity.this,
                                "Error de conexión al eliminar: "
                                        + mensajeError(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // ==========================================
    // RESTAURAR BOTÓN ELIMINAR
    // ==========================================

    private void restaurarBotonEliminar() {

        btnEliminar.setEnabled(true);
        btnEliminar.setText("ELIMINAR");
    }

    // ==========================================
    // CONVERTIR DIFICULTAD A TEXTO
    // ==========================================

    private String nombreDificultad(int dificultad) {

        switch (dificultad) {

            case 1:
                return "Fácil";

            case 2:
                return "Media";

            case 3:
                return "Difícil";

            default:
                return "No disponible";
        }
    }

    // ==========================================
    // VALIDAR TEXTOS
    // ==========================================

    private String textoSeguro(String valor) {

        return valor == null || valor.trim().isEmpty()
                ? "No disponible"
                : valor;
    }

    // ==========================================
    // MENSAJES DE ERROR
    // ==========================================

    private String mensajeError(Throwable t) {

        return t == null || t.getMessage() == null
                ? "Error desconocido"
                : t.getMessage();
    }

    // ==========================================
    // RECIBIR RESULTADO DE EDICIÓN
    // ==========================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDITAR_RECETA
                && resultCode == RESULT_OK) {

            // Volver a consultar los datos actualizados
            cargarDetalle();
        }
    }
}
