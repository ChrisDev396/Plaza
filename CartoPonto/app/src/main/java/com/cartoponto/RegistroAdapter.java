package com.cartoponto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cartoponto.model.Registro;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.MyViewHolder> {

    private List<Registro> registros;
    private Context context;

    public RegistroAdapter(List<Registro> registros, Context context) {
        this.registros = registros;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_registro, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Registro registro = registros.get(position);
        holder.txtData.setText(registro.getData());
        Picasso.get().load(registro.getImgUrl()).into(holder.imgFoto);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFoto;
        TextView txtData;
        public MyViewHolder(View itemView){
            super(itemView);

            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtData = itemView.findViewById(R.id.txtData);
        }
    }
}
