package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySociete extends AppCompatActivity {

    EditText nom;
    EditText adresse;
    EditText matricule;

    Societe societe = new Societe();
    TextView empty;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_societe);
        nom = findViewById(R.id.nomSociete);
        adresse = findViewById(R.id.adresse);
        matricule = findViewById(R.id.matricule);
        submit = findViewById(R.id.submit);
        empty = findViewById(R.id.empty);
        getData();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sociéte");
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


    public void getData(){
        db.collection("societe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() ){
                            Log.d("hmm", "list mch empty");
                            List<Societe> list = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Societe s = document.toObject(Societe.class);
                                s.setId(document.getId());
                             list.add(s);
                            }
                            if(!list.isEmpty()) {
                                societe = list.get(0);
                                nom.setText(societe.getNom());
                                adresse.setText(societe.getAdresse());
                                matricule.setText(societe.getMatriculeFiscal());
                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("nom", nom.getText().toString());
                                        map.put("adresse", adresse.getText().toString());
                                        map.put("matriculeFiscal", matricule.getText().toString());

                                        db.collection("societe")
                                                .document(societe.getId())
                                                .update(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                            Toast.makeText(ActivitySociete.this, "modification enregistrée ", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(ActivitySociete.this, "Erreur d'enregistrement!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }else{
                                empty.setVisibility(View.VISIBLE);
                                submit.setText("Ajouter");
                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        societe.setNom(nom.getText().toString());
                                        societe.setAdresse(adresse.getText().toString());
                                        societe.setMatriculeFiscal(matricule.getText().toString());
                                        db.collection("societe")
                                                .add(societe)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        Intent intent = new Intent(ActivitySociete.this, ActivitySociete.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });

                            }
                        }
                    }
                });
    }
}