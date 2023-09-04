package com.webling.graincorp.data.api.model.response.onboarding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child {
    @SerializedName("OnboardPage")
    @Expose
    private int onboardPage;
    @SerializedName("OnboardTitle")
    @Expose
    private String onboardTitle;
    @SerializedName("OnboardText")
    @Expose
    private String onBoardText;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;

    public int getOnboardPage() {
        return onboardPage;
    }

    public String getOnboardTitle() {
        return onboardTitle;
    }

    public String getOnBoardText() {
        return onBoardText;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
