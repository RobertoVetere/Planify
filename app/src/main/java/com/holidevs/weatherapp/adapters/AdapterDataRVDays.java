package com.holidevs.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holidevs.weatherapp.Models.Days;
import com.holidevs.weatherapp.R;
import com.holidevs.weatherapp.activities.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterDataRVDays extends RecyclerView.Adapter<AdapterDataRVDays.ViewHolderDays> {

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
        String currentTemp = days.getCurrentTemp() + "º";
        String maxTemp = days.getMaxTemp() + "º";
        String minTemp = days.getMinTemp() + "º";
        String dateRaw = days.getDay();
        String[] dateSplit = dateRaw.split(" ");

        int icon = MainActivity.obtenerCodigoImagen(days.getIcon());

        String date = convertDateToDayOfWeek(dateSplit[0]);

        // Actualizar los elementos de la interfaz de usuario con los datos de Days
        holder.txtDay.setText(date);
        holder.txtTemperature.setText(currentTemp);
        //holder.txtMaxTemperature.setText(maxTemp);
        //holder.txtMinTemperature.setText(minTemp);
        holder.icon.setImageResource(icon);

        setCardBackgroundColor(holder, position);

    }

    @Override
    public int getItemCount() {
        if (listDays == null) {
            return 0; // Si la lista es nula, devuelve 0
        } else {
            return listDays.size(); // Si la lista no es nula, devuelve el tamaño de la lista
        }
    }

    public static String convertDateToDayOfWeek(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setCardBackgroundColor(@NonNull ViewHolderDays holder, int position) {

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

    public void setDaysList(ArrayList<Days> newDaysList) {
        listDays = new ArrayList<>();
        listDays.clear();
        listDays.addAll(newDaysList);
        notifyDataSetChanged();
    }


    public static class ViewHolderDays extends RecyclerView.ViewHolder {
        TextView txtDay;
        TextView txtTemperature;
        //TextView txtMaxTemperature;
        //TextView txtMinTemperature;

        ImageView icon;

        public ViewHolderDays(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtItemCurrentDay);
            txtTemperature = itemView.findViewById(R.id.txtItemoCurrentTemp);
            //txtMaxTemperature = itemView.findViewById(R.id.txtItemWeatherMaxTemp);
            //txtMinTemperature = itemView.findViewById(R.id.txtItemWeatherMinTemp);
            icon = itemView.findViewById(R.id.imgItemWeatherIcon);
        }
    }
}


