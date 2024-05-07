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

public class BookActivity extends AppCompatActivity {

    EditText editTextBookId, editTextTitle, editTextPublisher;
    Button addBookButton, deleteBookButton, updateBookButton;
    LinearLayout bookListLayout;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        editTextBookId = findViewById(R.id.editTextBookId);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextPublisher = findViewById(R.id.editTextPublisher);
        addBookButton = findViewById(R.id.addBookButton);
        deleteBookButton = findViewById(R.id.deleteBookButton);
        updateBookButton = findViewById(R.id.updateBookButton);
        bookListLayout = findViewById(R.id.bookListLayout);

        dbHelper = new DBHelper(this);

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });

        updateBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook();
            }
        });

        displayBooks();

        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(BookActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });


    }

    private void addBook() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BOOK_ID", editTextBookId.getText().toString());
        values.put("TITLE", editTextTitle.getText().toString());
        values.put("PUBLISHER_NAME", editTextPublisher.getText().toString());
        long newRowId = db.insert("Book", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void deleteBook() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "BOOK_ID = ?";
        String[] selectionArgs = {editTextBookId.getText().toString()};
        int deletedRows = db.delete("Book", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No book found with the given ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void updateBook() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TITLE", editTextTitle.getText().toString());
        values.put("PUBLISHER_NAME", editTextPublisher.getText().toString());
        String selection = "BOOK_ID = ?";
        String[] selectionArgs = {editTextBookId.getText().toString()};
        int count = db.update(
                "Book",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No book found with the given ID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book updated successfully", Toast.LENGTH_SHORT).show();
            displayBooks();
        }
    }

    private void displayBooks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "BOOK_ID",
                "TITLE",
                "PUBLISHER_NAME"
        };
        Cursor cursor = db.query(
                "Book",
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
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String publisher = cursor.getString(cursor.getColumnIndexOrThrow("PUBLISHER_NAME"));

            // Create a TextView for each book item
            TextView textView = new TextView(this);
            textView.setText("Book ID: " + bookId + "\nTitle: " + title + "\nPublisher: " + publisher + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);

            // Add the TextView to the bookListLayout
            bookListLayout.addView(textView);
        }
        cursor.close();
    }
}

