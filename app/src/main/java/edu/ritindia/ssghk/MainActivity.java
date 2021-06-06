package edu.ritindia.ssghk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import edu.ritindia.ssghk.notice.DeleteNoticeActivity;
import edu.ritindia.ssghk.notice.NoticeAdapter;
import edu.ritindia.ssghk.notice.UploadNotice;

import edu.ritindia.ssghk.faculty.UpdateFaculty;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CardView uploadNotice, addGalleryImage, addEbook, faculty, deleteNotice,marks,attendance,timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadNotice = findViewById(R.id.addNotice);
        addGalleryImage = findViewById(R.id.addGalleryImage);
        addEbook = findViewById(R.id.addEbook);
        faculty = findViewById(R.id.faculty);
        deleteNotice = findViewById(R.id.delete);
        marks = findViewById(R.id.marks);
        attendance = findViewById(R.id.attendance);
        timetable = findViewById(R.id.timetable);

        uploadNotice.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        addEbook.setOnClickListener(this);
        faculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
        marks.setOnClickListener(this);
        attendance.setOnClickListener(this);
        timetable.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.addNotice:
                startActivity(new Intent(getApplicationContext(), UploadNotice.class));
                break;
            case R.id.addGalleryImage:
                startActivity(new Intent(getApplicationContext(), UploadImage.class));
                break;
            case R.id.addEbook:
                startActivity(new Intent(getApplicationContext(), UploadStudyMaterialActivity.class));
                break;

            case R.id.faculty:
                startActivity(new Intent(getApplicationContext(), UpdateFaculty.class));
                break;

            case R.id.delete:
                startActivity(new Intent(getApplicationContext(), DeleteNoticeActivity.class));
                break;
            case R.id.attendance:
                startActivity(new Intent(getApplicationContext(), Attendance.class));
                break;
            case R.id.marks:
                startActivity(new Intent(getApplicationContext(), marks.class));
                break;
            case R.id.timetable:
                startActivity(new Intent(getApplicationContext(), timetable.class));
                break;

        }
    }
}