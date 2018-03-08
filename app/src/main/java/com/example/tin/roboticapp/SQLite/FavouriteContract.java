package com.example.tin.roboticapp.SQLite;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tin on 06/03/2018.
 */

public class FavouriteContract {

    // The Authority, this is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.tin.roboticapp";
    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // Here we define the possible paths for accessing data in this contract
    // This is the path for the "favouriteCompanies" directory (the favouriteFilms table)
    public static final String PATH_FAVOURITE_COMPANIES = "favouriteCompanies";

    // We've made the class private here to prevent anyone accidentally instantiating the contract class
    private FavouriteContract() {}

    // BaseColumns is what creates the automatic Id's for each row.
    // We don't need to create a column for the ID sections as BaseColumns does it automatically
    public static final class FavouriteEntry implements BaseColumns {

        // FavouriteEntry content URI = BASE_CONTENT_URI + PATH_FAVOURITE_COMPANIES
        // content://com.example.tin.roboticapp/favouriteCompanies
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_COMPANIES).build();

        // Table name & Column names
        public static final String TABLE_NAME = "favouriteCompanies";
        public static final String COLUMN_COMPANY_ID = "companyId";
        public static final String COLUMN_COMPANY_TICKER = "companyTicker";
        public static final String COLUMN_COMPANY_NAME = "companyName";
        public static final String COLUMN_COMPANY_SECTOR = "companySector";
        public static final String COLUMN_COMPANY_QA_LIST = "companyQaList";
        public static final String COLUMN_COMPANY_ARTICLES_LIST = "companyArticlesList";
        public static final String COLUMN_COMPANY_PRICE = "companyPrice";

    }

}