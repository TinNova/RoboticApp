package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.Models.Comment;
import com.example.tin.roboticapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tin on 30/01/2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private String TAG = "CommentAdapter";
    private final ArrayList<Comment> mComments;
    private final Context context;

    private final CommentAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public CommentAdapter(ArrayList<Comment> mComments, Context context, CommentAdapter.ListItemClickListener listener) {
        this.mComments = mComments;
        this.context = context;
        mOnClickListener = listener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new View and inflate the list_item Layout into it
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comments_list_item, viewGroup, false);

        // Return the View we just created
        return new CommentAdapter.ViewHolder(v);

    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                   contents of the item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder viewHolder, int position) {

        Comment theComment = mComments.get(position);

        viewHolder.tvContent.setText(theComment.getContent());
        viewHolder.tvAuthor.setText(String.valueOf(theComment.getAuthor_full_name()));
        viewHolder.tvCreationDate.setText(theComment.getCreation_date());

    }

    // Returns the number of items in the listItems List
    @Override
    public int getItemCount() {
        return mComments.size();
    }


    // This is the ViewHolder Class, it represents the rows in the RecyclerView (i.e every row is a ViewHolder)
    // In this example each row is made up of an ImageView
    // The ViewHolder also "implements an OnClickListener"
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvContent;
        final TextView tvAuthor;
        final TextView tvCreationDate;


        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }

        public ViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_comment_author);
            tvCreationDate = (TextView) itemView.findViewById(R.id.tv_comment_creation_date);
            itemView.setOnClickListener(this);

        }
    }
}
