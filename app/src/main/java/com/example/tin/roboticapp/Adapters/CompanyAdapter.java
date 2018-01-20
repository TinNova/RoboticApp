package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tin.roboticapp.Models.TheCompany;
import com.example.tin.roboticapp.R;

import java.util.List;


public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private final List<TheCompany> mTheCompanies;
    private final Context context;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick (int clickedItemIndex);
    }


    public CompanyAdapter(List<TheCompany> mTheCompanies, Context context, ListItemClickListener listener) {
        this.mTheCompanies = mTheCompanies;
        this.context = context;
        mOnClickListener = listener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new View and inflate the list_item Layout into it
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.companies_list_item, viewGroup, false);

        // Return the View we just created
        return new ViewHolder(v);

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

        TheCompany theCompany = mTheCompanies.get(position);

        viewHolder.tvCompanyName.setText(theCompany.getCompanyName());
        viewHolder.tvTicker.setText(theCompany.getCompanyticker());

    }

    // Returns the number of items in the listItems List
    @Override
    public int getItemCount() {
        return mTheCompanies.size();
    }


    // This is the ViewHolder Class, it represents the rows in the RecyclerView (i.e every row is a ViewHolder)
    // In this example each row is made up of an ImageView
    // The ViewHolder also "implements an OnClickListener"
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tvCompanyName;
        final TextView tvTicker;


        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }

        public ViewHolder(View itemView) {
            super(itemView);

            tvCompanyName = (TextView) itemView.findViewById(R.id.tvCompanyName);
            tvTicker = (TextView) itemView.findViewById(R.id.tvTicker);
            itemView.setOnClickListener(this);

        }
    }
}
