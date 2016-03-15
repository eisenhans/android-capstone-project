package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {

    public int id;

    public int boxId;
    public int compartment;

    public String foreignWord;
    public String nativeWord;

    // creationDate must not be null in database, but can be null for entity (because creation date was not fetched from database)
    public Long creationDate;
    public Long updateDate;
    public Long lastRepeatDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.boxId);
        dest.writeInt(this.compartment);
        dest.writeString(this.foreignWord);
        dest.writeString(this.nativeWord);
        dest.writeLong(this.creationDate);
        dest.writeLong(this.updateDate);
        dest.writeLong(this.lastRepeatDate);
    }

    public Word() {}

    private Word(Parcel in) {
        this.id = in.readInt();
        this.boxId = in.readInt();
        this.compartment = in.readInt();
        this.foreignWord = in.readString();
        this.nativeWord = in.readString();
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

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", boxId=" + boxId +
                ", compartment=" + compartment +
                ", foreignWord='" + foreignWord + '\'' +
                ", nativeWord='" + nativeWord + '\'' +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                ", lastRepeatDate=" + lastRepeatDate +
                '}';
    }
}
