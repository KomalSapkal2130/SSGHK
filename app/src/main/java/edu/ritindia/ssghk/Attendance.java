package edu.ritindia.ssghk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import edu.ritindia.ssghk.notice.NoticeData;

public class Attendance extends AppCompatActivity {
    private CardView addpdf;
    private EditText pdfTitle;
    private Button uploadpdfBtn;
    private TextView pdfTextView;
    private String pdfName,title,category1,category2;
    private Spinner imageCategory1,imageCategory2;



    private final int REQ = 1;
    private Uri pdfdata;
    private ImageView noticeImageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String downloadUrl = "";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        imageCategory1=findViewById(R.id.category_standard);
        imageCategory2=findViewById(R.id.category_division);

        databaseReference  = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        addpdf = findViewById(R.id.addPDF);
        pdfTitle = findViewById(R.id.pdfTitle);
        uploadpdfBtn = findViewById(R.id.uploadpdfBtn);
        pdfTextView = findViewById(R.id.pdfTextView);

        String[] items1= new String[]{"Select standard","5th std","6th std","7th std","8th std","9th std","10th std"};
        imageCategory1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items1));

        imageCategory1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category1=imageCategory1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });

        String[] items2= new String[]{"Select Division","A","B","C"};
        imageCategory2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items2));

        imageCategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category2=imageCategory2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        addpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openGallery();
            }

        });

        uploadpdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdfTitle.getText().toString();
                if (title.isEmpty())
                {
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }
                else if (pdfdata == null)
                {
                    Toast.makeText(Attendance.this, "Please Upload PDF", Toast.LENGTH_SHORT).show();
                }
                else if (category1.equals("Select standard"))
                {
                    Toast.makeText(Attendance.this, "Please select Standard", Toast.LENGTH_SHORT).show();
                }
                else if (category2.equals("Select Division"))
                {
                    Toast.makeText(Attendance.this, "Please select Division", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadpdf();
                }

            }

        });


    }

    private void uploadpdf()
    {
        pd.setTitle("Please wait.....");
        pd.setMessage("Uploading your file....");
        pd.show();
        StorageReference reference = storageReference.child("Attendance/").child(category1).child(category2+" " +pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.child(category1).child(category2).putFile(pdfdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(Attendance.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String valueOf)
    {
        String uniqueKey=databaseReference.child("Attendace").child(category1).child(category2).push().getKey();

        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",valueOf);
        databaseReference.child("Attendace").child(category1).child(category2).child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task)
            {
                pd.dismiss();
                Toast.makeText(Attendance.this,"Attendance uploaded successfully",Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(Attendance.this,"Failed to upload Attendance",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select file"),REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ && resultCode == RESULT_OK){
            pdfdata = data.getData();

            if(pdfdata.toString().startsWith("content://"))
            {
                Cursor cursor = null;
                try {
                    cursor = Attendance.this.getContentResolver().query(pdfdata,null,null,null,null);
                    if(cursor != null && cursor.moveToFirst())
                    {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(pdfdata.toString().startsWith("file://"))
            {
                pdfName =  new File(pdfdata.toString()).getName();
            }
            pdfTextView.setText(pdfName);

        }
    }
    }
