package com.example.tin.roboticapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tin on 18/01/2018.
 */

public class TheCompany implements Parcelable {

    private int company_id;
    private int companyId;
    private String companyticker;
    private String companyName;
    private int companySector;

    // The Constructor
    public TheCompany (int company_id, int companyId, String companyticker, String companyName, int companySector) {
        this.company_id = company_id;
        this.companyId = companyId;
        this.companyticker = companyticker;
        this.companyName = companyName;
        this.companySector = companySector;
    }

    /** Parcelable Code */
    protected TheCompany(Parcel in) {
        company_id = in.readInt();
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
        parcel.writeInt(company_id);
        parcel.writeInt(companyId);
        parcel.writeString(companyticker);
        parcel.writeString(companyName);
        parcel.writeInt(companySector);
    }

    /** Getters */
    public int getCompany_id() { return company_id; }

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
