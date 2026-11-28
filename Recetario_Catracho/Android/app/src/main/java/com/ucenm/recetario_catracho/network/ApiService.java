
package com.ucenm.recetario_catracho.network;

import com.ucenm.recetario_catracho.model.ActualizarRecetaRequest;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.CrearRecetaRequest;
import com.ucenm.recetario_catracho.model.ImagenBase64Request;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Obtener recetas con búsqueda, paginación y categoría.
    @GET("api/recetas")
    Call<RespuestaRecetas> obtenerRecetas(
            @Query("search") String search,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("categoriaId") Integer categoriaId
    );

    // Obtener una receta por ID.
    @GET("api/recetas/{id}")
    Call<Receta> obtenerRecetaPorId(
            @Path("id") int id
    );

    // Obtener todas las categorías.
    @GET("api/categorias")
    Call<List<Categoria>> obtenerCategorias();

    // Crear receta.
    @POST("api/recetas")
    Call<Receta> crearReceta(
            @Body CrearRecetaRequest receta
    );

    // Actualizar receta.
    @PUT("api/recetas/{id}")
    Call<Void> actualizarReceta(
            @Path("id") int id,
            @Body ActualizarRecetaRequest receta
    );

    // Eliminar receta.
    @DELETE("api/recetas/{id}")
    Call<Void> eliminarReceta(
            @Path("id") int id
    );

    // Subir imagen.
    @PUT("api/recetas/{id}/imagen-base64")
    Call<ResponseBody> subirImagen(
            @Path("id") int id,
            @Body ImagenBase64Request imagen
    );

    // Subir video.
    @Multipart
    @POST("api/recetas/{id}/video")
    Call<ResponseBody> subirVideo(
            @Path("id") int id,
            @Part MultipartBody.Part archivo
    );
}
