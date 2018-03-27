package com.example.tin.roboticapp.SQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelper is only used when first creating the Database on a users Android Device and when
// updating the Database
public class FavouriteDbHelper extends SQLiteOpenHelper {

    // The name of the database as it will be saved on the user Android Device
    private static final String DATABASE_NAME = "favouriteCompanies.db";
    private static final int DATABASE_VERSION = 5;

    // Constructor that takes a context and calls the parent constructor
    public FavouriteDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_COMPANIES_TABLE = "CREATE TABLE " +
                FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
                FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ID + " INTEGER NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_TICKER + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_NAME + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_SECTOR + " INTEGER NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_QA_LIST + " TEXT, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ARTICLES_LIST + " TEXT, " +
                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_PRICE + " TEXT" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_COMPANIES_TABLE);

    }

    // ORIGINAL
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        final String SQL_CREATE_FAVOURITE_COMPANIES_TABLE = "CREATE TABLE " +
//                FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
//                FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ID + " INTEGER NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_TICKER + " TEXT NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_NAME + " TEXT NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_SECTOR + " INTEGER NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_QA_LIST + " TEXT NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_ARTICLES_LIST + " TEXT NOT NULL, " +
//                FavouriteContract.FavouriteEntry.COLUMN_COMPANY_PRICE + " TEXT NOT NULL" +
//                "); ";
//
//        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_COMPANIES_TABLE);
//
//    }

    // This is only called when the DATABASE_VERSION number is upgraded to let's say 1.1
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}