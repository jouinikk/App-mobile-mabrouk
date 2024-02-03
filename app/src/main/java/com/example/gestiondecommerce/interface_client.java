package com.example.gestiondecommerce;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class interface_client extends AppCompatActivity{
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    MVT mvt = new MVT();
    private EditText montantInput;
    private FirebaseFirestore db;
    Intent intent ;
    String commercial;
    String newMvtId;
    TextView attCom;
    EditText attEdit;
    RelativeLayout rl;
    Button imprim;
    Button update;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_client);
        intent=getIntent();
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        commercial = preferences.getString("commercial", "");
        montantInput = findViewById(R.id.montantInput);
        submitBtn = findViewById(R.id.submitBtn);
        db = FirebaseFirestore.getInstance();
        rl = findViewById(R.id.relativeLayout);
        attCom = findViewById(R.id.commercialTextView);
        attEdit = findViewById(R.id.montantTextView);
        update = findViewById(R.id.update);
        imprim = findViewById(R.id.imprimer);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(interface_client.this);
                pd.setMessage("Mis a jour en cours");
                pd.show();
                db.collection("mvt").document(newMvtId).update("montant",Integer.parseInt(String.valueOf(attEdit.getText())))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                            }
                        });
            }
        });

        submitBtn.setOnClickListener(view -> {
            if (montantInput.getText().toString().equals("")) {
                Toast.makeText(interface_client.this, "Montant Vide", Toast.LENGTH_SHORT).show();
            } else {
                mvt.setMontant(Integer.parseInt(montantInput.getText().toString()));
                mvt.setCommercial(commercial);
                mvt.setIdClient(preferences.getString("id", ""));
                mvt.setNomClient(preferences.getString("nom", ""));

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate currentDate = LocalDate.now();
                String formattedDate = currentDate.format(dateFormatter);
                mvt.setDate(formattedDate.substring(0, 10));
                db.collection("mvt").add(mvt)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentReference newMvtDocumentRef = task.getResult();
                                newMvtId = newMvtDocumentRef.getId();
                                Toast.makeText(interface_client.this, "Mouvement ajouter avec succes", Toast.LENGTH_SHORT).show();
                                ShowStatus();
                            } else {
                                Toast.makeText(interface_client.this, "Erreur", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        Button showMVTsBtn = findViewById(R.id.showMVTsBtn);
        showMVTsBtn.setOnClickListener(view -> {
            Intent i = new Intent(interface_client.this,MvtListActivity.class);
            i.putExtra("id", preferences.getString("id", ""));
            startActivity(i);
        });

        Button quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor= preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(interface_client.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imprim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             generatePdfForMVT(mvt);
             Intent intent1 = new Intent(interface_client.this,interface_client.class);
             startActivity(intent1);
             finish();
            }
        });
    }

    private void ShowStatus() {;
        montantInput.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);
        attEdit.setText(montantInput.getText());
        rl.setVisibility(View.VISIBLE);
        db.collection("mvt").document(newMvtId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }
                if (value != null && value.exists()) {
                    if(value.toObject(MVT.class).isValidation_commercial()){
                        update.setVisibility(View.GONE);
                        imprim.setVisibility(View.VISIBLE);
                    }
                } else {

                }
            }
        });
    }


    private void generatePdfForMVT(MVT mvt) {
        View pdfTemplateView = LayoutInflater.from(interface_client.this).inflate(R.layout.pdf_template, null);

        TextView idTextView = pdfTemplateView.findViewById(R.id.idTextView);
        TextView montantTextView = pdfTemplateView.findViewById(R.id.montantTextView);
        TextView dateTextView = pdfTemplateView.findViewById(R.id.dateTextView);
        TextView commercialTextView = pdfTemplateView.findViewById(R.id.commercialTextView);

        idTextView.setText("ID: " + mvt.getId());
        montantTextView.setText("Montant: " + mvt.getMontant());
        dateTextView.setText("Date: " + mvt.getDate());
        commercialTextView.setText("Commercial: " + mvt.getCommercial());

        // Check for permissions before generating PDF
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with generating PDF
            generatePdf(pdfTemplateView);
        }
    }

    private void generatePdf(View templateView) {
        // Convert the template view to a Bitmap (you might need to adjust the dimensions)
        templateView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        templateView.layout(0, 0, templateView.getMeasuredWidth(), templateView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(templateView.getWidth(), templateView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        templateView.draw(canvas);

        // Save the Bitmap as a PDF using iText or your preferred PDF generation library
        String pdfFileName = "ticket_"+newMvtId+".pdf"; // Name of the PDF file
        String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String pdfFilePath = downloadsPath + "/" + pdfFileName;
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas pdfCanvas = page.getCanvas();
            pdfCanvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            // Save the document to a file
            OutputStream outputStream = new FileOutputStream(pdfFilePath);
            document.writeTo(outputStream);
            document.close();
            outputStream.close();

            // Notify the user that the PDF has been generated successfully
            Toast.makeText(interface_client.this, "PDF generated successfully", Toast.LENGTH_SHORT).show();
            openPdfWithDefaultViewer(pdfFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle exceptions accordingly
            Toast.makeText(interface_client.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }catch (IOException ex){
            ex.printStackTrace();
            // Handle exceptions accordingly
            Toast.makeText(interface_client.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdfWithDefaultViewer(String pdfFilePath) {
        File file = new File(pdfFilePath);

        // Get the URI using FileProvider
        Uri fileUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", file);

        // Create an Intent to open the PDF file with the default PDF viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            this.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no PDF viewer app is available on the device
            Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
        }
    }


}
