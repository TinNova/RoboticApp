package com.example.tin.roboticapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tin.roboticapp.Models.Answer;
import com.example.tin.roboticapp.Models.Question;
import com.example.tin.roboticapp.R;

import java.util.ArrayList;

/**
 * Created by Tin on 13/02/2018.
 */

public class QaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "QaAdapter";

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.qa_list_item, viewGroup, false);

        if(viewType == VIEW_TYPE_QUESTION){
            return new QuestionViewHolder(v);
        }

        if(viewType == VIEW_TYPE_ANSWER){
            return new AnswerViewHolder(v);
        }

        return null;

//        // Create a new View and inflate the list_item Layout into it
//        View v = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.qa_list_item, viewGroup, false);
//
//        // Return the View we just created
//        return new QaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof QuestionViewHolder){
            ((QuestionViewHolder) viewHolder).populate(mQuestions.get(position));
        }

        if(viewHolder instanceof AnswerViewHolder){
            ((AnswerViewHolder) viewHolder).populate(mAnswers.get(position - mQuestions.size()));
        }

//        Question currentQuestion = mQuestions.get(position);
//        Answer currentAnswer = mAnswers.get(position);
//
//        int QuestionId = currentQuestion.getId();
//        int Answer_QuestionId = currentAnswer.getQuestion();
//
//        Log.v(TAG, "Current Question ID: " + currentQuestion);
//        Log.v(TAG, "Current Answer ID: " + currentAnswer);
//
//        // By default we are only entering the question to the TextView
//        viewHolder.tVQuestion.setText(currentQuestion.getQuestion());
//
//        // However, if QuestionId & Answer_QuestionId match then fill in the answer too
////        if (QuestionId == Answer_QuestionId) {
//
//            viewHolder.tVAnswer.setText(currentAnswer.getContent());
//
//            // Else, only fill in the QuestionId as an Answer doesn't exist for it...
////        }

    }

    @Override
    public int getItemCount() {
        return mQuestions.size() + mAnswers.size();
    }

    @Override
    public int getItemViewType(int position){
        if(position < mQuestions.size()){
            return VIEW_TYPE_QUESTION;
        }

        if(position - mQuestions.size() < mAnswers.size()){
            return VIEW_TYPE_ANSWER;
        }

        return -1;
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView tVQuestion;

        public QuestionViewHolder(View itemView){
            super(itemView);

            tVQuestion = (TextView) itemView.findViewById(R.id.tV_rV_question);
        }

        public void populate(Question question){
            tVQuestion.setText(question.getQuestion());

        }
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {

        TextView tVAnswer;

        public AnswerViewHolder(View itemView){
            super(itemView);

            tVAnswer = (TextView) itemView.findViewById(R.id.tV_rV_question);
        }

        public void populate(Answer answer){
            tVAnswer.setText(answer.getContent());

        }
    }

//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        final TextView tVQuestion;
//        final TextView tVAnswer;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            tVQuestion = (TextView) itemView.findViewById(R.id.tV_rV_question);
//            tVAnswer = (TextView) itemView.findViewById(R.id.tV_rV_answer);
//        }
//    }
}