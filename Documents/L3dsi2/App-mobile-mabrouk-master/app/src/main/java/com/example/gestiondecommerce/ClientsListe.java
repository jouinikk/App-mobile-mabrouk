package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClientsListe extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Intent intent;
    String from;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_liste);
        intent = getIntent();
        from = intent.getStringExtra("from");
        Log.i("from", from);
        RecyclerView rv = findViewById(R.id.recycler);
        getUsers(from, new UsersCallback() {
            @Override
            public void onCallback(List<User> users) {
                ClientListAdapter adapter = new ClientListAdapter(users, ClientsListe.this, from);
                rv.setLayoutManager(new LinearLayoutManager(ClientsListe.this));
                rv.setAdapter(adapter);
            }
        });
        add = findViewById(R.id.button5);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(this, Sign_up.class);
            intent.putExtra("from", from);
            startActivity(intent);
        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Liste des utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getUsers(String from, UsersCallback callback) {
        db.collection("User")
                .whereEqualTo("role", from)
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
