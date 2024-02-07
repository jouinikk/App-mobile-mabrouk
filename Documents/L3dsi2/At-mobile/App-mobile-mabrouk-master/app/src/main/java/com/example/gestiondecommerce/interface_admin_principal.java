package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class interface_admin_principal extends AppCompatActivity {
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin_principal);
        Button btn1 = findViewById(R.id.button3);
        Button btn2 = findViewById(R.id.button6);
        firestore = FirebaseFirestore.getInstance();

        btn1.setOnClickListener(view -> {
            Intent intent = new Intent(this, journal.class);
            startActivity(intent);
        });

        btn2.setOnClickListener(view -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
        });

        Button btnShowUsers = findViewById(R.id.btnShowUsers);
        btnShowUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(interface_admin_principal.this, ClientsListe.class);
                intent.putExtra("from", "client");
                startActivity(intent);
            }
        });
        Button showCommercials = findViewById(R.id.showCommercials);
        showCommercials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(interface_admin_principal.this, ClientsListe.class);
                intent.putExtra("from", "commercial");
                startActivity(intent);
            }
        });

        Button quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(interface_admin_principal.this, MainActivity.class);
                startActivity(i);
            }
        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Historique");

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
}
