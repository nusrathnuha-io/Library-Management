package com.example.librarymanagement;





import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText retypePasswordEditText;
    private Button signUpButton;

    private DBHelper DB; // Assuming DBHelper is a class for database interactions

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle onCreate;
        onCreate = null;
        super.onCreate(onCreate);
        setContentView(R.layout.activity_register); // Set the layout from activity_main.xml

        // Find view references by ID
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        retypePasswordEditText = findViewById(R.id.repassword);
        signUpButton = findViewById(R.id.btnsignup);

        DB = new DBHelper(this); // Initialize DBHelper instance

        // Set up button click listeners
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String retypePassword = retypePasswordEditText.getText().toString().trim();

                // Input validation
                if (username.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Password matching check
                if (!password.equals(retypePassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for existing username using DBHelper
                if (DB.checkUsername(username)) {
                    Toast.makeText(RegisterActivity.this  , "Username already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Registration logic
                boolean insert = DB.insertData(username, password);
                if (insert) {
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SystemActivity.class); // Assuming LoginActivity exists
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
