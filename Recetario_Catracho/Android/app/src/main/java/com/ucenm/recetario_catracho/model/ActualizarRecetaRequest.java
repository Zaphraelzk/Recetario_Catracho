package com.ucenm.recetario_catracho.model;

/**
 * Datos enviados al endpoint PUT api/recetas/{id}.
 * Los nombres deben coincidir con las propiedades de ActualizarRecetaDto
 * en el backend ASP.NET Core.
 */
public class ActualizarRecetaRequest {

    private String nombre;
    private String descripcion;
    private int categoriaId;
    private int dificultad;
    private int tiempoPreparacionMin;
    private int porciones;
    private String ingredientes;
    private String pasos;

    public ActualizarRecetaRequest(
            String nombre,
            String descripcion,
            int categoriaId,
            int dificultad,
            int tiempoPreparacionMin,
            int porciones,
            String ingredientes,
            String pasos
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.dificultad = dificultad;
        this.tiempoPreparacionMin = tiempoPreparacionMin;
        this.porciones = porciones;
        this.ingredientes = ingredientes;
        this.pasos = pasos;
    }
}
