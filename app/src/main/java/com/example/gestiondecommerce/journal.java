package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class journal extends AppCompatActivity {

    private List<MVT> mvtList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dateFormat.format(new Date()).substring(0, 10);
    Button valide;
    Button quit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        valide =findViewById(R.id.valide);
        recyclerView = findViewById(R.id.rv);
        quit = findViewById(R.id.quite);
        getData(new MvtsCallBack() {
            @Override
            public void onCallBack(List<MVT> mvtList) {
                CommercialAdapter commercialAdapter = new CommercialAdapter(mvtList, journal.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(journal.this));
                recyclerView.setAdapter(commercialAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        valide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valider();
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(journal.this,interface_admin_principal.class);
                startActivity(intent);
                finish();
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

    public void getData(MvtsCallBack callBack){
        db.collection("mvt")
                .whereEqualTo("date",formattedDate )
                .whereEqualTo("validation_commercial", true)
                .whereEqualTo("validation_admin", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                MVT mvt = document.toObject(MVT.class);
                                mvt.setId(document.getId());
                                mvtList.add(mvt);
                            }
                        }
                        calculateAndSetTotal();
                        callBack.onCallBack(mvtList);
                    }
                });
    }

    private void calculateAndSetTotal() {
        int sum = 0;
        for (MVT value : mvtList) {
            sum = sum + value.getMontant();
        }
        TextView t = findViewById(R.id.textView9);
        t.setText("Total: " + String.valueOf(sum));
    }

    public void valider(){
        if (mvtList.isEmpty()){
            Toast.makeText(journal.this, "la liste est vide",Toast.LENGTH_SHORT ).show();
        }else{
            for(MVT mvt:mvtList){
               db.collection("mvt")
                       .document(mvt.getId())
                       .update("validation_admin",true);
            }
        }
    }
}