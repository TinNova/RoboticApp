package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 30/01/2018.
 */

public class Question implements Parcelable {

    private int id;
    private String question;
    private int position;

    // Constructor
    public Question(int id, String question, int position) {
        this.id = id;
        this.question = question;
        this.position = position;
    }

    protected Question(Parcel in) {
        id = in.readInt();
        question = in.readString();
        position = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(question);
        parcel.writeInt(position);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public int getPosition() {
        return position;
    }
}

