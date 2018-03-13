package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.Models.Article;
import com.example.tin.roboticapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tin on 23/01/2018.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private final ArrayList<Article> mArticles;
    private final Context context;

    private final ArticleAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick (int clickedItemIndex);
    }


    public ArticleAdapter(ArrayList<Article> mArticles, Context context, ArticleAdapter.ListItemClickListener listener) {
        this.mArticles = mArticles;
        this.context = context;
        mOnClickListener = listener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new View and inflate the list_item Layout into it
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.article_list_item, viewGroup, false);

        // Return the View we just created
        return new ArticleAdapter.ViewHolder(v);

    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param viewHolder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Article theArticle = mArticles.get(position);

        viewHolder.tvHeadline.setText(theArticle.getHeadline());
        viewHolder.tvSummary.setText(theArticle.getSummary());
        viewHolder.tvSourceUrl.setText(theArticle.getSourceUrl());

    }


    // Returns the number of items in the listItems List
    @Override
    public int getItemCount() {
        return mArticles.size();
    }


    // This is the ViewHolder Class, it represents the rows in the RecyclerView (i.e every row is a ViewHolder)
    // In this example each row is made up of an ImageView
    // The ViewHolder also "implements an OnClickListener"
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvHeadline;
        final TextView tvSummary;
        final TextView tvSourceUrl;


        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }

        public ViewHolder(View itemView) {
            super(itemView);

            tvHeadline = (TextView) itemView.findViewById(R.id.tv_headline);
            tvSummary = (TextView) itemView.findViewById(R.id.tv_summary);
            tvSourceUrl = (TextView) itemView.findViewById(R.id.tv_Source_Url);
            itemView.setOnClickListener(this);

        }
    }
}
