package com.example.tin.roboticapp.SQLite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.tin.roboticapp.SQLite.FavouriteContract.FavouriteEntry.TABLE_NAME;

/**
 * Created by Tin on 06/03/2018.
 */

public class FavouriteContentProvider extends ContentProvider {

    /** Steps To Creating A ContentProvider
     * 1a. Create a ContentProvider class and extend ContentProvider
     * 1b. Implement the onCreate() function
     * 2.  Register the ContentProvider in the AndroidManifest
     * 3.  Define the URI's
     * 4.  Add these URI's to the Contract
     * 5.  Build a URI Matcher
     * 6.  Implement the required CRUD methods (Query, Insert, Delete) */

    // Defining final integer constants for the directory of favouritesMovies and a single Item
    // It's convention to use 100, 200, 300 ect for directories,
    // and related ints (101, 102, ..) for items in that directory.
    private static final int FAVOURITECOMPANY = 100;
    private static final int FAVOURITECOMPANY_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Defining a static buildUriMatcher method that associates URI's with their int match
    private static UriMatcher buildUriMatcher() {
        // .NO_MATCH defines it as an empty uriMatcher (because we haven't added an int match yet
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add matches with addURI (String authority, String path, int code), this means we are adding the Uri
        // This is for an entire directory
        // content://com.example.tin.robotic/favouriteCompanies/100
        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVOURITE_COMPANIES, FAVOURITECOMPANY);
        // This is for a single item
        // content://com.example.tin.robotic/favouriteCompanies/2/101
        uriMatcher.addURI(FavouriteContract.AUTHORITY, FavouriteContract.PATH_FAVOURITE_COMPANIES + "/#", FAVOURITECOMPANY_WITH_ID);

        return uriMatcher;
    }

    // Variable of the FavouriteDbHelper so it can be initialised in onCreate
    private FavouriteDbHelper mFavouriteDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavouriteDbHelper = new FavouriteDbHelper(context);

        // Return true because the method is done
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        // Initialise the SQLite database as a getWritableDatabase so we can insert data
        final SQLiteDatabase favouritesDB = mFavouriteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVOURITECOMPANY:
                // Inserting values into favourites SQLite table
                // This insert should return an id, if unsuccessful it will return -1
                // if successful, it should take the id and return a new uri for that new item
                long id = favouritesDB.insert(TABLE_NAME, null, contentValues);
                // if statement to check if the insert was successful
                if (id > 0) {
                    // success
                    returnUri = ContentUris.withAppendedId(
                            FavouriteContract.FavouriteEntry.CONTENT_URI, id);

                } else { // id is -1, therefore it failed
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Here we notify the resolver that the uri has been changed

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
