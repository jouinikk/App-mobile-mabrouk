package com.example.gestiondecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
        getUsers(new UsersCallback() {
            @Override
            public void onCallback(List<User> users) {
                ArrayAdapter<User> userArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, users);
                userArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.sp.setAdapter(userArrayAdapter);
            }
        });
        holder.sp.setSelected(false);
        holder.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                User selected = (User) holder.sp.getSelectedItem();
                updateUser(user.getId(), selected.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void updateUser(String user, String selected) {
        Task<Void> task;
        DocumentReference document = db.collection("User").document(user);
        if (from.equals("client")) task = document.update("commercialAffectee", selected);
        else task = document.update("clientAffectee", selected);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
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

        public ClientDetailsVH(@NonNull View itemView) {
            super(itemView);
            sp = itemView.findViewById(R.id.affected);
            nc = itemView.findViewById(R.id.nomClient);
        }
    }


    public void getUsers(UsersCallback callback) {
        Query query;
        CollectionReference collection = db.collection("User");
        if (from.equals("client"))
            query = collection.whereEqualTo("role", Arrays.asList("commercial", "Commercial"));
        else query = collection.whereEqualTo("role", Arrays.asList("client", "Client"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                Toast.makeText(context, String.valueOf(usersList.size()), Toast.LENGTH_SHORT).show();
                callback.onCallback(usersList);
            }
        });
    }
}