package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class detailsUser extends AppCompatActivity {
    private User currentUser;
    private ListenerRegistration userListener;

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

        // Set up Firestore snapshot listener
        userListener = FirebaseFirestore.getInstance().collection("User")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreListener", "Listen failed", error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case MODIFIED:
                                    if (currentUser != null && currentUser.getId().equals(dc.getDocument().getId())) {
                                        // Update the currentUser object
                                        currentUser = dc.getDocument().toObject(User.class);
                                        // Update the UI with the new data
                                        updateUI(currentUser);
                                    }
                                    break;
                            }
                        }
                    }
                });

        // Set initial UI
        updateUI(currentUser);

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

        if ("commercial".equalsIgnoreCase(currentUser.getRole())) {
            editTextCommercialAffecte.setVisibility(View.GONE);
            editTextClientAffecte.setText(currentUser.getClientAffectee());
            editTextClientAffecte.setVisibility(View.VISIBLE);
        } else if ("client".equalsIgnoreCase(currentUser.getRole())) {
            editTextCommercialAffecte.setVisibility(View.VISIBLE);
            editTextClientAffecte.setVisibility(View.GONE);
            editTextCommercialAffecte.setText(currentUser.getCommercialAffectee());
        } else {
            editTextCommercialAffecte.setVisibility(View.GONE);
            editTextClientAffecte.setVisibility(View.GONE);
        }
        Log.d("CurrentUser", "Name: " + currentUser.getName());
        Log.d("CurrentUser", "Email: " + currentUser.getEmail());
        Log.d("CurrentUser", "Tel: " + currentUser.getTel());
        Log.d("CurrentUser", "Role: " + currentUser.getRole());
        Log.d("CurrentUser", "Client Affecté: " + currentUser.getClientAffectee());
        Log.d("CurrentUser", "Commercial Affecté: " + currentUser.getCommercialAffectee());

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

        // Log the Firestore document snapshot before the update
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("UpdateUserDetails", "Before Update: " + documentSnapshot.getData());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateUserDetails", "Error fetching document before update", e);
                });

        // Prepare updated data
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", newName);
        updatedData.put("email", newEmail);
        updatedData.put("tel", Integer.parseInt(newTel));
        updatedData.put("password", newPWS);
        updatedData.put("role", newRole);

        /*Clear the previous values of clientAffectee and commercialAffectee
        updatedData.put("clientAffectee", "");
        updatedData.put("commercialAffectee", "");*/

        // Update the user details
        if ("commercial".equalsIgnoreCase(newRole)) {
            updatedData.put("clientAffectee", newClientAffecte);
        } else if ("client".equalsIgnoreCase(newRole)) {
            updatedData.put("commercialAffectee", newCommercialAffecte);
        }

        userRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateUserDetails", "User details updated successfully");
                    Toast.makeText(detailsUser.this, "User details updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateUserDetails", "Failed to update user details", e);
                    Toast.makeText(detailsUser.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                });

        // Log the Firestore document snapshot after the update
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("UpdateUserDetails", "After Update: " + documentSnapshot.getData());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateUserDetails", "Error fetching document after update", e);
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

    private void updateUI(User user) {
        if (user != null) {
            TextView txtName = findViewById(R.id.textView2);
            TextView txtEmail = findViewById(R.id.textView3);
            TextView txttel = findViewById(R.id.textView4);
            TextView txtRole = findViewById(R.id.textView);
            TextView txtAffecteé = findViewById(R.id.textView6);

            txtName.setText("Nom: " + user.getName());
            txtEmail.setText("Email: " + user.getEmail());
            txttel.setText("N° Tel: " + user.getTel());
            txtRole.setText("Role: " + user.getRole());
            if ("Commercial".equalsIgnoreCase(user.getRole())) {
                txtAffecteé.setText("Client Affecteé : " + user.getClientAffectee());
            } else if ("Client".equalsIgnoreCase(user.getRole())) {
                txtAffecteé.setText("Commercial Affecteé : " + user.getCommercialAffectee());
            }
            else {
                txtAffecteé.setVisibility(View.GONE);

            }
        }
    }

    @Override
    protected void onDestroy() {
        // Remove the snapshot listener to avoid memory leaks
        if (userListener != null) {
            userListener.remove();
        }
        super.onDestroy();
    }
}