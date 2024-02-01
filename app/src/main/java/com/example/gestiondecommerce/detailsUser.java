package com.example.gestiondecommerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class detailsUser extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);

        TextView txtName = findViewById(R.id.textView2);
        TextView txtEmail = findViewById(R.id.textView3);
        TextView txttel = findViewById(R.id.textView4);
        TextView txtRole = findViewById(R.id.textView);
        TextView txtAffecteé = findViewById(R.id.textView6);

        currentUser = (User) getIntent().getSerializableExtra("data");

        if (currentUser != null) {
            txtName.setText("Nom: " + currentUser.getName());
            txtEmail.setText("Email: " + currentUser.getEmail());
            txttel.setText("N° Tel: " + currentUser.getTel());
            txtRole.setText("Role: " + currentUser.getRole());
            if ("Commercial".equalsIgnoreCase(currentUser.getRole())) {
                txtAffecteé.setText("Client Affecteé : " + currentUser.getClientAffectee());
            } else if ("Client".equalsIgnoreCase(currentUser.getRole())) {
                txtAffecteé.setText("Commercial Affecteé : " + currentUser.getCommercialAffectee());
            }
        }
        Log.d("CurrentUser", "Name: " + currentUser.getName());
        Log.d("CurrentUser", "Email: " + currentUser.getEmail());
        Log.d("CurrentUser", "Tel: " + currentUser.getTel());
        Log.d("CurrentUser", "Role: " + currentUser.getRole());
        Log.d("CurrentUser", "Client Affecté: " + currentUser.getClientAffectee());
        Log.d("CurrentUser", "Commercial Affecté: " + currentUser.getCommercialAffectee());



        Button btn = findViewById(R.id.Editer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditUserDialog();
            }
        });

        Button btn1 = findViewById(R.id.suprimer);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        Button btn3 = findViewById(R.id.Quitter);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(detailsUser.this, SuperUser.class);
                startActivity(i);
            }
        });
    }

    private void showEditUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier User");

        View view = getLayoutInflater().inflate(R.layout.edit_form, null);
        builder.setView(view);

        final EditText editName = view.findViewById(R.id.editTextNom);
        final EditText editEmail = view.findViewById(R.id.editTextEmail);
        final EditText editTel = view.findViewById(R.id.editTextTel);
        final EditText editPws = view.findViewById(R.id.editTextpws);
        final Spinner spinnerRole = view.findViewById(R.id.spinnerRole);
        final EditText editTextCommercialAffecte = view.findViewById(R.id.editTextCommercialAffecte);
        final EditText editTextClientAffecte = view.findViewById(R.id.editTextClientAffecte);

        editName.setText(currentUser.getName());
        editEmail.setText(currentUser.getEmail());
        editTel.setText(String.valueOf(currentUser.getTel()));
        editPws.setText(currentUser.getPassword());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        int position = adapter.getPosition(currentUser.getRole());
        spinnerRole.setSelection(position);

        if ("Commercial".equalsIgnoreCase(currentUser.getRole())) {
            editTextCommercialAffecte.setVisibility(View.GONE);
            editTextCommercialAffecte.setText(currentUser.getCommercialAffectee());
            editTextClientAffecte.setVisibility(View.VISIBLE);
        } else if ("Client".equalsIgnoreCase(currentUser.getRole())) {
            editTextCommercialAffecte.setVisibility(View.VISIBLE);
            editTextClientAffecte.setVisibility(View.GONE);
            editTextClientAffecte.setText(currentUser.getClientAffectee());
        }else {
            editTextCommercialAffecte.setVisibility(View.GONE);
            editTextClientAffecte.setVisibility(View.GONE);



        }

        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = editName.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();
                String newTel = editTel.getText().toString().trim();
                String newPWS = editPws.getText().toString().trim();
                String newRole = spinnerRole.getSelectedItem().toString();
                String newCommercialAffecte = editTextCommercialAffecte.getText().toString().trim();
                String newClientAffecte = editTextClientAffecte.getText().toString().trim();

                updateUserDetails(newName, newEmail, newTel, newPWS, newRole, newCommercialAffecte, newClientAffecte);
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void updateUserDetails(String newName, String newEmail, String newTel, String newPWS, String newRole, String newCommercialAffecte, String newClientAffecte) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(currentUser.getId());

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", newName);
        updatedData.put("email", newEmail);
        updatedData.put("tel", Integer.parseInt(newTel));
        updatedData.put("password", newPWS);
        updatedData.put("role", newRole);

        if ("Commercial".equalsIgnoreCase(newRole)) {
            Log.d("UpdateUserDetails", "New Commercial Affecte: " + newCommercialAffecte);
            updatedData.put("commercialAffectee", newCommercialAffecte);
            updatedData.put("clientAffectee", "");
        } else if ("Client".equalsIgnoreCase(newRole)) {
            Log.d("UpdateUserDetails", "New Client Affecte: " + newClientAffecte);
            updatedData.put("clientAffectee", newClientAffecte);
            updatedData.put("commercialAffectee", "");
        } else {
            updatedData.put("commercialAffectee", "");
            updatedData.put("clientAffectee", "");
        }

        userRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(detailsUser.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(detailsUser.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer un Produit");
        builder.setMessage("Voulez-vous vraiment supprimer ce produit?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCurrentUser();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void deleteCurrentUser() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(currentUser.getId());

        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(detailsUser.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(detailsUser.this, SuperUser.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(detailsUser.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                });
    }
}



