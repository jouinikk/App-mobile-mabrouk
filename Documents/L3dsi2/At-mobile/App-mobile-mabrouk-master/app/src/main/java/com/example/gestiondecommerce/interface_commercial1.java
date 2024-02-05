package com.example.gestiondecommerce;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

interface CallBack{
    public void onCallBack(List<MVT> list);
}
public class interface_commercial1 extends AppCompatActivity {

    private List<MVT> mvtList;
    private FirebaseFirestore db;
    private CollectionReference mvtCollection;
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = dateFormat.format(currentDate).substring(0, 10);
    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_commercial1);
        mvtList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mvtCollection = db.collection("mvt");
        TextView vide = findViewById(R.id.vide);
        loadDataFromFirestore(new CallBack() {
            @Override
            public void  onCallBack(List<MVT> list) {
                if (list.isEmpty()) vide.setVisibility(View.VISIBLE);
                CommercialListAdapter adapter = new CommercialListAdapter(interface_commercial1.this, list);
                ListView listView = findViewById(R.id.listViewMVT);
                listView.setAdapter(adapter);
            }
        });

        Button quit = findViewById(R.id.quit);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(interface_commercial1.this, MainActivity.class);
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
    public void loadDataFromFirestore(CallBack callBack) {
        mvtCollection
                .whereEqualTo("validation_commercial", false)
                .whereEqualTo("date", formattedDate)
                .whereEqualTo("clientAffectee", intent.getStringExtra("client"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MVT mvt = document.toObject(MVT.class);
                            mvt.setId(document.getId());
                            mvtList.add(mvt);
                        }
                        callBack.onCallBack(mvtList);
                    } else {
                        Toast.makeText(this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}