package com.androidapp.carcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.carcare.R;
import com.androidapp.carcare.datamodels.VendorItem;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.androidapp.carcare.utils.Constants.SERVER_ADDRESS;
import static com.androidapp.carcare.utils.Constants.UNDEFINED;

public class VendorsListAdapter extends RecyclerView.Adapter<VendorsListAdapter.VH> implements Filterable {

    ArrayList<VendorItem> vfiltered_VendorsArrList;
    ArrayList<VendorItem> vlegit_VendorsArrList;
    Context context;
    private UserFilter userFilter;


    public VendorsListAdapter(ArrayList<VendorItem> vfiltered_VendorsArrList, Context context) {
        this.vfiltered_VendorsArrList = new ArrayList<>(vfiltered_VendorsArrList);
        this.vlegit_VendorsArrList = vfiltered_VendorsArrList;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        VendorItem currentVendorItem = vfiltered_VendorsArrList.get(position);
        //Glide.with(context).load(SERVER_ADDRESS+currentVendorItem.getVendorimgurl()).into(holder.v_VendorImgv);

        Picasso.get()
                .load(SERVER_ADDRESS+currentVendorItem.getVendorimgurl())
                .placeholder(R.drawable.loading_gif)
                .error(R.drawable.butn_bgrnd)
                .into(holder.v_VendorImgv);

        holder.v_VendorNameTxt.setText(currentVendorItem.getVendorname());
        holder.v_VendorTitleTxt.setText(currentVendorItem.getVendortitle());
        holder.v_VendorDistanceTxt.setText(currentVendorItem.getVendorcity()); //todo

        System.out.println("vollrescatnew"+currentVendorItem.getVendorimgurl());

    }

    @Override
    public int getItemCount() {
        return vfiltered_VendorsArrList.size();
    }

    @Override
    public Filter getFilter() {
        if(userFilter == null)
            userFilter = new UserFilter(this, vlegit_VendorsArrList);
        return userFilter;
    }

    public class VH extends RecyclerView.ViewHolder {

        @BindView(R.id.vendor_image)
        ImageView v_VendorImgv;

        @BindView(R.id.vendor_name_txt)
        TextView v_VendorNameTxt;

        @BindView(R.id.vendor_title_txt)
        TextView v_VendorTitleTxt;

        @BindView(R.id.vendor_distance_txt)
        TextView v_VendorDistanceTxt;


        public VH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }



    private class UserFilter extends Filter {

        private final VendorsListAdapter adapter;

        private final ArrayList<VendorItem> originalList;

        private final ArrayList<VendorItem> filteredList;

        private UserFilter(VendorsListAdapter adapter, ArrayList<VendorItem> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            FilterResults results = new FilterResults();

            if (constraint==null || constraint.length()<=0 || constraint.toString().equalsIgnoreCase("")) {
                filteredList.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toUpperCase();
                System.out.println("adding..."+filterPattern);
                for (VendorItem vendor : originalList) {

                    if (vendor.getVendorname().toUpperCase().contains(filterPattern)
                    || vendor.getVendorcity().toUpperCase().contains(filterPattern)
                    || vendor.getVendortitle().toUpperCase().contains(filterPattern)
                    ){

                        filteredList.add(vendor);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.vfiltered_VendorsArrList.clear();
            adapter.vfiltered_VendorsArrList.addAll((ArrayList<VendorItem>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
