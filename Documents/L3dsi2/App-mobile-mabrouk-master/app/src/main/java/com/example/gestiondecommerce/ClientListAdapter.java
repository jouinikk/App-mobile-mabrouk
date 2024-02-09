package com.example.gestiondecommerce;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ClientDetailsVH> {
    List<User> users;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String from;
    public ClientListAdapter(List<User> users, Context context, String from) {
        this.users = users;
        this.context = context;
        this.from = from;
    }

    @NonNull
    @Override
    public ClientDetailsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.affectation_details, parent, false);
        return new ClientDetailsVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ClientDetailsVH holder, int position) {
        User user = users.get(position);
        holder.nc.setText(user.getName());
        String userRole = "client";
        if (from.equals(userRole)) {
            userRole = "commercial";
            holder.labelUser.setText("Client: ");
            holder.labelAffected.setText("Commercial: ");
        }else{
            holder.labelUser.setText("Commercial: ");
            holder.labelAffected.setText("Client: ");
        }
        getUsers(userRole, new UsersCallback() {
            @Override
            public void onCallback(List<User> users) {
                ArrayAdapter<User> userArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, users);
                userArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                User placeholder = new User();
                placeholder.setName("Choisir un utilisateur");
                userArrayAdapter.insert(placeholder, 0);
                holder.sp.setAdapter(userArrayAdapter);
                holder.sp.setSelection(0);
                holder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.sp.getSelectedItemPosition() != 0) {
                            User selected = (User) holder.sp.getSelectedItem();
                            updateUser(user.getRole(), user.getId(), selected.getName());
                            updateUser(selected.getRole(), selected.getId(), user.getName());
                        }else {
                            Toast.makeText(context,"Choisir un utilisateur svp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void updateUser(String role,String userId, String selected) {
        String affectee = "commercialAffectee";
        if(role.equals("commercial")) affectee = "clientAffectee";
        db.collection("User").document(userId)
        .update(affectee, selected)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Commercial affectée", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ClientDetailsVH extends RecyclerView.ViewHolder {
        Spinner sp;
        TextView nc;
        TextView labelUser;
        TextView labelAffected;
        Button update;

        public ClientDetailsVH(@NonNull View itemView) {
            super(itemView);
            sp = itemView.findViewById(R.id.affected);
            nc = itemView.findViewById(R.id.nomClient);
            labelUser = itemView.findViewById(R.id.labelNomClient);
            labelAffected = itemView.findViewById(R.id.labelCommercial);
            update = itemView.findViewById(R.id.update);
        }
    }


    public void getUsers(String role,UsersCallback callback) {
         db.collection("User")
         .whereEqualTo("role",role)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> usersList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        user.setId(document.getId());
                        usersList.add(user);
                    }
                }
                callback.onCallback(usersList);
            }
        });
    }
}