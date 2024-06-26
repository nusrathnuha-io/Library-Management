package com.example.librarymanagement;




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

public class BookCopyActivity extends AppCompatActivity {

    EditText editTextBookID, editTextBranchID, editTextAccessNo;
    Button addButton, deleteButton, updateButton;
    LinearLayout bookCopyListLayout;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookcopy);

        editTextBookID = findViewById(R.id.editTextBookID);
        editTextBranchID = findViewById(R.id.editTextBranchID);
        editTextAccessNo = findViewById(R.id.editTextAccessNumber);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        bookCopyListLayout = findViewById(R.id.bookCopyListLayout);

        dbHelper = new DBHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookCopy();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookCopy();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookCopy();
            }
        });
        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(BookCopyActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
        displayBookCopies();
    }

    private void addBookCopy() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the provided Book ID exists in the Book table
        String bookId = editTextBookID.getText().toString();
        if (!isBookExists(db, bookId)) {
            Toast.makeText(this, "Book ID does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the provided Branch ID exists in the Branch table
        String branchId = editTextBranchID.getText().toString();
        if (!isBranchExists(db, branchId)) {
            Toast.makeText(this, "Branch ID does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with adding the book copy
        ContentValues values = new ContentValues();
        values.put("BOOK_ID", bookId);
        values.put("BRANCH_ID", branchId);
        values.put("ACCESS_NO", editTextAccessNo.getText().toString());
        long newRowId = db.insert("Book_Copy", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding book copy", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book copy added successfully", Toast.LENGTH_SHORT).show();
            displayBookCopies();
        }
    }

    private void updateBookCopy() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the provided Book ID exists in the Book table
        String bookId = editTextBookID.getText().toString();
        if (!isBookExists(db, bookId)) {
            Toast.makeText(this, "Book ID does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the provided Branch ID exists in the Branch table
        String branchId = editTextBranchID.getText().toString();
        if (!isBranchExists(db, branchId)) {
            Toast.makeText(this, "Branch ID does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with updating the book copy
        ContentValues values = new ContentValues();
        values.put("BOOK_ID", bookId);
        String selection = "ACCESS_NO = ? AND BRANCH_ID = ?";
        String[] selectionArgs = {editTextAccessNo.getText().toString(), branchId};
        int count = db.update(
                "Book_Copy",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No book copy found with the given Access Number and Branch ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book copy updated successfully", Toast.LENGTH_SHORT).show();
            displayBookCopies();
        }
    }

    // Helper method to check if a book with the given ID exists in the Book table
    private boolean isBookExists(SQLiteDatabase db, String bookId) {
        Cursor cursor = db.query(
                "Book",
                new String[]{"BOOK_ID"},
                "BOOK_ID = ?",
                new String[]{bookId},
                null,
                null,
                null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Helper method to check if a branch with the given ID exists in the Branch table
    private boolean isBranchExists(SQLiteDatabase db, String branchId) {
        Cursor cursor = db.query(
                "Branch",
                new String[]{"BRANCH_ID"},
                "BRANCH_ID = ?",
                new String[]{branchId},
                null,
                null,
                null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void deleteBookCopy() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "ACCESS_NO = ? AND BRANCH_ID = ?";
        String[] selectionArgs = {editTextAccessNo.getText().toString(), editTextBranchID.getText().toString()};
        int deletedRows = db.delete("Book_Copy", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No book copy found with the given Access Number and Branch ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book copy deleted successfully", Toast.LENGTH_SHORT).show();
            displayBookCopies();
        }
    }



    private void displayBookCopies() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "BOOK_ID",
                "BRANCH_ID",
                "ACCESS_NO"
        };
        Cursor cursor = db.query(
                "Book_Copy",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        bookCopyListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String bookID = cursor.getString(cursor.getColumnIndexOrThrow("BOOK_ID"));
            String branchID = cursor.getString(cursor.getColumnIndexOrThrow("BRANCH_ID"));
            String accessNo = cursor.getString(cursor.getColumnIndexOrThrow("ACCESS_NO"));

            // Create a TextView for each book copy item
            TextView textView = new TextView(this);
            textView.setText("Book ID: " + bookID + "\nBranch ID: " + branchID + "\nAccess Number: " + accessNo + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);

            // Add the TextView to the bookCopyListLayout
            bookCopyListLayout.addView(textView);
        }
        cursor.close();
    }
}

