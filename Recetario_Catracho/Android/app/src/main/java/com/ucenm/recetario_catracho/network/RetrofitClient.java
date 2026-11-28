package com.ucenm.recetario_catracho.network;

import com.ucenm.recetario_catracho.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL =
            normalizarUrl(BuildConfig.API_BASE_URL);

    private static Retrofit retrofit;
    private static ApiService apiService;

    // Normalizar la dirección del backend
    private static String normalizarUrl(String url) {

        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException(
                    "La dirección API_BASE_URL no está configurada."
            );
        }

        url = url.trim();

        // Retrofit necesita una barra al final.
        if (!url.endsWith("/")) {
            url += "/";
        }

        return url;
    }

    // Obtener dirección base de la API
    public static String getBaseUrl() {
        return BASE_URL;
    }

    // Construir dirección HTTP de la imagen de una receta
    public static String getImagenUrl(int id) {

        return BASE_URL
                + "api/recetas/"
                + id
                + "/imagen";
    }

    // Obtener instancia de Retrofit
    public static ApiService getApiService() {

        if (apiService == null) {

            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor();

            logging.setLevel(
                    HttpLoggingInterceptor.Level.BODY
            );

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}
