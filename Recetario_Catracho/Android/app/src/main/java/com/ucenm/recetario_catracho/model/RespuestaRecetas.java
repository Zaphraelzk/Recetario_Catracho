package com.ucenm.recetario_catracho.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaRecetas {

    @SerializedName("recetas")
    private List<Receta> recetas;

    @SerializedName("totalRegistros")
    private int totalRegistros;

    @SerializedName("paginaActual")
    private int paginaActual;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPaginas")
    private int totalPaginas;

    public List<Receta> getRecetas() {
        return recetas;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
