package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
public class add_form extends AppCompatActivity {
    private Spinner usersSpinner;
    private EditText editTextNom;
    private EditText editTextEmail;
    private EditText editTextTel;
    private EditText editTextpws;
    Intent intent;
    private Button addButton;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form);
        db = FirebaseFirestore.getInstance();
        usersSpinner = findViewById(R.id.spinnerUsers);
        editTextNom = findViewById(R.id.editTextNom);
        String role = intent.getStringExtra("from");
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTel = findViewById(R.id.editTextTel);
        editTextpws = findViewById(R.id.editTextpws);
        addButton = findViewById(R.id.button2);
        if ("commercial".equals(role)) {
            getUsers("client", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(add_form.this, android.R.layout.simple_spinner_item,users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    usersSpinner.setAdapter(userAdapter);
                    usersSpinner.setVisibility(View.VISIBLE);                       }
            });
        } else if ("client".equals(role)) {
            getUsers("commercial", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(add_form.this, android.R.layout.simple_spinner_item,users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    usersSpinner.setAdapter(userAdapter);
                    usersSpinner.setVisibility(View.VISIBLE);
                }
            });
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nom = editTextNom.getText().toString();

                if (nom.isEmpty()) {
                    Toast.makeText(add_form.this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = editTextEmail.getText().toString();
                int tel = Integer.parseInt(editTextTel.getText().toString());
                String password = editTextpws.getText().toString();
                User newUser = new User();

                newUser.setName(nom);
                newUser.setEmail(email);
                newUser.setTel(tel);
                newUser.setPassword(password);
                newUser.setRole(role);

                User user =(User) usersSpinner.getSelectedItem();

                if (role.equals("client")){
                    newUser.setCommercialAffectee(user.getName());
                }else if(role.equals("commercial")){
                    newUser.setClientAffectee(user.getName());
                }
                db.collection("User").add(newUser)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(add_form.this,"Utilisateur ajoutée avec succée", Toast.LENGTH_SHORT).show();
                            }
                        })
                ;
                finish();
            }
        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ajouter un utilisateur");
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

    public void getUsers(String role,UsersCallback callback){
        List<User> users = new ArrayList<>();
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("getting data...");
        pd.show();
        db.collection("User")
                .whereEqualTo("role",role)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                User user = document.toObject(User.class);
                                user.setId(document.getId());
                                users.add(user);
                            }
                            callback.onCallback(users);
                        }
                        pd.dismiss();
                    }
                });
    }
}