
package com.ucenm.recetario_catracho.model;

public class CrearRecetaRequest {

    private String nombre;
    private String descripcion;
    private int categoriaId;
    private int dificultad;
    private int tiempoPreparacionMin;
    private int porciones;
    private String ingredientes;
    private String pasos;
    private String imagenRuta;
    private String videoRuta;

    public CrearRecetaRequest(
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
        this.imagenRuta = null;
        this.videoRuta = null;
    }
}
