package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Sign_up extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextEmail, editTextName, editTextTel, editTextPassword;
    private Spinner spinnerUser;
    private Button btnRegister;
    Intent i;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Ajouter un utilisateur");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        i = getIntent();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextTel = findViewById(R.id.editTextTel);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        spinnerUser = findViewById(R.id.spinnerUser);
        if (i.getStringExtra("from").equals("client")) {
            getUsers("commercial", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(Sign_up.this, android.R.layout.simple_spinner_item, users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUser.setAdapter(userAdapter);
                    spinnerUser.setVisibility(View.VISIBLE);
                }
            });

        } else{
            getUsers("client", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(Sign_up.this, android.R.layout.simple_spinner_item, users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUser.setAdapter(userAdapter);
                    spinnerUser.setVisibility(View.VISIBLE);
                }
            });
        }


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String telString = editTextTel.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String role = i.getStringExtra("from");

        ProgressDialog progressDialog = new ProgressDialog(Sign_up.this);
        progressDialog.setMessage("Ajout en cours...");
        progressDialog.show();

        if (email.isEmpty() || !isValidEmail(email)) {
            Toast.makeText(this, "Veuillez saisir une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telString.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un numéro de téléphone.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le numéro de téléphone est un entier valide
        int tel;
        try {
            tel = Integer.parseInt(telString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez saisir un numéro de téléphone valide.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            saveUserDetails(email, name, tel, role, password);
                        } else {
                            Toast.makeText(Sign_up.this, "Échec de l'enregistrement.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDetails(String email, String name, int tel, String role, String password) {
        User user = new User(email, password);
        user.setName(name);
        user.setTel(tel);
        user.setRole(role);
        if (role.equals("client")) {
            User com = (User) spinnerUser.getSelectedItem();
            user.setCommercialAffectee(com.getName());
        } else if (role.equals("commercial")) {
            User cli = (User) spinnerUser.getSelectedItem();
            user.setClientAffectee(cli.getName());
        }
        firestore.collection("User")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Sign_up.this, interface_admin_principal.class));
                            finish(); // Fermez l'activité d'enregistrement
                        } else {
                            Toast.makeText(Sign_up.this, "Échec de l'enregistrement des détails de l'utilisateur.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void getUsers(String role, UsersCallback callback) {
        List<User> users = new ArrayList<>();
        db.collection("User").whereEqualTo("role", role)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(document.toObject(User.class));
                            }
                            callback.onCallback(users);
                        } else {
                            Toast.makeText(Sign_up.this, "liste vide", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}