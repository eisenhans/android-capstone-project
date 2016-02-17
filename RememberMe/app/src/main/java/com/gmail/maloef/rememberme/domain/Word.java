package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {

    int _id;

    String foreignWord;
    String nativeWord;
    int compartment;

    long creationDate;
    long updateDate;
    long lastRepeatDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.foreignWord);
        dest.writeString(this.nativeWord);
        dest.writeInt(this.compartment);
        dest.writeLong(this.creationDate);
        dest.writeLong(this.updateDate);
        dest.writeLong(this.lastRepeatDate);
    }

    public Word() {}

    private Word(Parcel in) {
        this._id = in.readInt();
        this.foreignWord = in.readString();
        this.nativeWord = in.readString();
        this.compartment = in.readInt();
        this.creationDate = in.readLong();
        this.updateDate = in.readLong();
        this.lastRepeatDate = in.readLong();
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
