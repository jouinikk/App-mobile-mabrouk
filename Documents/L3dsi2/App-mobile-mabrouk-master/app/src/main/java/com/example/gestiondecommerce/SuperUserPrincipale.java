package com.example.gestiondecommerce;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SuperUserPrincipale extends AppCompatActivity {
    Button clients;
    Button Admins;
    Button Commerciaux;
    Button quitter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user_principale);

        clients = findViewById(R.id.client); // Assurez-vous de récupérer correctement les boutons depuis le layout

        clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuperUserPrincipale.this, SuperUser.class);
                i.putExtra("role", "client");
                startActivity(i);


            }
        });
        Commerciaux = findViewById(R.id.Commerciaux); // Assurez-vous de récupérer correctement les boutons depuis le layout

        Commerciaux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuperUserPrincipale.this, SuperUser.class);
                i.putExtra("role", "commercial");
                startActivity(i);


            }
        });
        Admins = findViewById(R.id.Admins); // Assurez-vous de récupérer correctement les boutons depuis le layout

        Admins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuperUserPrincipale.this, SuperUser.class);
                i.putExtra("role", "admin");
                startActivity(i);
            }
        });
        quitter = findViewById(R.id.quitter); // Assurez-vous de récupérer correctement les boutons depuis le layout


        // Assurez-vous de configurer les autres boutons (Admins et Commerciaux) de manière similaire si nécessaire
    }
}