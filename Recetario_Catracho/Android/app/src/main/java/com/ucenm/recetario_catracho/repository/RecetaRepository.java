
package com.ucenm.recetario_catracho.repository;

import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.model.RespuestaRecetas;
import com.ucenm.recetario_catracho.network.ApiService;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RecetaRepository {

    private final ApiService apiService;

    public RecetaRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // Obtener recetas
    public void obtenerRecetas(
            String busqueda,
            Callback<RespuestaRecetas> callback) {

        Call<RespuestaRecetas> llamada =
                apiService.obtenerRecetas(
                        busqueda,
                        1,
                        20
                );

        llamada.enqueue(callback);
    }

    // Obtener receta por ID
    public void obtenerReceta(
            int id,
            Callback<Receta> callback) {

        Call<Receta> llamada =
                apiService.obtenerRecetaPorId(id);

        llamada.enqueue(callback);
    }

    // Obtener categorías
    public void obtenerCategorias(
            Callback<List<Categoria>> callback) {

        Call<List<Categoria>> llamada =
                apiService.obtenerCategorias();

        llamada.enqueue(callback);
    }
}
