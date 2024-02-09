package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommercialActivity extends AppCompatActivity {
    private TextView clientInfoTextView;
    private Button validateBtn;
    private FirebaseFirestore db;
    private ListenerRegistration mvtListener;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial);

        intent = getIntent();
        clientInfoTextView = findViewById(R.id.clientInfoTextView);
        validateBtn = findViewById(R.id.validateBtn);

        db = FirebaseFirestore.getInstance();
        listenForMvtChanges();

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePendingTransaction();
            }
        });

        TextView clientToday = findViewById(R.id.clientToday);
        clientToday.setText("Votre Client pour aujourd'hui est: "+intent.getStringExtra("client"));

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Espace Commercial");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void listenForMvtChanges() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(new Date()).substring(0, 10);
        mvtListener = db.collection("mvt")
                .whereEqualTo("commercial",intent.getStringExtra("commercial") )
                .whereEqualTo("validation_commercial", false)
                .whereEqualTo("nomClient", intent.getStringExtra("client"))
                .whereEqualTo("date", date)
                .limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null){
                        Toast.makeText(CommercialActivity.this, "Error listening for updates", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        DocumentChange documentChange = value.getDocumentChanges().get(0);
                        MVT mvt = documentChange.getDocument().toObject(MVT.class);
                        mvt.setId(documentChange.getDocument().getId());
                        updateClientInfo(mvt);
                    } else {
                        updateClientInfo(null);
                    }
                });
    }
    private void validatePendingTransaction() {
        db.collection("mvt").whereEqualTo("validation_commercial", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            MVT mvt = task.getResult().getDocuments().get(0).toObject(MVT.class);
                            mvt.setId(task.getResult().getDocuments().get(0).getId());
                            db.collection("mvt").document(mvt.getId())
                                    .update("validation_commercial", true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                listenForMvtChanges();
                                                Toast.makeText(CommercialActivity.this, "Transaction validated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CommercialActivity.this, "Error validating transaction", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void updateClientInfo(MVT mvt) {
        if (mvt != null) {
            String clientInfo = "Client: " + mvt.getNomClient() + "\nMontant: " + mvt.getMontant();
            clientInfoTextView.setText(clientInfo);
            validateBtn.setVisibility(View.VISIBLE);
        } else {
            clientInfoTextView.setText("Pas de mouvement à l'heure");
            validateBtn.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onDestroy() {

        if (mvtListener != null) {
            mvtListener.remove();
        }
        super.onDestroy();
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
