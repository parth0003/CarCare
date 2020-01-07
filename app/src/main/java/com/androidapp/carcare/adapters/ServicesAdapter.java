package com.androidapp.carcare.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.carcare.R;
import com.androidapp.carcare.activities.VendorsListActivity;
import com.androidapp.carcare.datamodels.ServiceItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;


public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.VH> {

    ArrayList<ServiceItem> serviceItems;
    Context context;

    public ServicesAdapter(ArrayList<ServiceItem> ServiceItems, Context context) {
        this.serviceItems = ServiceItems;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_servicecard, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {


        final String imgurl = SERVER_ADDRESS+serviceItems.get(position).getServiceImageURL();

        Picasso.get()
                .load(imgurl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(imgurl)
                                .placeholder(R.drawable.loading_gif)
                                .error(R.drawable.butn_bgrnd)
                                .into(holder.imageView);
                    }


                });



        holder.text.setText(serviceItems.get(position).getServiceName());

        holder.TopLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,VendorsListActivity.class);
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.min(serviceItems.size(),8);
    }


    public class VH extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView text;
        RelativeLayout TopLay;

        public VH(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            TopLay = itemView.findViewById(R.id.top_lay);
        }
    }


}
