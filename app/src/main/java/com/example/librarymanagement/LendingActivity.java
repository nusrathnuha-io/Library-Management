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

public class LendingActivity extends AppCompatActivity {

    EditText editTextAccessNo, editTextBranchID, editTextCardNo, editTextDateOut, editTextDateDue, editTextDateReturned;
    Button addButton, deleteButton, updateButton;
    LinearLayout lendingListLayout;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);

        editTextAccessNo = findViewById(R.id.editTextAccessNumber);
        editTextBranchID = findViewById(R.id.editTextBranchID);
        editTextCardNo = findViewById(R.id.editTextCardNo);
        editTextDateOut = findViewById(R.id.editTextDateOut);
        editTextDateDue = findViewById(R.id.editTextDateDue);
        editTextDateReturned = findViewById(R.id.editTextDateReturned);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        lendingListLayout = findViewById(R.id.ListLayout);

        dbHelper = new DBHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookLoan();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookLoan();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookLoan();
            }
        });


        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(LendingActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
        displayBookLoans();
    }

    private void addBookLoan() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the provided Access Number and Branch ID exist in the Book_Copy table
        String accessNo = editTextAccessNo.getText().toString();
        String branchId = editTextBranchID.getText().toString();
        if (!isBookCopyExists(db, accessNo, branchId)) {
            Toast.makeText(this, "Access Number and/or Branch ID do not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the provided Card Number exists in the Member table
        String cardNo = editTextCardNo.getText().toString();
        if (!isMemberExists(db, cardNo)) {
            Toast.makeText(this, "Card Number does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with adding the book loan
        ContentValues values = new ContentValues();
        values.put("ACCESS_NO", accessNo);
        values.put("BRANCH_ID", branchId);
        values.put("CARD_NO", cardNo);
        values.put("DATE_OUT", editTextDateOut.getText().toString());
        values.put("DATE_DUE", editTextDateDue.getText().toString());
        values.put("DATE_RETURNED", editTextDateReturned.getText().toString());
        long newRowId = db.insert("Book_Loan", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding book loan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book loan added successfully", Toast.LENGTH_SHORT).show();
            displayBookLoans();
        }
    }

    private void updateBookLoan() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the provided Access Number and Branch ID exist in the Book_Copy table
        String accessNo = editTextAccessNo.getText().toString();
        String branchId = editTextBranchID.getText().toString();
        if (!isBookCopyExists(db, accessNo, branchId)) {
            Toast.makeText(this, "Access Number and/or Branch ID do not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the provided Card Number exists in the Member table
        String cardNo = editTextCardNo.getText().toString();
        if (!isMemberExists(db, cardNo)) {
            Toast.makeText(this, "Card Number does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with updating the book loan
        ContentValues values = new ContentValues();
        values.put("CARD_NO", cardNo);
        values.put("DATE_DUE", editTextDateDue.getText().toString());
        values.put("DATE_RETURNED", editTextDateReturned.getText().toString());
        String selection = "ACCESS_NO = ? AND BRANCH_ID = ? AND DATE_OUT = ?";
        String[] selectionArgs = {
                accessNo,
                branchId,
                editTextDateOut.getText().toString()
        };
        int count = db.update(
                "Book_Loan",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No book loan found with the given details", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book loan updated successfully", Toast.LENGTH_SHORT).show();
            displayBookLoans();
        }
    }

    // Helper method to check if a book copy with the given Access Number and Branch ID exists in the Book_Copy table
    private boolean isBookCopyExists(SQLiteDatabase db, String accessNo, String branchId) {
        Cursor cursor = db.query(
                "Book_Copy",
                new String[]{"ACCESS_NO", "BRANCH_ID"},
                "ACCESS_NO = ? AND BRANCH_ID = ?",
                new String[]{accessNo, branchId},
                null,
                null,
                null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Helper method to check if a member with the given Card Number exists in the Member table
    private boolean isMemberExists(SQLiteDatabase db, String cardNo) {
        Cursor cursor = db.query(
                "Member",
                new String[]{"CARD_NO"},
                "CARD_NO = ?",
                new String[]{cardNo},
                null,
                null,
                null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    private void deleteBookLoan() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "ACCESS_NO = ? AND BRANCH_ID = ? AND CARD_NO = ? AND DATE_OUT = ?";
        String[] selectionArgs = {
                editTextAccessNo.getText().toString(),
                editTextBranchID.getText().toString(),
                editTextCardNo.getText().toString(),
                editTextDateOut.getText().toString()
        };
        int deletedRows = db.delete("Book_Loan", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No book loan found with the given details", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book loan deleted successfully", Toast.LENGTH_SHORT).show();
            displayBookLoans();
        }
    }



    @SuppressLint("SetTextI18n")
    private void displayBookLoans() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "ACCESS_NO",
                "BRANCH_ID",
                "CARD_NO",
                "DATE_OUT",
                "DATE_DUE",
                "DATE_RETURNED"
        };
        Cursor cursor = db.query(
                "Book_Loan",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        lendingListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String accessNo = cursor.getString(cursor.getColumnIndexOrThrow("ACCESS_NO"));
            String branchID = cursor.getString(cursor.getColumnIndexOrThrow("BRANCH_ID"));
            String cardNo = cursor.getString(cursor.getColumnIndexOrThrow("CARD_NO"));
            String dateOut = cursor.getString(cursor.getColumnIndexOrThrow("DATE_OUT"));
            String dateDue = cursor.getString(cursor.getColumnIndexOrThrow("DATE_DUE"));
            String dateReturned = cursor.getString(cursor.getColumnIndexOrThrow("DATE_RETURNED"));

            // Create a TextView for each book loan item
            TextView textView = new TextView(this);
            textView.setText("Access Number: " + accessNo +
                    "\nBranch ID: " + branchID +
                    "\nCard Number: " + cardNo +
                    "\nDate Out: " + dateOut +
                    "\nDate Due: " + dateDue +
                    "\nDate Returned: " + dateReturned + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            // Add the TextView to the lendingListLayout
            lendingListLayout.addView(textView);
        }
        cursor.close();
    }
}

