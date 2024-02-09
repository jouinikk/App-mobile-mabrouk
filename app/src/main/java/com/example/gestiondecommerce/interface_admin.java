package com.example.gestiondecommerce;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

interface UsersCallback {
    void onCallback(List<User> users);
}

interface MvtsCallBack {
    void onCallBack(List<MVT> mvtList);
}

public class interface_admin extends AppCompatActivity {

    private CommercialAdapter commercialAdapter;
    private List<MVT> mvtList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mvtCollection = db.collection("mvt");
    Spinner spinner;
    RecyclerView recyclerView;
    private Button btnDatePicker;
    private Button btnDatePicker1;
    private Calendar selectedDate;
    private Calendar selectedDate1;
    private String date;
    private String date1;
    Button chercher;
    Button efface ;
    private List<MVT> filteredList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin);
        mvtList = new ArrayList<>();
        spinner = findViewById(R.id.spinner);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnDatePicker1 = findViewById(R.id.btnDatePicker1);
        efface = findViewById(R.id.effacer);

        btnDatePicker.setOnClickListener(view -> showDatePickerDialog(btnDatePicker));
        btnDatePicker1.setOnClickListener(view -> showDatePickerDialog(btnDatePicker1));
        efface.setOnClickListener(view -> clean1());

        getUsers(users -> {
            ArrayAdapter<User> adapter = new ArrayAdapter<>(interface_admin.this, android.R.layout.simple_spinner_item, users);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        });

        recyclerView = findViewById(R.id.rv);

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

    private void loadDataFromFirestore() {
        User user = (User) spinner.getSelectedItem();
        if (user != null) {
            mvtCollection
                    .whereEqualTo("commercial", user.getName())
                    .whereEqualTo("validation_admin", true)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mvtList.clear(); // Effacer la liste actuelle
                            for (DocumentSnapshot document : task.getResult()) {
                                MVT mvt = document.toObject(MVT.class);
                                mvt.setId(document.getId());
                                mvtList.add(mvt);
                                Log.d("FirestoreData", "Date: " + mvt.getDate());
                            }
                            if (mvtList.isEmpty()) {
                                Toast.makeText(this, "Liste vide", Toast.LENGTH_SHORT).show();
                            } else {
                                filterListByDate(); // Filtrer la liste par date
                            }
                        } else {
                            Log.d("erreur","erreur");
                        }
                    });
        } else {
            Toast.makeText(this, "La liste est vide", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterListByDate() {
        if (date != null && date1 != null) {
            filteredList = new ArrayList<>();
            for (MVT mvt : mvtList) {
                if (mvt.getDate().compareTo(date1) >= 0 && mvt.getDate().compareTo(date) <= 0) {
                    filteredList.add(mvt);
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Aucune donnée disponible pour la période sélectionnée", Toast.LENGTH_SHORT).show();
            } else {
                // Afficher la liste filtrée dans le RecyclerView
                commercialAdapter = new CommercialAdapter(filteredList, interface_admin.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(interface_admin.this));
                recyclerView.setAdapter(commercialAdapter);
                recyclerView.setVisibility(View.VISIBLE);
                calculateAndSetTotal(filteredList);
            }
        }
    }

    private void calculateAndSetTotal(List<MVT> list) {
        int sum = 0;
        for (MVT value : list) {
            sum += value.getMontant();
        }
        TextView t = findViewById(R.id.textView9);
        t.setText("Total: " + String.valueOf(sum));
    }

    private void getUsers(UsersCallback callback){
        List<User> lst = new ArrayList<>();
        db.collection("User")
                .whereEqualTo("role", "commercial")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document :task.getResult()){
                            User u = document.toObject(User.class);
                            u.setId(document.getId());
                            lst.add(u);
                        }
                        callback.onCallback(lst);
                    }
                });
    }

    private void showDatePickerDialog(Button btnDatePicker) {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(yearSelected, monthOfYear, dayOfMonth);
                    if (btnDatePicker.getId() == R.id.btnDatePicker) {
                        this.selectedDate = selectedDate;
                    } else if (btnDatePicker.getId() == R.id.btnDatePicker1) {
                        this.selectedDate1 = selectedDate;
                    }
                    handleDateSelection();
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void handleDateSelection() {
        if (selectedDate != null && selectedDate1 != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = dateFormat.format(selectedDate.getTime());
            date1 = dateFormat.format(selectedDate1.getTime());
            Toast.makeText(this, "Selected Date: " + date, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Selected Date1: " + date1, Toast.LENGTH_SHORT).show();
            loadDataFromFirestore();
        }
    }

    public void clean1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes-vous sûr de vouloir effacer les éléments sélectionnés ?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            ProgressDialog pd = new ProgressDialog(interface_admin.this);
            pd.setMessage("Effacement..");
            pd.show();
            for (MVT mvt : filteredList) {
                db.collection("mvt")
                        .document(mvt.getId())
                        .delete();
            }
            pd.dismiss();
            Intent intent = new Intent(interface_admin.this, interface_admin.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}