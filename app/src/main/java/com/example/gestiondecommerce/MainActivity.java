package com.example.gestiondecommerce;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText mail=findViewById(R.id.mail);
        EditText password=findViewById(R.id.Password);
        Button btn=findViewById(R.id.btn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(mail.getText());
                String psw = String.valueOf(password.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "L'e-mail est vide", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Authentification en cours...");
                    progressDialog.show();

                    db.collection("User")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String documentEmail = document.getString("email");
                                            String documentPsw = document.getString("password");
                                            String documentRole = document.getString("role");

                                            if (email.equals(documentEmail) && psw.equals(documentPsw) && "client".equals(documentRole)) {
                                                // Rediriger vers ClientActivity
                                                Intent intent = new Intent(MainActivity.this,interface_client.class);
                                                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("id", document.getId());
                                                editor.putString("nom", document.getString("name"));
                                                editor.putString("commercial", document.getString("commercialAffectee"));
                                                editor.apply();
                                                startActivity(intent);
                                                finish();  // Optionnel : fermer cette activité pour éviter le retour en arrière
                                                return;
                                            }
                                            else
                                            if (email.equals(documentEmail) && psw.equals(documentPsw) && "commercial".equals(documentRole)) {
                                                Intent intent = new Intent(MainActivity.this, CommercialActivity.class);
                                                intent.putExtra("client", document.getString("clientAffectee"));
                                                intent.putExtra("commercial", document.getString("name"));
                                                startActivity(intent);
                                                finish();  // Optionnel : fermer cette activité pour éviter le retour en arrière
                                                return;
                                            }
                                            else
                                            if (email.equals(documentEmail) && psw.equals(documentPsw) && "admin".equals(documentRole)) {
                                                // Rediriger vers ClientActivity
                                                Intent intent = new Intent(MainActivity.this, interface_admin_principal.class);
                                                startActivity(intent);
                                                finish();  // Optionnel : fermer cette activité pour éviter le retour en arrière
                                                return;
                                            } else
                                            if (email.equals(documentEmail) && psw.equals(documentPsw) && "superUser".equals(documentRole)) {
                                                // Rediriger vers ClientActivity
                                                Intent intent = new Intent(MainActivity.this, SuperUser.class);
                                                startActivity(intent);
                                                finish();  // Optionnel : fermer cette activité pour éviter le retour en arrière
                                                return;
                                            }
                                        }
                                        Toast.makeText(MainActivity.this, "Aucun utilisateur correspondant trouvé", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.w(TAG, "Erreur d'authentification!.", task.getException());
                                    }
                                }
                            });
                }
            }
        });




    }

}