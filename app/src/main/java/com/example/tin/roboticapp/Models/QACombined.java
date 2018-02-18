package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 17/02/2018.
 */

public class QACombined implements Parcelable {

    // Question
    private int qId;
    private String question;
    private int position;
    // Answer
    private int aId;
    private int a_qId;
    private int company;
    private String content;

    // Constructor
    public QACombined(int qId, String question, int position, int aId, int a_qId, int company, String content) {
        this.qId = qId;
        this.question = question;
        this.position = position;
        this.aId = aId;
        this.a_qId = a_qId;
        this.company = company;
        this.content = content;

    }

    public QACombined(Parcel in) {
        qId = in.readInt();
        question = in.readString();
        position = in.readInt();
        aId = in.readInt();
        a_qId = in.readInt();
        company = in.readInt();
        content = in.readString();
    }

    public static final Creator<QACombined> CREATOR = new Creator<QACombined>() {
        @Override
        public QACombined createFromParcel(Parcel in) {
            return new QACombined(in);
        }

        @Override
        public QACombined[] newArray(int size) {
            return new QACombined[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(qId);
        parcel.writeString(question);
        parcel.writeInt(position);
        parcel.writeInt(aId);
        parcel.writeInt(a_qId);
        parcel.writeInt(company);
        parcel.writeString(content);
    }

    // Questions
    public int getqId() {
        return qId;
    }

    public String getQuestion() {
        return question;
    }

    public int getPosition() {
        return position;
    }

    // Answers
    public int getaId() {
        return aId;
    }

    public int getA_qId() {
        return a_qId;
    }

    public int getCompany() {
        return company;
    }

    public String getContent() {
        return content;
    }
}
