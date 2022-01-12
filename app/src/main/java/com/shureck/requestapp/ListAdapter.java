package com.shureck.requestapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.nio.Buffer;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<Req> mRestaurants = new ArrayList<>();
    private Context mContext;
    private OnBtnClickListener onBtnClickListener;

    public ListAdapter(Context context, ArrayList<Req> restaurants, OnBtnClickListener onBtnClickListener) {
        mContext = context;
        mRestaurants = restaurants;
        this.onBtnClickListener = onBtnClickListener;
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
        ImageButton delButton;


        public ViewHolder(View itemView) {
            super(itemView);
            addr = itemView.findViewById(R.id.addr_edit);
            port = itemView.findViewById(R.id.port_edit);
            time = itemView.findViewById(R.id.time_edit);
            delButton = itemView.findViewById(R.id.delButton);
            mContext = itemView.getContext();

            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onBtnClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onBtnClickListener.onDeleteBtnClick(position);
                        }
                    }
                }
            });
        }

        public void bindRestaurant(Req restaurant) {
            addr.setText(restaurant.address);
            port.setText(restaurant.port);
            time.setText(restaurant.timeout);
        }
    }

    public interface OnBtnClickListener{
        void onDeleteBtnClick(int position);
    }
}
