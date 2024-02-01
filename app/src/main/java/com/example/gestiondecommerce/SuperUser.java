package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SuperUser extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SuperUserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize User List
        userList = new ArrayList<>();

        // Initialize Adapter
        adapter = new SuperUserAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase Firestore
        fetchDataFromFirestore();

        // Corrected setOnItemClickListener usage
        adapter.setOnItemClickListener(user -> {
            // Rediriger vers l'activité de détails avec les données du produit
            Intent intent = new Intent(SuperUser.this, detailsUser.class);
            intent.putExtra("data", user);
            startActivity(intent);
        });
        Button btn1=findViewById(R.id.button4);
                btn1.setOnClickListener(view -> {
                    Intent i = new Intent(SuperUser.this,add_form.class);
                    startActivity(i);
                });
    }

    private void fetchDataFromFirestore() {
        db.collection("User") // Remplacez par le nom réel de votre collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            String name = document.getString("name");
                            String role = document.getString("role");

                            // Modification ici pour récupérer tel en tant qu'objet et le convertir en int
                            Object telObject = document.get("tel");
                            int tel = (telObject instanceof Number) ? ((Number) telObject).intValue() : 0;

                            User user = new User(email, name, role, tel);
                            user.setId(document.getId()); // Set the document id as user id
                            userList.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Gérer les erreurs
                    }
                });
    }


}
