package com.example.gestiondecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuperUserAdapter extends RecyclerView.Adapter<SuperUserAdapter.superUserViewHolder> {
    List<User> listeClient;
    Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public SuperUserAdapter(List<User> listeClient, Context context) {
        this.listeClient = listeClient;
        this.context = context;
    }

    @NonNull
    @Override
    public superUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_super_user, parent, false);
        return new superUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull superUserViewHolder holder, int position) {
        User currentItem = listeClient.get(position);

        holder.details.setText("\n Name: " + currentItem.getName() + "\n Role: " + currentItem.getRole()
        );
    }

    @Override
    public int getItemCount() {
        return listeClient.size();
    }
    public interface OnItemClickListener {
        void onItemClick(User item);
    }

    public class superUserViewHolder extends RecyclerView.ViewHolder {
        TextView details;

        public superUserViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.details);

            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(listeClient.get(position));
                    }
                }
            });
        }
    }



}
