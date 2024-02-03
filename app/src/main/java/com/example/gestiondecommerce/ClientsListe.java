package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientsListe extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Intent intent= getIntent();
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_liste);
        from = intent.getStringExtra("from");
        RecyclerView rv = findViewById(R.id.recycler);
        getUsers(new UsersCallback() {
            @Override
            public void onCallback(List<User> users) {
                ClientListAdapter adapter = new ClientListAdapter(users,ClientsListe.this,from);
                rv.setLayoutManager(new LinearLayoutManager(ClientsListe.this));
                rv.setAdapter(adapter);
            }
        });
    }

    public void getUsers(UsersCallback callback){
        CollectionReference collection =  db.collection("User");
        Query query ;
        if(from.equals("client")) query = collection.whereEqualTo("role", Arrays.asList("client","Client"));
        else query = collection.whereEqualTo("role", Arrays.asList("commercial","Commercial"));
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> users = new ArrayList<>();
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                User user = document.toObject(User.class);
                                user.setId(document.getId());
                                users.add(user);
                            }
                            callback.onCallback(users);
                        }
                    }
                });
    }

}