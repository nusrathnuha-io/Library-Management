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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class membersActivity extends AppCompatActivity {

    EditText editTextCardNo, editTextName, editTextAddress, editTextPhone, editTextUnpaidDues;
    Button addButton, deleteButton, updateButton;
    LinearLayout buttonLayout;
    ScrollView scrollViewMembers;
    LinearLayout memberListLayout;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        editTextCardNo = findViewById(R.id.editTextCardNo);
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextUnpaidDues = findViewById(R.id.editTextUnpaidDues);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        buttonLayout = findViewById(R.id.buttonLayout);
        scrollViewMembers = findViewById(R.id.scrollView);
        memberListLayout = findViewById(R.id.ListLayout);
        dbHelper = new DBHelper(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMember();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMember();
            }
        });

        displayMembers();
        Button homeButton = findViewById(R.id.HomeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(membersActivity.this, SystemActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    // Handle exception (e.g., display error message)
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMember() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CARD_NO", editTextCardNo.getText().toString());
        values.put("NAME", editTextName.getText().toString());
        values.put("ADDRESS", editTextAddress.getText().toString());
        values.put("PHONE", editTextPhone.getText().toString());
        values.put("UNPAID_DUES", editTextUnpaidDues.getText().toString());
        long newRowId = db.insert("Member", null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error adding member", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
            displayMembers();
        }
    }

    private void deleteMember() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = "CARD_NO = ?";
        String[] selectionArgs = { editTextCardNo.getText().toString() };
        int deletedRows = db.delete("Member", selection, selectionArgs);
        if (deletedRows == 0) {
            Toast.makeText(this, "No member found with the given card number", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Member deleted successfully", Toast.LENGTH_SHORT).show();
            displayMembers();
        }
    }

    private void updateMember() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME", editTextName.getText().toString());
        values.put("ADDRESS", editTextAddress.getText().toString());
        values.put("PHONE", editTextPhone.getText().toString());
        values.put("UNPAID_DUES", editTextUnpaidDues.getText().toString());
        String selection = "CARD_NO = ?";
        String[] selectionArgs = { editTextCardNo.getText().toString() };
        int count = db.update(
                "Member",
                values,
                selection,
                selectionArgs);
        if (count == 0) {
            Toast.makeText(this, "No member found with the given card number", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Member updated successfully", Toast.LENGTH_SHORT).show();
            displayMembers();
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayMembers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "CARD_NO",
                "NAME",
                "ADDRESS",
                "PHONE",
                "UNPAID_DUES"
        };
        Cursor cursor = db.query(
                "Member",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        memberListLayout.removeAllViews(); // Clear existing views

        while (cursor.moveToNext()) {
            String cardNo = cursor.getString(cursor.getColumnIndexOrThrow("CARD_NO"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("PHONE"));
            String unpaidDues = cursor.getString(cursor.getColumnIndexOrThrow("UNPAID_DUES"));

            // Create a TextView for each member item
            TextView textView = new TextView(this);
            textView.setText("Card No: " + cardNo +
                    "\nName: " + name +
                    "\nAddress: " + address +
                    "\nPhone: " + phone +
                    "\nUnpaid Dues: " + unpaidDues + "\n");
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            // Add the TextView to the memberListLayout
            memberListLayout.addView(textView);
        }
        cursor.close();
    }


}
