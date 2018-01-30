package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 18/01/2018.
 */

public class TheCompany implements Parcelable {

    private int companyId;
    private String companyticker;
    private String companyName;
    private int companySector;

    // The Constructor
    public TheCompany (int companyId, String companyticker, String companyName, int companySector) {
        this.companyId = companyId;
        this.companyticker = companyticker;
        this.companyName = companyName;
        this.companySector = companySector;
    }

    /** Parcelable Code */
    protected TheCompany(Parcel in) {
        companyId = in.readInt();
        companyticker = in.readString();
        companyName = in.readString();
        companySector = in.readInt();
    }

    public static final Creator<TheCompany> CREATOR = new Creator<TheCompany>() {
        @Override
        public TheCompany createFromParcel(Parcel in) {
            return new TheCompany(in);
        }

        @Override
        public TheCompany[] newArray(int size) {
            return new TheCompany[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(companyId);
        parcel.writeString(companyticker);
        parcel.writeString(companyName);
        parcel.writeInt(companySector);
    }

    /** Getters */
    public int getCompanyId() {
        return companyId;
    }

    public String getCompanyticker() {
        return companyticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getCompanySector() {
        return companySector;
    }
}
