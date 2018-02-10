package com.example.tin.roboticapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tin.roboticapp.Fragments.QaFragment;
import com.example.tin.roboticapp.R;

public class QaDetailActivity extends AppCompatActivity {

    private String mQuestion;
    private String mAnswer;

    private EditText mAnswerView;

    // 0 = New Answer (i.e no answer was passed in the intent)
    // 1 = Editing Answer (i.e we're editing an existing answer)
    int newAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_detail);

        mAnswerView = (EditText) findViewById(R.id.answer_editText);

        /** Extracting Data From Intent */
        // if intent is not null, open the intent Bundle
        Intent intentFromQaFrag = getIntent();
        if (intentFromQaFrag.getExtras() != null) {

            mQuestion = intentFromQaFrag.getStringExtra(QaFragment.QUESTION_01);

            // if the answer is not null then extract it, then put the answer within the EditText
            // and mark the newAnswer as 1
            if (intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01) != null) {

                mAnswer = intentFromQaFrag.getStringExtra(QaFragment.ANSWER_01);
                mAnswerView.setText(mAnswer);
                newAnswer = 1;

            }

        } else {

            Toast.makeText(this, "ERROR: Data didn't load correctly", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qa_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String newAnswer = mAnswerView.getText().toString();

        /** HERE INSERT THE POST FUNCTION USING THE NEW ANSWER */

        Toast.makeText(this, "Answer Saved.", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

}

// TODO: Add a save button that triggers the POST function
// COMMIT!
// TODO: Pass the Answer ID to the qaDetailActivity
// COMMIT!
// TODO: Add POST function
// COMMIT!
// TODO: Have an UP button that takes you specifically to the QA Fragment (NOT a random fragment)
// COMMIT!
// TODO: Fix the layout a little
// COMMIT!
// TODO: Work on the POST function for Comments
