package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
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

public class detailsUser extends AppCompatActivity {
    private User currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        final Spinner spinnerUsers= view.findViewById(R.id.spinnerUser);

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
            getUsers("client", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(detailsUser.this, android.R.layout.simple_spinner_item,users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUsers.setAdapter(userAdapter);
                    spinnerUsers.setVisibility(View.VISIBLE);
                }
            });
        } else if ("client".equalsIgnoreCase(currentUser.getRole())) {
            getUsers("commercial", new UsersCallback() {
                @Override
                public void onCallback(List<User> users) {
                    ArrayAdapter<User> userAdapter = new ArrayAdapter<>(detailsUser.this, android.R.layout.simple_spinner_item,users);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerUsers.setAdapter(userAdapter);
                    spinnerUsers.setVisibility(View.VISIBLE);
                }
            });
        }else {
            spinnerUsers.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = editName.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();
                String newTel = editTel.getText().toString().trim();
                String newPWS = editPws.getText().toString().trim();
                String newRole = spinnerRole.getSelectedItem().toString();
                User user = (User) spinnerUsers.getSelectedItem();
                String newCommercialAffecte ="";
                String newClientAffecte="";
                if (newRole.equals("client")) newCommercialAffecte = user.getName();
                if(newRole.equals("commercial")) newClientAffecte = user.getName();
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

        if ("commercial".equalsIgnoreCase(newRole)) {
            Log.d("UpdateUserDetails", "New Commercial Affecte: " + newCommercialAffecte);
            updatedData.put("commercialAffectee", newCommercialAffecte);
            updatedData.put("clientAffectee", "");
        } else if ("client".equalsIgnoreCase(newRole)) {
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
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(detailsUser.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                });
    }
    private void getUsers(String role,UsersCallback callback){
        List<User> users = new ArrayList<>();
        db.collection("User").whereEqualTo("role", role)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                users.add(document.toObject(User.class));
                            }
                            callback.onCallback(users);
                        }else {
                            Toast.makeText(detailsUser.this, "liste vide",Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}