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
    private String providerName;
    private String displayName;
    private String photoUri;
    private String phone;

    public Profile() {
    }

    // Parcelable Methods
    protected Profile(Parcel in) {
        uid = in.readString();
        providerName = in.readString();
        displayName = in.readString();
        photoUri = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(providerName);
        dest.writeString(displayName);
        dest.writeString(photoUri);
        dest.writeString(phone);
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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
}
