
package com.ucenm.recetario_catracho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ucenm.recetario_catracho.R;
import com.ucenm.recetario_catracho.model.Receta;
import com.ucenm.recetario_catracho.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class RecetaAdapter
        extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private List<Receta> listaRecetas;
    private final OnRecetaClickListener listener;

    // Interfaz para detectar cuando se selecciona una receta.
    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    // Constructor del adaptador.
    public RecetaAdapter(OnRecetaClickListener listener) {
        this.listaRecetas = new ArrayList<>();
        this.listener = listener;
    }

    // Actualizar los datos del RecyclerView.
    public void actualizarLista(List<Receta> recetas) {

        if (recetas == null) {
            this.listaRecetas = new ArrayList<>();
        } else {
            this.listaRecetas = new ArrayList<>(recetas);
        }

        notifyDataSetChanged();
    }

    // Crear cada elemento de la lista.
    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);

        return new RecetaViewHolder(vista);
    }

    // Mostrar la información de cada receta.
    @Override
    public void onBindViewHolder(
            @NonNull RecetaViewHolder holder,
            int position) {

        Receta receta = listaRecetas.get(position);

        // Nombre de la receta.
        holder.txtNombre.setText(receta.getNombre());

        // Categoría de la receta.
        holder.txtCategoria.setText(
                receta.getCategoriaNombre() != null
                        ? receta.getCategoriaNombre()
                        : "Categoría #" + receta.getCategoriaId()
        );

        // Tiempo de preparación.
        holder.txtTiempo.setText(
                "Tiempo: "
                        + receta.getTiempoPreparacionMin()
                        + " minutos"
        );

        // Cargar imagen mediante la URL HTTP del backend.
        if (receta.getId() > 0) {

            String imagenUrl =
                    RetrofitClient.getImagenUrl(receta.getId());

            Glide.with(holder.itemView.getContext())
                    .load(imagenUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.imgReceta);

        } else {

            // Mostrar imagen predeterminada si no existe un ID válido.
            Glide.with(holder.itemView.getContext())
                    .clear(holder.imgReceta);

            holder.imgReceta.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        // Evento al seleccionar una receta.
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onRecetaClick(receta);
            }

        });
    }

    // Cantidad de recetas disponibles.
    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    // Clase que contiene las vistas de cada elemento.
    static class RecetaViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgReceta;
        TextView txtNombre;
        TextView txtCategoria;
        TextView txtTiempo;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgReceta = itemView.findViewById(R.id.imgReceta);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtTiempo = itemView.findViewById(R.id.txtTiempo);
        }
    }
}
