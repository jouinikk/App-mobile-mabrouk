package com.example.gestiondecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class add_form extends AppCompatActivity {
    private Spinner roleSpinner;
    private EditText commercialAffecteEditText;
    private EditText clientAffecteEditText;
    private EditText editTextNom;
    private EditText editTextEmail;
    private EditText editTextTel;
    private EditText editTextpws;
    private Button addButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_form);

        db = FirebaseFirestore.getInstance();

        roleSpinner = findViewById(R.id.spinnerRole);
        commercialAffecteEditText = findViewById(R.id.editTextCommercialAffecte);
        clientAffecteEditText = findViewById(R.id.editTextClientAffecte);
        editTextNom = findViewById(R.id.editTextNom);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTel = findViewById(R.id.editTextTel);
        editTextpws = findViewById(R.id.editTextpws);
        addButton = findViewById(R.id.button2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.roles,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRole = parentView.getItemAtPosition(position).toString();
                if ("Commercial".equals(selectedRole)) {
                    commercialAffecteEditText.setVisibility(View.GONE);
                    clientAffecteEditText.setVisibility(View.VISIBLE);
                } else if ("Client".equals(selectedRole)) {
                    commercialAffecteEditText.setVisibility(View.VISIBLE);
                    clientAffecteEditText.setVisibility(View.GONE);
                }
                else {
                    commercialAffecteEditText.setVisibility(View.GONE);
                    clientAffecteEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Ne rien faire ici, si nécessaire
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = editTextNom.getText().toString();
                // Vérifiez si le nom est non nul ou vide avant d'ajouter l'utilisateur à Firebase
                if (nom.isEmpty()) {
                    Toast.makeText(add_form.this, "Veuillez saisir un nom", Toast.LENGTH_SHORT).show();
                    return;  // Arrêtez l'exécution de la méthode si le nom est vide
                }

                String email = editTextEmail.getText().toString();
                int tel = Integer.parseInt(editTextTel.getText().toString());
                String password = editTextpws.getText().toString();
                String commercialAffecte = commercialAffecteEditText.getText().toString();
                String clientAffecte = clientAffecteEditText.getText().toString();
                String selectedRole = roleSpinner.getSelectedItem().toString();

                User newUser = new User(nom, email, tel, password, selectedRole, commercialAffecte, clientAffecte);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Ajoutez l'utilisateur à la collection "users" dans Firestore
                db.collection("User").document(currentUser.getUid()).set(newUser);

                finish();
            }
        });

    }}

