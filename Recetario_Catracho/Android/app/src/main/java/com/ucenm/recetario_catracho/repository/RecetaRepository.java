package com.ucenm.recetario_catracho.repository;

import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.model.Categoria;
import com.ucenm.recetario_catracho.network.ApiService;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetaRepository {

    private final ApiService apiService;

    public RecetaRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void obtenerRecetas(
            String busqueda,
            Callback<List<Receta>> callback) {

        Call<List<Receta>> llamada =
                apiService.obtenerRecetas(busqueda, 1, 20);

        llamada.enqueue(callback);
    }

    public void obtenerReceta(
            int id,
            Callback<Receta> callback) {

        Call<Receta> llamada =
                apiService.obtenerRecetaPorId(id);

        llamada.enqueue(callback);
    }

    public void obtenerCategorias(
            Callback<List<Categoria>> callback) {

        Call<List<Categoria>> llamada =
                apiService.obtenerCategorias();

        llamada.enqueue(callback);
    }
}
