package com.ucenm.recetario_catracho.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.model.ActualizarRecetaRequest;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.CrearRecetaRequest;
import com.ucenm.recetario_catracho.model.ImagenBase64Request;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.repository.RecetaRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarRecetaActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etDescripcion;
    private EditText etTiempo;
    private EditText etPorciones;
    private EditText etIngredientes;
    private EditText etPasos;

    private Spinner spCategoria;
    private Spinner spDificultad;

    private ImageView ivImagen;
    private TextView tvVideoSeleccionado;

    private Button btnGuardarReceta;
    private Button btnSeleccionarImagen;
    private Button btnSeleccionarVideo;
    private Button btnCancelar;

    private Uri imagenUri;
    private Uri videoUri;

    private RecetaRepository repository;
    private final List<Categoria> categorias = new ArrayList<>();

    private ActivityResultLauncher<String> seleccionarImagen;
    private ActivityResultLauncher<String> seleccionarVideo;

    private boolean modoEdicion;
    private boolean categoriasCargadas;
    private boolean recetaCargada;
    private int recetaId = -1;
    private Receta recetaOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_receta);

        repository = new RecetaRepository();
        modoEdicion = getIntent().getBooleanExtra("MODO_EDICION", false);
        recetaId = getIntent().getIntExtra("RECETA_ID", -1);

        inicializarVistas();
        configurarSelectores();
        configurarDificultad();
        configurarBotones();

        if (modoEdicion) {
            if (recetaId <= 0) {
                mostrarMensaje("No se recibió un ID válido para editar.");
                finish();
                return;
            }
            btnGuardarReceta.setText("GUARDAR CAMBIOS");
            cargarRecetaParaEditar();
        }

        cargarCategorias();
    }

    private void inicializarVistas() {
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etTiempo = findViewById(R.id.etTiempo);
        etPorciones = findViewById(R.id.etPorciones);
        etIngredientes = findViewById(R.id.etIngredientes);
        etPasos = findViewById(R.id.etPasos);

        spCategoria = findViewById(R.id.spCategoria);
        spDificultad = findViewById(R.id.spDificultad);

        ivImagen = findViewById(R.id.ivImagen);
        tvVideoSeleccionado = findViewById(R.id.tvVideoSeleccionado);

        btnGuardarReceta = findViewById(R.id.btnGuardarReceta);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarVideo = findViewById(R.id.btnSeleccionarVideo);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void configurarSelectores() {
        seleccionarImagen = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagenUri = uri;
                        ivImagen.setImageURI(uri);
                        ivImagen.setVisibility(View.VISIBLE);
                    }
                }
        );

        seleccionarVideo = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        videoUri = uri;
                        tvVideoSeleccionado.setText(
                                "Video seleccionado: " + obtenerNombreArchivo(uri)
                        );
                    }
                }
        );
    }

    private void configurarDificultad() {
        String[] dificultades = {"Fácil", "Media", "Difícil"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dificultades
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adapter);
    }

    private void configurarBotones() {
        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen.launch("image/*"));
        btnSeleccionarVideo.setOnClickListener(v -> seleccionarVideo.launch("video/*"));
        btnGuardarReceta.setOnClickListener(v -> validarFormulario());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarCategorias() {
        repository.obtenerCategorias(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(
                    Call<List<Categoria>> call,
                    Response<List<Categoria>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias.clear();
                    categorias.addAll(response.body());

                    ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                            AgregarRecetaActivity.this,
                            android.R.layout.simple_spinner_item,
                            categorias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategoria.setAdapter(adapter);

                    categoriasCargadas = true;
                    aplicarDatosDeEdicionCuandoEsténListos();
                } else {
                    mostrarMensaje("No se pudieron cargar las categorías. HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                mostrarMensaje("Error cargando categorías: " + mensajeError(t));
            }
        });
    }

    private void cargarRecetaParaEditar() {
        repository.obtenerReceta(recetaId, new Callback<Receta>() {
            @Override
            public void onResponse(Call<Receta> call, Response<Receta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recetaOriginal = response.body();
                    recetaCargada = true;
                    aplicarDatosDeEdicionCuandoEsténListos();
                } else {
                    mostrarMensaje("No se pudo cargar la receta para editar. HTTP " + response.code());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Receta> call, Throwable t) {
                mostrarMensaje("Error cargando la receta: " + mensajeError(t));
                finish();
            }
        });
    }

    // Se espera a que estén listas tanto la receta como las categorías.
    private void aplicarDatosDeEdicionCuandoEsténListos() {
        if (!modoEdicion || !categoriasCargadas || !recetaCargada || recetaOriginal == null) {
            return;
        }

        etNombre.setText(valorSeguro(recetaOriginal.getNombre()));
        etDescripcion.setText(valorSeguro(recetaOriginal.getDescripcion()));
        etTiempo.setText(String.valueOf(recetaOriginal.getTiempoPreparacionMin()));
        etPorciones.setText(String.valueOf(recetaOriginal.getPorciones()));
        etIngredientes.setText(valorSeguro(recetaOriginal.getIngredientes()));
        etPasos.setText(valorSeguro(recetaOriginal.getPasos()));

        int dificultadPosition = recetaOriginal.getDificultad() - 1;
        if (dificultadPosition >= 0 && dificultadPosition < spDificultad.getCount()) {
            spDificultad.setSelection(dificultadPosition);
        }

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == recetaOriginal.getCategoriaId()) {
                spCategoria.setSelection(i);
                break;
            }
        }

        String rutaImagen = recetaOriginal.getImagenRuta();
        if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
            ivImagen.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(rutaImagen)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivImagen);
        }

        String rutaVideo = recetaOriginal.getVideoRuta();
        if (rutaVideo != null && !rutaVideo.trim().isEmpty()) {
            tvVideoSeleccionado.setText("Esta receta ya tiene un video. Selecciona otro solamente si deseas reemplazarlo.");
        }
    }

    private void validarFormulario() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String tiempo = etTiempo.getText().toString().trim();
        String porciones = etPorciones.getText().toString().trim();
        String ingredientes = etIngredientes.getText().toString().trim();
        String pasos = etPasos.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa el nombre");
            etNombre.requestFocus();
            return;
        }
        if (descripcion.isEmpty()) {
            etDescripcion.setError("Ingresa una descripción");
            etDescripcion.requestFocus();
            return;
        }
        if (categorias.isEmpty() || spCategoria.getSelectedItem() == null) {
            mostrarMensaje("No hay categorías disponibles.");
            return;
        }
        if (tiempo.isEmpty()) {
            etTiempo.setError("Ingresa el tiempo");
            etTiempo.requestFocus();
            return;
        }
        if (porciones.isEmpty()) {
            etPorciones.setError("Ingresa las porciones");
            etPorciones.requestFocus();
            return;
        }
        if (ingredientes.isEmpty()) {
            etIngredientes.setError("Ingresa los ingredientes");
            etIngredientes.requestFocus();
            return;
        }
        if (pasos.isEmpty()) {
            etPasos.setError("Ingresa los pasos");
            etPasos.requestFocus();
            return;
        }

        if (nombre.length() > 100 || descripcion.length() > 500
                || ingredientes.length() > 2000 || pasos.length() > 4000) {
            mostrarMensaje("Revisa la longitud de los campos.");
            return;
        }

        int minutos;
        int cantidadPorciones;
        try {
            minutos = Integer.parseInt(tiempo);
            cantidadPorciones = Integer.parseInt(porciones);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa números válidos.");
            return;
        }

        if (minutos < 1 || minutos > 600) {
            etTiempo.setError("Debe estar entre 1 y 600 minutos");
            etTiempo.requestFocus();
            return;
        }
        if (cantidadPorciones < 1 || cantidadPorciones > 50) {
            etPorciones.setError("Debe estar entre 1 y 50");
            etPorciones.requestFocus();
            return;
        }

        Categoria categoria = (Categoria) spCategoria.getSelectedItem();
        int dificultad = spDificultad.getSelectedItemPosition() + 1;

        btnGuardarReceta.setEnabled(false);
        btnGuardarReceta.setText(modoEdicion ? "GUARDANDO..." : "GUARDANDO...");

        if (modoEdicion) {
            ActualizarRecetaRequest request = new ActualizarRecetaRequest(
                    nombre,
                    descripcion,
                    categoria.getId(),
                    dificultad,
                    minutos,
                    cantidadPorciones,
                    ingredientes,
                    pasos
            );
            actualizarReceta(request);
        } else {
            CrearRecetaRequest request = new CrearRecetaRequest(
                    nombre,
                    descripcion,
                    categoria.getId(),
                    dificultad,
                    minutos,
                    cantidadPorciones,
                    ingredientes,
                    pasos
            );
            crearReceta(request);
        }
    }

    private void crearReceta(CrearRecetaRequest request) {
        repository.crearReceta(request, new Callback<Receta>() {
            @Override
            public void onResponse(Call<Receta> call, Response<Receta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int id = response.body().getId();
                    if (id <= 0) {
                        finalizarConError("La API no devolvió un ID válido.");
                        return;
                    }
                    subirImagenSiExiste(id);
                } else {
                    finalizarConError("No se pudo crear la receta. HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Receta> call, Throwable t) {
                finalizarConError("Error de conexión: " + mensajeError(t));
            }
        });
    }

    private void actualizarReceta(ActualizarRecetaRequest request) {
        repository.actualizarReceta(recetaId, request, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si no se eligió un archivo nuevo, se conservan los que ya tenía la receta.
                    subirImagenSiExiste(recetaId);
                } else {
                    finalizarConError("No se pudo actualizar la receta. HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                finalizarConError("Error de conexión al actualizar: " + mensajeError(t));
            }
        });
    }

    private void subirImagenSiExiste(int id) {
        if (imagenUri == null) {
            subirVideoSiExiste(id);
            return;
        }

        try {
            byte[] imagenBytes = convertirImagen(imagenUri);
            if (imagenBytes == null || imagenBytes.length == 0) {
                finalizarParcial(id, "Los datos se guardaron, pero no se pudo procesar la imagen.");
                return;
            }
            if (imagenBytes.length > 5 * 1024 * 1024) {
                finalizarParcial(id, "Los datos se guardaron, pero la imagen supera el tamaño permitido.");
                return;
            }

            String base64 = Base64.encodeToString(imagenBytes, Base64.NO_WRAP);
            ImagenBase64Request imagen = new ImagenBase64Request(
                    "receta_" + id + ".jpg",
                    base64
            );

            repository.subirImagen(id, imagen, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        subirVideoSiExiste(id);
                    } else {
                        finalizarParcial(id, "Los datos se guardaron, pero falló la subida de imagen. HTTP " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    finalizarParcial(id, "Los datos se guardaron, pero hubo un error subiendo la imagen: " + mensajeError(t));
                }
            });
        } catch (Exception e) {
            finalizarParcial(id, "Los datos se guardaron, pero no se pudo procesar la imagen: " + mensajeError(e));
        }
    }

    private byte[] convertirImagen(Uri uri) throws Exception {
        ContentResolver resolver = getContentResolver();
        Bitmap bitmap;

        try (InputStream input = resolver.openInputStream(uri)) {
            if (input == null) {
                return null;
            }
            bitmap = BitmapFactory.decodeStream(input);
        }

        if (bitmap == null) {
            return null;
        }

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();
        float escala = Math.min(1f, 1600f / Math.max(ancho, alto));

        if (escala < 1f) {
            Bitmap reducido = Bitmap.createScaledBitmap(
                    bitmap,
                    Math.max(1, (int) (ancho * escala)),
                    Math.max(1, (int) (alto * escala)),
                    true
            );
            bitmap.recycle();
            bitmap = reducido;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, output);
        bitmap.recycle();
        return output.toByteArray();
    }

    private void subirVideoSiExiste(int id) {
        if (videoUri == null) {
            finalizarCorrectamente();
            return;
        }

        try {
            byte[] videoBytes = leerVideo(videoUri);
            if (videoBytes == null || videoBytes.length == 0) {
                finalizarParcial(id, "Los datos se guardaron, pero no se pudo leer el video.");
                return;
            }
            if (videoBytes.length > 31 * 1024 * 1024) {
                finalizarParcial(id, "Los datos se guardaron, pero el video supera el límite permitido.");
                return;
            }

            String nombre = obtenerNombreArchivo(videoUri);
            String tipo = getContentResolver().getType(videoUri);
            if (tipo == null || !tipo.startsWith("video/")) {
                tipo = "video/mp4";
            }

            MediaType mediaType = MediaType.parse(tipo);
            RequestBody body = RequestBody.create(mediaType, videoBytes);
            MultipartBody.Part archivo = MultipartBody.Part.createFormData(
                    "archivo",
                    nombre,
                    body
            );

            repository.subirVideo(id, archivo, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        finalizarCorrectamente();
                    } else {
                        finalizarParcial(id, "Los datos se guardaron, pero falló la subida del video. HTTP " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    finalizarParcial(id, "Los datos se guardaron, pero hubo un error subiendo el video: " + mensajeError(t));
                }
            });
        } catch (Exception e) {
            finalizarParcial(id, "Los datos se guardaron, pero no se pudo procesar el video: " + mensajeError(e));
        }
    }

    private byte[] leerVideo(Uri uri) throws Exception {
        try (InputStream input = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            if (input == null) {
                return null;
            }

            byte[] buffer = new byte[8192];
            int leidos;
            int total = 0;
            while ((leidos = input.read(buffer)) != -1) {
                total += leidos;
                if (total > 31 * 1024 * 1024) {
                    throw new Exception("Video demasiado grande");
                }
                output.write(buffer, 0, leidos);
            }
            return output.toByteArray();
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        String nombre = "archivo";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columna = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columna >= 0) {
                    String valor = cursor.getString(columna);
                    if (valor != null && !valor.trim().isEmpty()) {
                        nombre = valor;
                    }
                }
            }
        } catch (Exception ignored) {
            // Se conserva el nombre genérico si el proveedor no informa el nombre.
        }
        return nombre;
    }

    private void finalizarCorrectamente() {
        String mensaje = modoEdicion
                ? "¡Cambios guardados correctamente!"
                : "¡Receta guardada correctamente!";
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finalizarParcial(int id, String mensaje) {
        Toast.makeText(
                this,
                mensaje + " ID de receta: " + id,
                Toast.LENGTH_LONG
        ).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finalizarConError(String mensaje) {
        btnGuardarReceta.setEnabled(true);
        btnGuardarReceta.setText(modoEdicion ? "GUARDAR CAMBIOS" : "GUARDAR RECETA");
        mostrarMensaje(mensaje);
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private String mensajeError(Throwable t) {
        return t == null || t.getMessage() == null
                ? "Error desconocido"
                : t.getMessage();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
