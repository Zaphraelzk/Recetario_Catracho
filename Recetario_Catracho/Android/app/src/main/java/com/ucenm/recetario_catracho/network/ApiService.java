package com.ucenm.recetario_catracho.network;

import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Obtener todas las recetas
    @GET("api/recetas")
    Call<RespuestaRecetas> obtenerRecetas(
            @Query("search") String search,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    // Obtener una receta por su ID
    @GET("api/recetas/{id}")
    Call<Receta> obtenerRecetaPorId(
            @Path("id") int id
    );

    // Obtener todas las categorías
    @GET("api/categorias")
    Call<List<Categoria>> obtenerCategorias();
}
