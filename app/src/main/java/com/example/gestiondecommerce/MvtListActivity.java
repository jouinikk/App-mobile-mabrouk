package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

interface HistCallback{
    void onCallback(List<MVT> mvtList);
}


public class MvtListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String clientId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvt_list);
        Intent intent = getIntent();
        clientId = intent.getStringExtra("id");
        List<MVT> mvtList = getAttFromDatabase(new HistCallback() {
            @Override
            public void onCallback(List<MVT> mvtList) {
                MvtListAdapter mvtAdapter = new MvtListAdapter(MvtListActivity.this, mvtList);
                ListView mvtListView = findViewById(R.id.mvtListView);
                mvtListView.setAdapter(mvtAdapter);
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
    public List<MVT> getAttFromDatabase(HistCallback callback) {
        List<MVT> mouvmnts = new ArrayList<>();
        db.collection("mvt")
                .whereEqualTo("idClient", clientId)
                .whereEqualTo("validation_admin",true)
                .whereEqualTo("validation_commercial", true)
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        if(task.getResult().isEmpty()){
                            Toast.makeText(this, "empty list", Toast.LENGTH_LONG).show();
                        }else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MVT mvt = document.toObject(MVT.class);
                                mvt.setId(document.getId());
                                mouvmnts.add(mvt);
                            }
                        }
                        callback.onCallback(mouvmnts);
                    }
                });
        return mouvmnts;
    }
    // Handle the back button press

}


