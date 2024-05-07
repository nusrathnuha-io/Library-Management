package com.example.librarymanagement;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "System.db";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1); // Increment the database version
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the 'users' table
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT)");

        // Create the 'Book' table
        db.execSQL("CREATE TABLE Book (" +
                "BOOK_ID VARCHAR(13) PRIMARY KEY," +
                "TITLE VARCHAR(30)," +
                "PUBLISHER_NAME VARCHAR(20))");

        // Create the 'Book_Author' table
        db.execSQL("CREATE TABLE Book_Author (" +
                "BOOK_ID VARCHAR(13)," +
                "AUTHOR_NAME VARCHAR(20)," +
                "PRIMARY KEY(BOOK_ID, AUTHOR_NAME)," +
                "FOREIGN KEY(BOOK_ID) REFERENCES Book(BOOK_ID))");

        // Create the 'Publisher' table
        db.execSQL("CREATE TABLE Publisher (" +
                "NAME VARCHAR(20) PRIMARY KEY," +
                "ADDRESS VARCHAR(30)," +
                "PHONE VARCHAR(10))");

        //create the member table
        db.execSQL("CREATE TABLE Member (" +
                "CARD_NO VARCHAR(10) PRIMARY KEY," +
                "NAME VARCHAR(20)," +
                "ADDRESS VARCHAR(30)," +
                "PHONE VARCHAR(10)," +
                "UNPAID_DUES NUMERIC(5,2))");

        // Create the 'Book_Copy' table
        db.execSQL("CREATE TABLE Book_Copy (" +
                "BOOK_ID VARCHAR(13)," +
                "BRANCH_ID VARCHAR(5)," +
                "ACCESS_NO VARCHAR(5)," +
                "PRIMARY KEY(ACCESS_NO, BRANCH_ID)," +
                "FOREIGN KEY(BOOK_ID) REFERENCES Book(BOOK_ID)," +
                "FOREIGN KEY(BRANCH_ID) REFERENCES Branch(BRANCH_ID))");

        // Create the 'Branch' table
        db.execSQL("CREATE TABLE Branch (" +
                "BRANCH_ID VARCHAR(5) PRIMARY KEY," +
                "BRANCH_NAME VARCHAR(20)," +
                "ADDRESS VARCHAR(30))");

        // Create the 'Book_Loan' table
        db.execSQL("CREATE TABLE Book_Loan (" +
                "ACCESS_NO VARCHAR(5)," +
                "BRANCH_ID VARCHAR(5)," +
                "CARD_NO VARCHAR(10)," +
                "DATE_OUT DATE," +
                "DATE_DUE DATE," +
                "DATE_RETURNED DATE," +
                "PRIMARY KEY(ACCESS_NO, BRANCH_ID, CARD_NO, DATE_OUT)," +
                "FOREIGN KEY(ACCESS_NO, BRANCH_ID) REFERENCES Book_Copy(ACCESS_NO, BRANCH_ID)," +
                "FOREIGN KEY(CARD_NO) REFERENCES Member(CARD_NO)," +
                "FOREIGN KEY(BRANCH_ID) REFERENCES Branch(BRANCH_ID))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS Book");
        db.execSQL("DROP TABLE IF EXISTS Book_Author");
        db.execSQL("DROP TABLE IF EXISTS Publisher");
        db.execSQL("DROP TABLE IF EXISTS Member");
        db.execSQL("DROP TABLE IF EXISTS Book_Copy");
        db.execSQL("DROP TABLE IF EXISTS Branch");
        db.execSQL("DROP TABLE IF EXISTS Book_Loan");

        // Recreate the tables
        onCreate(db);
    }

    public boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }
}
