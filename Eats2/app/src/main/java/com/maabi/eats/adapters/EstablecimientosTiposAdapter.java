package com.maabi.eats.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.maabi.eats.R;
import com.maabi.eats.models.EstablecimientosTipos;

import java.util.ArrayList;

public class EstablecimientosTiposAdapter extends  RecyclerView.Adapter<EstablecimientosTiposAdapter.ViewHolder> {
    private ArrayList<EstablecimientosTipos> dataset;
    private Context context;

    public EstablecimientosTiposAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipoestablecimiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstablecimientosTiposAdapter.ViewHolder holder, int position) {
        EstablecimientosTipos p = dataset.get(position);
        holder.nombreTextView.setText(p.getDenominacion());

        Glide.with(context)
                .load(p.getImagen_url())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.fotoImageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaEstablecimientosTipos(ArrayList<EstablecimientosTipos> listaEstablecimientosTipos) {
        dataset.addAll(listaEstablecimientosTipos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView fotoImageView;
        private TextView nombreTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoImageView = (ImageView) itemView.findViewById(R.id.imagen);
            nombreTextView = (TextView) itemView.findViewById(R.id.denominacion);
        }
    }
}
