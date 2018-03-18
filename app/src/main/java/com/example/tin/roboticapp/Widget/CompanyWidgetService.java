package com.example.tin.roboticapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.tin.roboticapp.Fragments.ArticlesFragment;
import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class CompanyWidgetService extends RemoteViewsService {

    private static final String TAG = "CompanyWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CompanyServiceFactory(this.getApplicationContext());
    }

    class CompanyServiceFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context context;
        private ArrayList<Article> mArticles;

        public CompanyServiceFactory(Context context) {

            this.context = context;
            mArticles = new ArrayList<>();
            mArticles.add(new Article(0, "publishDate", "headline", "summary", "sourceUrl"));

        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String theArticlesJson = preferences.getString(ArticlesFragment.SHARED_PREFERENCES_KEY, "");
            if (!theArticlesJson.equals("")) {
                Gson gson = new Gson();
                mArticles = gson.fromJson(theArticlesJson, new TypeToken<ArrayList<Article>>() {
                }.getType());

                Log.d(TAG, "mArticles in onDataSetChanged: " + mArticles);
            }

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (mArticles != null) {
                return mArticles.size();

            } else return 0;

        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.companies_list_item);
            rv.setTextViewText(R.id.widget_articles_tv, String.valueOf(mArticles.get(i).getArticleId()));

            Log.d(TAG, "getCount In Widget Service: comp " + String.valueOf(mArticles.get(i).getArticleId()));


//            rv.setTextViewText(R.id.widget_measure_tv, String.valueOf(mTheIngredients.get(i).getMeasure()));
//            rv.setTextViewText(R.id.widget_quantity_tv, String.valueOf(mTheIngredients.get(i).getQuantity()));
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
