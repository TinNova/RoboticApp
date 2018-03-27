package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 30/01/2018.
 */

public class Comment implements Parcelable {

    private int id;
    private int author;
    private String creation_date;
    private int company;
    private String content;
    private String author_full_name;

    // The Constructor
    public Comment(int id, int author, String creation_date, int company, String content, String author_full_name) {
        this.id = id;
        this.author = author;
        this.creation_date = creation_date;
        this.company = company;
        this.content = content;
        this.author_full_name = author_full_name;
    }

    /** Parcelable Code */
    protected Comment(Parcel in) {
        id = in.readInt();
        author = in.readInt();
        creation_date = in.readString();
        company = in.readInt();
        content = in.readString();
        author_full_name = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(id);
        parcel.writeInt(author);
        parcel.writeString(creation_date);
        parcel.writeInt(company);
        parcel.writeString(content);
        parcel.writeString(author_full_name);
    }

    /** Getters */
    public int getId() {
        return id;
    }

    public int getAuthor() {
        return author;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public int getCompany() {
        return company;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor_full_name() {

        return author_full_name;

    }

}