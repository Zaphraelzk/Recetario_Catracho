
package com.ucenm.recetario_catracho.model;

public class ImagenBase64Request {

    private String nombreArchivo;
    private String imagenBase64;

    public ImagenBase64Request(
            String nombreArchivo,
            String imagenBase64
    ) {
        this.nombreArchivo = nombreArchivo;
        this.imagenBase64 = imagenBase64;
    }
}
