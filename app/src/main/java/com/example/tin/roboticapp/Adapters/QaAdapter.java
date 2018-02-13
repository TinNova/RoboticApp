package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.tin.roboticapp.Models.Answer;
import com.example.tin.roboticapp.Models.Question;

import java.util.ArrayList;

/**
 * Created by Tin on 13/02/2018.
 */

public class QaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_QUESTION = 0;
    final int VIEW_TYPE_ANSWER = 1;

    private final ArrayList<Question> mQuestions;
    private final ArrayList<Answer> mAnswers;
    private final Context context;

    public QaAdapter(ArrayList<Question> mQuestions, ArrayList<Answer> mAnswers, Context context) {
        this.mQuestions = mQuestions;
        this.mAnswers = mAnswers;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
