package com.webling.graincorp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class OnboardingItem implements Parcelable {

    private int onboardPage;
    private String onboardTitle;
    private String onBoardText;
    private String imageUrl;

    public int getOnboardPage() {
        return onboardPage;
    }

    public void setOnboardPage(int onboardPage) {
        this.onboardPage = onboardPage;
    }

    public String getOnboardTitle() {
        return onboardTitle;
    }

    public void setOnboardTitle(String onboardTitle) {
        this.onboardTitle = onboardTitle;
    }

    public String getOnBoardText() {
        return onBoardText;
    }

    public void setOnBoardText(String onBoardText) {
        this.onBoardText = onBoardText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.onboardPage);
        dest.writeString(this.onboardTitle);
        dest.writeString(this.onBoardText);
        dest.writeString(this.imageUrl);
    }

    public OnboardingItem() {
    }

    protected OnboardingItem(Parcel in) {
        this.onboardPage = in.readInt();
        this.onboardTitle = in.readString();
        this.onBoardText = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<OnboardingItem> CREATOR = new Parcelable.Creator<OnboardingItem>() {
        @Override
        public OnboardingItem createFromParcel(Parcel source) {
            return new OnboardingItem(source);
        }

        @Override
        public OnboardingItem[] newArray(int size) {
            return new OnboardingItem[size];
        }
    };
}
