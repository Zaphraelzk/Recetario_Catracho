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

import java.util.ArrayList;
import java.util.List;

public class RecetaAdapter
        extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private List<Receta> listaRecetas;
    private final OnRecetaClickListener listener;

    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    public RecetaAdapter(OnRecetaClickListener listener) {
        this.listaRecetas = new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(List<Receta> recetas) {
        this.listaRecetas = recetas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);

        return new RecetaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecetaViewHolder holder, int position) {

        Receta receta = listaRecetas.get(position);

        holder.txtNombre.setText(receta.getNombre());

        holder.txtCategoria.setText(
                receta.getCategoriaNombre() != null
                        ? receta.getCategoriaNombre()
                        : "Categoría #" + receta.getCategoriaId()
        );

        holder.txtTiempo.setText(
                "Tiempo: " +
                        receta.getTiempoPreparacionMin() +
                        " minutos"
        );

        Glide.with(holder.itemView.getContext())
                .load(receta.getImagenRuta())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imgReceta);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecetaClick(receta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

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
