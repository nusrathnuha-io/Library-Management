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

public class BranchActivity extends AppCompatActivity {

    EditText editTextBranchID, editTextBranchName, editTextBranchAddress;
    Button addBranchButton, deleteBranchButton, updateBranchButton;
    LinearLayout branchListLayout;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        editTextBranchID = findViewById(R.id.editTextBranchID);
        editTextBranchName = findViewById(R.id.editText);
        editTextBranchAddress = findViewById(R.id.editText2);
        addBranchButton = findViewById(R.id.addButton);
        deleteBranchButton = findViewById(R.id.deleteButton);
        updateBranchButton = findViewById(R.id.updateButton);
        branchListLayout = findViewById(R.id.ListLayout);

        dbHelper = new DBHelper(this);

        addBranchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBranch();
            }
        });

        deleteBranchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBranch();
            }
        });

        updateBranchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBranch();
            }
        });


        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(BranchActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
        displayBranches();
    }

    private void addBranch() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BRANCH_ID", editTextBranchID.getText().toString());
        values.put("BRANCH_NAME", editTextBranchName.getText().toString());
        values.put("ADDRESS", editTextBranchAddress.getText().toString());
        long newRowId = db.insert("Branch", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding branch", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Branch added successfully", Toast.LENGTH_SHORT).show();
            displayBranches();
        }
    }

    private void deleteBranch() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "BRANCH_ID = ?";
        String[] selectionArgs = {editTextBranchID.getText().toString()};
        int deletedRows = db.delete("Branch", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No branch found with the given ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Branch deleted successfully", Toast.LENGTH_SHORT).show();
            displayBranches();
        }
    }

    private void updateBranch() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BRANCH_NAME", editTextBranchName.getText().toString());
        values.put("ADDRESS", editTextBranchAddress.getText().toString());
        String selection = "BRANCH_ID = ?";
        String[] selectionArgs = {editTextBranchID.getText().toString()};
        int count = db.update(
                "Branch",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No branch found with the given ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Branch updated successfully", Toast.LENGTH_SHORT).show();
            displayBranches();
        }
    }

    private void displayBranches() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "BRANCH_ID",
                "BRANCH_NAME",
                "ADDRESS"
        };
        Cursor cursor = db.query(
                "Branch",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        branchListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String branchID = cursor.getString(cursor.getColumnIndexOrThrow("BRANCH_ID"));
            String branchName = cursor.getString(cursor.getColumnIndexOrThrow("BRANCH_NAME"));
            String branchAddress = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));

            // Create a TextView for each branch item
            TextView textView = new TextView(this);
            textView.setText("Branch ID: " + branchID + "\nBranch Name: " + branchName + "\nAddress: " + branchAddress + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            // Add the TextView to the branchListLayout
            branchListLayout.addView(textView);
        }
        cursor.close();
    }
}

