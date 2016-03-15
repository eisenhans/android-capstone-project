package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class VocabularyBox implements Parcelable {

    public static final int TRANSLATION_DIRECTION_FOREIGN_TO_NATIVE = 0;
    public static final int TRANSLATION_DIRECTION_NATIVE_TO_FOREIGN = 1;
    public static final int TRANSLATION_DIRECTION_MIXED = 2;

    public int id;

    public String name;
    public String foreignLanguage;
    public String nativeLanguage;
    public int translationDirection;
    public boolean isCurrent;

    public VocabularyBox() {}

    protected VocabularyBox(Parcel in) {
        id = in.readInt();
        name = in.readString();
        foreignLanguage = in.readString();
        nativeLanguage = in.readString();
        translationDirection = in.readInt();
        isCurrent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(foreignLanguage);
        dest.writeString(nativeLanguage);
        dest.writeInt(translationDirection);
        dest.writeByte((byte) (isCurrent ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VocabularyBox> CREATOR = new Creator<VocabularyBox>() {
        @Override
        public VocabularyBox createFromParcel(Parcel in) {
            return new VocabularyBox(in);
        }

        @Override
        public VocabularyBox[] newArray(int size) {
            return new VocabularyBox[size];
        }
    };
}
