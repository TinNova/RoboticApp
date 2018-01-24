package com.example.tin.roboticapp.NetworkUtils;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tin on 22/01/2018.
 */

public class UrlUtils {

    private static final String TAG = UrlUtils.class.getSimpleName();

    /**Level1URL:
     *  Companies
     * "http://127.0.0.1:8000/rest-api/companies/"
     *  Questions Feed - No Filter, questions are the same for every company
     * "http://127.0.0.1:8000/rest-api/questions/"
     */

    /** Level2URL:
     *  Companies w/Sector filter
     * "http://127.0.0.1:8000/rest-api/companies/?sector={{Sector_Id}}"
     *  Fundamentals Feed
     * "http://127.0.0.1:8000/rest-api/fundamentals/?company={{Company_Id}}"
     *  Comments Feed
     * "http://127.0.0.1:8000/rest-api/comments/?company={{Company_Id}}"
     */

    /** Articles Feed
     * "http://127.0.0.1:8000/rest-api/articles/rest-api/articles/?format=json&is_useful=yes&mode=company&ticker={{Company_Ticker}}"
     */

    /**
     * Answers Feed
     * "http://127.0.0.1:8000/rest-api/answers/?company={{Company_Id}}&question={{Question_Id}}"
     */

    // BASE_URL
    public static final String BASE_URL = "http://10.0.2.2:8000/rest-api/";

    // FRAGMENT_PATH
    public static final String COMPANIES_PATH = "companies";
    public static final String FUNDAMENTALS_PATH = "fundamentals";
    public static final String COMMENTS_PATH = "comments";
    public static final String QUESTIONS_PATH = "questions";
    public static final String ANSWERS_PATH = "answers";
    public static final String ARTICLES_PATH = "articles";

    // FILTER_TYPE
    public static final String COMPANY_FILTER = "company";
    public static final String SECTOR_FILTER = "sector"; // Only needed for Companies Feed
    public static final String ARTICLES_TICKER_FILTER = "articles/rest-api/articles/?format=json&is_useful=yes&mode=company&ticker";

    // ARTICLE_BASE_URL
    public static final String ARTICLE_BASE_URL = BASE_URL + ARTICLES_TICKER_FILTER;


    /**
     * buildLevel1URL builds the URL for Companies & Questions
     */
    public static URL buildLevel1URL(String FRAGMENT_PATH) {
        Uri builtTrailerUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(FRAGMENT_PATH)
                .build();

        URL url = null;
        try {
            url = new URL(builtTrailerUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Level1URL " + url);

        return url;
    }

    /**
     * buildLevel2URL builds the URL for the Fundamentals, Comments and Companies (when there is a sector filter
     */
    public static URL buildLevel2URL(String FRAGMENT_PATH, String FILTER_TYPE, String ID) {
        Uri builtTrailerUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(FRAGMENT_PATH)
                .appendQueryParameter(FILTER_TYPE, ID)
                .build();

        URL url = null;
        try {
            url = new URL(builtTrailerUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built Level2URL " + url);

        return url;
    }

    /**
     * Articles Feed
     * "http://127.0.0.1:8000/rest-api/articles/rest-api/articles/?format=json&is_useful=yes&mode=company&ticker={{Company_Ticker}}"
     * " http://10.0.2.2:8000/rest-api/articles/rest-api/articles?format=json&is_useful=yes&mode=company&ticker=888"
     * "http://10.0.2.2:8000/rest-api/?format=json&is_useful=yes&mode=company&ticker=888"
     */
    public static URL buildArticleURL(String TICKER) {
        Uri builtTrailerUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("articles")
                .appendPath("rest-api")
                .appendPath("articles")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("is_useful", "yes")
                .appendQueryParameter("mode", "company")
                .appendQueryParameter("ticker", TICKER)
                .build();

        URL url = null;
        try {
            url = new URL(builtTrailerUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built ArticleURL " + url);

        return url;
    }


}
