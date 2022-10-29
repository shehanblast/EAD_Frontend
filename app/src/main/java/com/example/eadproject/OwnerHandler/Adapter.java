package com.example.eadproject.OwnerHandler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eadproject.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final Context context;
    LayoutInflater layoutInflater;
    List<Fuel> fuels;

    public Adapter(Context context, List<Fuel> fuels) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.fuels = fuels;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.single_fuel_details,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String id=fuels.get(position).getFuelId();

        //holder.fuelId.setText(fuels.get(position).getStationId());
        holder.stationName.setText(fuels.get(position).getStationName());
        holder.fuelType.setText(fuels.get(position).getFuelType());
        holder.fuelfinish.setText(fuels.get(position).getFinishStatus().toString());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditFuelInfo.class);
                intent.putExtra("id", fuels.get(position).getFuelId());
                intent.putExtra("StationId", fuels.get(position).getStationId());
                intent.putExtra("fuelName", fuels.get(position).getFuelType());
                intent.putExtra("email", fuels.get(position).getEmail());
                intent.putExtra("fuelfinish", fuels.get(position).getFinishStatus().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fuels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView fuelId, stationName,fuelType,fuelfinish;
        ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            fuelId=itemView.findViewById(R.id.txtrecyclerFuelStationId);
            stationName=itemView.findViewById(R.id.txtrecyclerFuelStation);
            fuelType=itemView.findViewById(R.id.txtrecyclerFuelType);
            fuelfinish=itemView.findViewById(R.id.txtFuelrecyclerFinish);
            imageView=itemView.findViewById(R.id.imageView10);

        }
    }
}
