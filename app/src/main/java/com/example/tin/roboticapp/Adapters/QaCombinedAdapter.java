package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.Models.QACombined;
import com.example.tin.roboticapp.R;

import java.util.ArrayList;

/**
 * Created by Tin on 18/02/2018.
 */

public class QaCombinedAdapter extends RecyclerView.Adapter<QaCombinedAdapter.ViewHolder> {

    private final ArrayList<QACombined> mQACombined;
    private final Context context;

    private final QaCombinedAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick (int clickedItemIndex);
    }

    public QaCombinedAdapter(ArrayList<QACombined> mQACombined, Context context, QaCombinedAdapter.ListItemClickListener mOnClickListener) {
        this.mQACombined = mQACombined;
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public QaCombinedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new View and inflate the list_item Layout into it
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.qa_list_item, viewGroup, false);

        // Return the View we just created
        return new QaCombinedAdapter.ViewHolder(v);
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

        QACombined currentQA = mQACombined.get(position);

        viewHolder.tvQuestion.setText((currentQA.getQuestion()));

        if (currentQA.getContent() != ""){

            viewHolder.tvAnswer.setText((currentQA.getContent()));
        }

    }

    @Override
    public int getItemCount() {
        return mQACombined.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvQuestion;
        final TextView tvAnswer;

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }

        public ViewHolder(View itemView) {
            super(itemView);

            tvQuestion = (TextView) itemView.findViewById(R.id.tV_rV_question);
            tvAnswer = (TextView) itemView.findViewById(R.id.tV_rV_answer);
            itemView.setOnClickListener(this);

        }

    }
}
