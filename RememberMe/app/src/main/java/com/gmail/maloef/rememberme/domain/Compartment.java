package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Compartment implements Parcelable {

    int _id;

    int vocabularyBox;
    int number;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeInt(this.vocabularyBox);
        dest.writeInt(this.number);
    }

    public Compartment() {}

    private Compartment(Parcel in) {
        this._id = in.readInt();
        this.vocabularyBox = in.readInt();
        this.number = in.readInt();
    }

    public static final Parcelable.Creator<Compartment> CREATOR = new Parcelable.Creator<Compartment>() {
        public Compartment createFromParcel(Parcel source) {
            return new Compartment(source);
        }

        public Compartment[] newArray(int size) {
            return new Compartment[size];
        }
    };
}
