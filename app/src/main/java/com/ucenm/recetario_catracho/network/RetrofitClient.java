
package com.ucenm.recetario_catracho.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Dirección de nuestra API
    private static final String BASE_URL =
            "http://10.0.2.2:5000/";

    private static Retrofit retrofit;
    private static ApiService apiService;

    public static ApiService getApiService() {

        if (apiService == null) {

            // Registro de peticiones HTTP
            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor();

            logging.setLevel(
                    HttpLoggingInterceptor.Level.BODY
            );

            // Configuración de OkHttp
            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

            // Configuración de Retrofit
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
