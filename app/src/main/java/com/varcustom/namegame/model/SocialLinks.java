package com.varcustom.namegame.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialLinks implements Parcelable {

    public static final Creator<SocialLinks> CREATOR = new Creator<SocialLinks>() {
        @Override
        public SocialLinks createFromParcel(Parcel source) {
            return new SocialLinks(source);
        }

        @Override
        public SocialLinks[] newArray(int size) {
            return new SocialLinks[size];
        }
    };
    private final String type;
    private final String callToAction;
    private final String url;

    public SocialLinks(String type,
                       String callToAction,
                       String url) {
        this.type = type;
        this.callToAction = callToAction;
        this.url = url;
    }

    private SocialLinks(Parcel in) {
        this.type = in.readString();
        this.callToAction = in.readString();
        this.url = in.readString();

    }

    public String getType() {
        return type;
    }

    public String getCallToAction() {
        return callToAction;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.callToAction);
        dest.writeString(this.url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}