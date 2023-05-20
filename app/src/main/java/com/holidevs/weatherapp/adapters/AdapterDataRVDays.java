package com.holidevs.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holidevs.weatherapp.Days.Days;
import com.holidevs.weatherapp.R;

import java.util.ArrayList;

public class AdapterDataRVDays extends RecyclerView.Adapter<AdapterDataRVDays.ViewHolderDays>{

    private ArrayList<Days> listDays;

    public AdapterDataRVDays(ArrayList<Days> listDays) {
        this.listDays = listDays;
    }

    @NonNull
    @Override
    public ViewHolderDays onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de los elementos de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolderDays(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDays holder, int position) {
        // Obtener el objeto Days en la posición actual
        Days days = listDays.get(position);

        // Actualizar los elementos de la interfaz de usuario con los datos de Days
        holder.txtDay.setText(days.getDay());
        // Actualiza el resto de elementos según sea necesario

        setCardBackgroundColor(holder,position);
    }

    private void setCardBackgroundColor(@NonNull ViewHolderDays holder,int position) {

        // Asignar un color diferente a cada tarjeta
        int colorResId;
        switch (position % 5) { // Cambia 3 a la cantidad de colores disponibles
            case 0:
                colorResId = R.drawable.rounded_card1;
                break;
            case 1:
                colorResId = R.drawable.rounded_card2;
                break;
            case 2:
                colorResId = R.drawable.rounded_card3;
                break;
            case 3:
                colorResId = R.drawable.rounded_card4;
                break;
            default:
                colorResId = R.drawable.rounded_card5;
                break;
        }
        holder.itemView.setBackgroundResource(colorResId);
    }

    @Override
    public int getItemCount() {
        return listDays.size();
    }

    public static class ViewHolderDays extends RecyclerView.ViewHolder {
        TextView txtDay;
        // Agrega referencias a los elementos de tu diseño de elemento de lista

        public ViewHolderDays(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtItemCurrentDay);
            // Inicializa las demás referencias según tus elementos de diseño
        }
    }
}


