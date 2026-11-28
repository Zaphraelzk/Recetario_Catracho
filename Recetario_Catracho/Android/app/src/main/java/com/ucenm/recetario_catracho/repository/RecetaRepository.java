
package com.ucenm.recetario_catracho.repository;

import com.ucenm.recetario_catracho.model.ActualizarRecetaRequest;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.CrearRecetaRequest;
import com.ucenm.recetario_catracho.model.ImagenBase64Request;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;

import com.ucenm.recetario_catracho.network.ApiService;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

import retrofit2.Callback;

public class RecetaRepository {

    private final ApiService apiService;

    public RecetaRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // Obtener recetas con búsqueda y filtro por categoría.
    public void obtenerRecetas(
            String busqueda,
            Integer categoriaId,
            Callback<RespuestaRecetas> callback
    ) {

        apiService.obtenerRecetas(
                busqueda,
                1,
                20,
                categoriaId
        ).enqueue(callback);
    }

    // Método anterior para mantener compatibilidad.
    public void obtenerRecetas(
            String busqueda,
            Callback<RespuestaRecetas> callback
    ) {

        obtenerRecetas(busqueda, null, callback);
    }

    // Obtener una receta por ID.
    public void obtenerReceta(
            int id,
            Callback<Receta> callback
    ) {

        apiService.obtenerRecetaPorId(id).enqueue(callback);
    }

    // Obtener todas las categorías.
    public void obtenerCategorias(
            Callback<List<Categoria>> callback
    ) {

        apiService.obtenerCategorias().enqueue(callback);
    }

    // Crear receta.
    public void crearReceta(
            CrearRecetaRequest receta,
            Callback<Receta> callback
    ) {

        apiService.crearReceta(receta).enqueue(callback);
    }

    // Actualizar receta.
    public void actualizarReceta(
            int id,
            ActualizarRecetaRequest receta,
            Callback<Void> callback
    ) {

        apiService.actualizarReceta(id, receta).enqueue(callback);
    }

    // Eliminar receta.
    public void eliminarReceta(
            int id,
            Callback<Void> callback
    ) {

        apiService.eliminarReceta(id).enqueue(callback);
    }

    // Subir imagen.
    public void subirImagen(
            int id,
            ImagenBase64Request imagen,
            Callback<ResponseBody> callback
    ) {

        apiService.subirImagen(id, imagen).enqueue(callback);
    }

    // Subir video.
    public void subirVideo(
            int id,
            MultipartBody.Part archivo,
            Callback<ResponseBody> callback
    ) {

        apiService.subirVideo(id, archivo).enqueue(callback);
    }
}
