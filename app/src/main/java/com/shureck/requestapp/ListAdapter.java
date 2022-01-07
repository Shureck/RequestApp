package com.shureck.requestapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.nio.Buffer;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<Req> mRestaurants = new ArrayList<>();
    private Context mContext;

    public ListAdapter(Context context, ArrayList<Req> restaurants) {
        mContext = context;
        mRestaurants = restaurants;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        holder.bindRestaurant(mRestaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        EditText addr;
        EditText port;
        EditText time;


        public ViewHolder(View itemView) {
            super(itemView);
            addr = itemView.findViewById(R.id.addr_edit);
            port = itemView.findViewById(R.id.port_edit);
            time = itemView.findViewById(R.id.time_edit);
            mContext = itemView.getContext();
        }

        public void bindRestaurant(Req restaurant) {
            addr.setText(restaurant.address);
            port.setText(restaurant.port);
            time.setText(restaurant.timeout);
        }
    }
}
