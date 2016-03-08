package com.gmail.maloef.rememberme.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Language implements Parcelable {

    public int id;

    public String code;
    public String name;
    public String nameCode;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.nameCode);
    }

    public Language() {}

    protected Language(Parcel in) {
        this.id = in.readInt();
        this.code = in.readString();
        this.name = in.readString();
        this.nameCode = in.readString();
    }

    public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
        public Language createFromParcel(Parcel source) {return new Language(source);}

        public Language[] newArray(int size) {return new Language[size];}
    };
}
