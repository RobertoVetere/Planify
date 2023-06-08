package com.holidevs.weatherapp.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.holidevs.weatherapp.Models.Plans;
import com.holidevs.weatherapp.R;
import java.util.ArrayList;
import java.util.List;

public class AdapterDataPlans extends RecyclerView.Adapter<AdapterDataPlans.ViewHolderPlans> {

    private ArrayList<Plans> listPlans;

    public AdapterDataPlans(ArrayList<Plans> listPlans) {
        this.listPlans = listPlans;
    }

    public void setPlansList(List<Plans> newPlansList) {
        listPlans = new ArrayList<>();
        listPlans.clear();
        listPlans.addAll(newPlansList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterDataPlans.ViewHolderPlans onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de los elementos de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plans, parent, false);
        return new AdapterDataPlans.ViewHolderPlans(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDataPlans.ViewHolderPlans holder, int position) {

        Plans plans = listPlans.get(position);
        String plan = plans.getPlan();

        holder.planTxt.setText(plan);
    }

    @Override
    public int getItemCount() {
        if (listPlans == null) {
            return 0; // Si la lista es nula, devuelve 0
        } else {
            return listPlans.size(); // Si la lista no es nula, devuelve el tamaño de la lista
        }
    }

    public class ViewHolderPlans extends RecyclerView.ViewHolder {

        TextView cityName;

        TextView planTxt;

        public ViewHolderPlans(@NonNull View itemView) {
            super(itemView);

            cityName = itemView.findViewById(R.id.cityName);
            planTxt = itemView.findViewById(R.id.cityPlan);
        }


    }
}
