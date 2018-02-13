package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 30/01/2018.
 */

public class Answer implements Parcelable {

    private int id;
    private int question;
    private int company;
    private String content;

    // Constructor
    public Answer(int id, int question, int company, String content) {
        this.id = id;
        this.question = question;
        this.company = company;
        this.content = content;
    }


    protected Answer(Parcel in) {
        id = in.readInt();
        question = in.readInt();
        company = in.readInt();
        content = in.readString();
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(question);
        parcel.writeInt(company);
        parcel.writeString(content);
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getQuestion() {
        return question;
    }

    public int getCompany() {
        return company;
    }

    public String getContent() {
        return content;
    }
}
