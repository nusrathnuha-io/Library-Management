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

public class AuthorActivity extends AppCompatActivity {

    EditText editTextBookId, editTextAuthorName;
    Button addButton, deleteButton, updateButton;
    LinearLayout bookListLayout;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        editTextBookId = findViewById(R.id.editTextBookId);
        editTextAuthorName = findViewById(R.id.editTextAuthorName);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        bookListLayout = findViewById(R.id.bookListLayout);

        dbHelper = new DBHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookAuthor();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookAuthor();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookAuthor();
            }
        });

        displayBooks();

        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(AuthorActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
    }

    private void addBookAuthor() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BOOK_ID", editTextBookId.getText().toString());
        values.put("AUTHOR_NAME", editTextAuthorName.getText().toString());
        long newRowId = db.insert("Book_Author", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding book author", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book author added successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void deleteBookAuthor() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "BOOK_ID = ? AND AUTHOR_NAME = ?";
        String[] selectionArgs = {editTextBookId.getText().toString(), editTextAuthorName.getText().toString()};
        int deletedRows = db.delete("Book_Author", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No book author found with the given ID and name", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book author deleted successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void updateBookAuthor() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("AUTHOR_NAME", editTextAuthorName.getText().toString());
        String selection = "BOOK_ID = ?";
        String[] selectionArgs = {editTextBookId.getText().toString()};
        int count = db.update(
                "Book_Author",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No book author found with the given ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book author updated successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void displayBooks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "BOOK_ID",
                "AUTHOR_NAME"
        };
        Cursor cursor = db.query(
                "Book_Author",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        bookListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String bookId = cursor.getString(cursor.getColumnIndexOrThrow("BOOK_ID"));
            String authorName = cursor.getString(cursor.getColumnIndexOrThrow("AUTHOR_NAME"));

            // Create a TextView for each book author item
            TextView textView = new TextView(this);
            textView.setText("Book ID: " + bookId + "\nAuthor Name: " + authorName + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            // Add the TextView to the bookListLayout
            bookListLayout.addView(textView);
        }
        cursor.close();
    }
}
