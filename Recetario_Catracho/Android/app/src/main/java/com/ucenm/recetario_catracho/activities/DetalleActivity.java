
package com.ucenm.recetario_catracho.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleActivity extends AppCompatActivity {

    private ImageView imgDetalle;
    private TextView txtNombre;
    private TextView txtCategoria;
    private TextView txtDificultad;
    private TextView txtTiempo;
    private TextView txtPorciones;
    private TextView txtIngredientes;
    private TextView txtPasos;

    private int recetaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        // Obtener los controles de la pantalla
        imgDetalle = findViewById(R.id.imgDetalle);
        txtNombre = findViewById(R.id.txtNombreDetalle);
        txtCategoria = findViewById(R.id.txtCategoriaDetalle);
        txtDificultad = findViewById(R.id.txtDificultadDetalle);
        txtTiempo = findViewById(R.id.txtTiempoDetalle);
        txtPorciones = findViewById(R.id.txtPorcionesDetalle);
        txtIngredientes = findViewById(R.id.txtIngredientesDetalle);
        txtPasos = findViewById(R.id.txtPasosDetalle);

        Button btnVolver = findViewById(R.id.btnVolverDetalle);

        btnVolver.setOnClickListener(v -> finish());

        // Recibir el ID enviado desde la lista
        recetaId = getIntent().getIntExtra("RECETA_ID", -1);

        if (recetaId == -1) {
            Toast.makeText(this,
                    "No se recibió el ID de la receta",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarDetalle();
    }

    private void cargarDetalle() {

        RetrofitClient.getApiService()
                .obtenerRecetaPorId(recetaId)
                .enqueue(new Callback<Receta>() {

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
                                    "No se encontró la receta",
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
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void mostrarReceta(Receta receta) {

        txtNombre.setText(receta.getNombre());

        txtCategoria.setText(
                "Categoría: " + textoSeguro(receta.getCategoriaNombre())
        );

        txtDificultad.setText(
                "Dificultad: " + textoSeguro(receta.getDificultad())
        );

        txtTiempo.setText(
                "Tiempo: " + receta.getTiempoPreparacionMin() + " minutos"
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

        String imagen = receta.getImagenRuta();

        if (imagen != null && !imagen.trim().isEmpty()) {
            Glide.with(this)
                    .load(imagen)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgDetalle);
        }
    }

    private String textoSeguro(String valor) {
        return valor == null || valor.trim().isEmpty()
                ? "No disponible"
                : valor;
    }
}
