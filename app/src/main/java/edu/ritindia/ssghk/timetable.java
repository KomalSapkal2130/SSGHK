package edu.ritindia.ssghk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.ritindia.ssghk.notice.NoticeData;

public class timetable extends AppCompatActivity {

    private CardView addImage;
    private EditText timetableTitle;
    private Button uploadtimetableBtn;
    private final int REQ = 1;
    private Bitmap bitmap;
    private ImageView noticeImageView;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl = "";
    private ProgressDialog pd;
    private Spinner imageCategory1,imageCategory2,imageCategory3;
    private String category1,category2,category3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        reference  = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        addImage = findViewById(R.id.addImage);
        noticeImageView = findViewById(R.id.noticeImageView);
        timetableTitle= findViewById(R.id.timetableTitle);
        uploadtimetableBtn = findViewById(R.id.uploadtimetableBtn);
        imageCategory1=findViewById(R.id.category_timetable);
        imageCategory2=findViewById(R.id.category_division);
        imageCategory3=findViewById(R.id.category_standard);


        String[] items1= new String[]{"Select category","Class Timetable","Exam Timetable"};
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


        String[] items3= new String[]{"Select Standard","5th std","6th std","7th std","8th std","9th std","10th std","All"};
        imageCategory3.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items3));

        imageCategory3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category3=imageCategory3.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });



        String[] items2= new String[]{"Select Division","A","B","All"};
        imageCategory2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items2));

        imageCategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category2=imageCategory2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openGallery();
            }

        });

        uploadtimetableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timetableTitle.getText().toString().isEmpty()){
                    timetableTitle.setError("Empty");
                    timetableTitle.requestFocus();
                }
                else if(bitmap == null){
                    uploadData();
                }
                else if (category1.equals("Select category"))
                {
                    Toast.makeText(timetable.this, "Please select Category", Toast.LENGTH_SHORT).show();
                }
                else if (category2.equals("Select Division"))
                {
                    Toast.makeText(timetable.this, "Please select Division", Toast.LENGTH_SHORT).show();
                }

                else if (category3.equals("Select Standard")) {
                    Toast.makeText(timetable.this, "Please select Standard", Toast.LENGTH_SHORT).show();

                }
                else{
                    uploadImage();
                }

            }
        });

    }
    private void uploadImage() {

        pd.setMessage("Uploading....");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Timetable").child(category1).child(category3).child(category2).child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(timetable.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }
                else{
                    pd.dismiss();
                    Toast.makeText(timetable.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void uploadData() {

        reference = reference.child("Timetable").child(category1).child(category3).child(category2);
        final String uniqueKey = reference.push().getKey();

        String title = timetableTitle.getText().toString();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calForDate.getTime());

        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time,uniqueKey);
        reference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(timetable.this, "Timetable uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(timetable.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            noticeImageView.setImageBitmap(bitmap);
        }
    }
}