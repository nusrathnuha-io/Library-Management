package com.example.librarymanagement;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PublisherActivity extends AppCompatActivity {

    EditText editTextPublisherName, editTextPublisherAddress, editTextPublisherPhone;
    Button addButton, deleteButton, updateButton;
    LinearLayout publisherListLayout;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        editTextPublisherName = findViewById(R.id.editTextPublisherName);
        editTextPublisherAddress = findViewById(R.id.editTextPublisherAddress);
        editTextPublisherPhone = findViewById(R.id.editTextPublisherPhone);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        publisherListLayout = findViewById(R.id.ListLayout);

        dbHelper = new DBHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPublisher();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePublisher();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePublisher();
            }
        });

        displayPublishers();

        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(PublisherActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
    }

    private void addPublisher() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", editTextPublisherName.getText().toString());
        values.put("ADDRESS", editTextPublisherAddress.getText().toString());
        values.put("PHONE", editTextPublisherPhone.getText().toString());
        long newRowId = db.insert("Publisher", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding publisher", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Publisher added successfully", Toast.LENGTH_SHORT).show();
            displayPublishers();
        }
    }

    private void deletePublisher() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "NAME = ?";
        String[] selectionArgs = {editTextPublisherName.getText().toString()};
        int deletedRows = db.delete("Publisher", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No publisher found with the given name", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Publisher deleted successfully", Toast.LENGTH_SHORT).show();
            displayPublishers();
        }
    }

    private void updatePublisher() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ADDRESS", editTextPublisherAddress.getText().toString());
        values.put("PHONE", editTextPublisherPhone.getText().toString());
        String selection = "NAME = ?";
        String[] selectionArgs = {editTextPublisherName.getText().toString()};
        int count = db.update(
                "Publisher",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No publisher found with the given name", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Publisher updated successfully", Toast.LENGTH_SHORT).show();
            displayPublishers();
        }
    }

    private void displayPublishers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "NAME",
                "ADDRESS",
                "PHONE"
        };
        Cursor cursor = db.query(
                "Publisher",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        publisherListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("PHONE"));

            // Create a TextView for each publisher item
            TextView textView = new TextView(this);
            textView.setText("Publisher Name: " + name + "\nAddress: " + address + "\nPhone: " + phone + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            // Add the TextView to the publisherListLayout
            publisherListLayout.addView(textView);
        }
        cursor.close();
    }
}

