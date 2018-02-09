package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.R;

public class QaDetailActivity extends AppCompatActivity {

    private String mQuestion;
    private String mAnswer;

    // 0 = New Answer (i.e no answer was passed in the intent) 1 = Editing Answer
    int newAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_detail);

        /** Extracting Data From Intent */
        Intent intentFromQaFrag = getIntent();
        if (intentFromQaFrag.getExtras() != null) {

            mQuestion = intentFromQaFrag.getStringExtra(QaFragment.QUESTION_01);

            if (intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01) != null) {

                mAnswer = intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01);
                newAnswer = 1;

            }

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }
    }
}
