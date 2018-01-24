package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 23/01/2018.
 */

public class Article implements Parcelable {

    private int articleId;
    private String publishDate;
    private String headline;
    private String summary;
    private String sourceUrl;
    private int companyId;

    public Article (int articleId, String publishDate, String headline, String summary, String sourceUrl, int companyId) {
        this.articleId = articleId;
        this.publishDate = publishDate;
        this.headline = headline;
        this.summary = summary;
        this.sourceUrl = sourceUrl;
        this.companyId = companyId;
    }

    protected Article (Parcel in) {
        articleId = in.readInt();
        publishDate = in.readString();
        headline = in.readString();
        summary = in.readString();
        sourceUrl = in.readString();
        companyId = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(articleId);
        parcel.writeString(publishDate);
        parcel.writeString(headline);
        parcel.writeString(summary);
        parcel.writeString(sourceUrl);
        parcel.writeInt(companyId);
    }

    public int getArticleId() {
        return articleId;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSummary() {
        return summary;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public int getCompanyId() {
        return companyId;
    }
}
