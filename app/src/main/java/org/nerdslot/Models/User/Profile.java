package org.nerdslot.Models.User;

import android.os.Parcel;
import android.os.Parcelable;

public class Profile implements Parcelable {
    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
    private String uid;
    private String accessToken;
    private String provider;
    private String displayName;
    private String email;
    private String photoUri;
    private String phone;
    private boolean isEmailVerified;
    private boolean tokenExpired;

    public Profile(){}

    // Parcelable Methods
    protected Profile(Parcel in) {
        uid = in.readString();
        accessToken = in.readString();
        provider = in.readString();
        displayName = in.readString();
        email = in.readString();
        photoUri = in.readString();
        phone = in.readString();
        isEmailVerified = in.readByte() != 0;
        tokenExpired = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(accessToken);
        dest.writeString(provider);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(photoUri);
        dest.writeString(phone);
        dest.writeByte((byte) (isEmailVerified ? 1 : 0));
        dest.writeByte((byte) (tokenExpired ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpires(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }
}
