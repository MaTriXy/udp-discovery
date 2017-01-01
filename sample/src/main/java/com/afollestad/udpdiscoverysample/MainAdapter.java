package com.afollestad.udpdiscoverysample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.udpdiscovery.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainVH> {

    private TextView emptyView;
    private List<Entity> entities;

    public MainAdapter(TextView emptyView) {
        this.emptyView = emptyView;
        entities = new ArrayList<>();
    }

    public void add(Entity entity) {
        entities.add(entity);
        emptyView.setVisibility(View.GONE);
        notifyItemInserted(getItemCount() - 1);
    }

    public void clear() {
        entities.clear();
        emptyView.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
    }

    @Override public MainVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_discovered, parent, false);
        return new MainVH(view);
    }

    @Override public void onBindViewHolder(MainVH holder, int position) {
        Entity entity = entities.get(position);
        holder.name.setText(entity.name());
        holder.address.setText(entity.address());
    }

    @Override public int getItemCount() {
        return entities.size();
    }

    public static class MainVH extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView address;

        public MainVH(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.address);
        }
    }

}
