package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class VocabularyBox implements Parcelable {

    static final int TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE = 0;
    static final int TRANSLATION_DIRECTION_NATIVE_TO_FOREIGN = 1;
    static final int TRANSLATION_DIRECTION_MIXED = 2;

    int _id;

    String name;
    String nativeLanguage;
    String foreignLanguage;
    int translationDirection;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.name);
        dest.writeString(this.nativeLanguage);
        dest.writeString(this.foreignLanguage);
        dest.writeInt(this.translationDirection);
    }

    public VocabularyBox() {}

    private VocabularyBox(Parcel in) {
        this._id = in.readInt();
        this.name = in.readString();
        this.nativeLanguage = in.readString();
        this.foreignLanguage = in.readString();
        this.translationDirection = in.readInt();
    }

    public static final Parcelable.Creator<VocabularyBox> CREATOR = new Parcelable.Creator<VocabularyBox>() {
        public VocabularyBox createFromParcel(Parcel source) {
            return new VocabularyBox(source);
        }

        public VocabularyBox[] newArray(int size) {
            return new VocabularyBox[size];
        }
    };
}
