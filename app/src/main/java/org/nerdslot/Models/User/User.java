package org.nerdslot.Models.User;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Models.src.Model;

import java.util.ArrayList;

public class User extends Model implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String uid;
    private String name;
    private String email;
    private ArrayList<Profile> providers;
    private String phone;
    private String gender;
    private String dob;
    private String photoUri;
    private boolean isEmailVerified;

    public User() {
    }

    public User(Builder builder) {
        uid = builder.uid;
        name = builder.name;
        email = builder.email;
        providers = builder.providers;
        phone = builder.phone;
        gender = builder.gender;
        dob = builder.dob;
        photoUri = builder.photoUri;
        isEmailVerified = builder.isEmailVerified;
    }

    // Parcelable Methods
    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        gender = in.readString();
        photoUri = in.readString();
        isEmailVerified = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(photoUri);
        dest.writeByte((byte) (isEmailVerified ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Getters and Setters
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Profile> getProviders() {
        return providers;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public static class Builder {
        private String uid;
        private String name;
        private String email;
        private ArrayList<Profile> providers;
        private String phone;
        private String gender;
        private String dob;
        private String photoUri;
        private boolean isEmailVerified;

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setProviders(ArrayList<Profile> providers) {
            this.providers = providers;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setGender(@NotNull GENDER gender) {
            this.gender = gender.toString();
            return this;
        }

        public Builder setDateOfBirth(String dob) {
            this.dob = dob;
            return this;
        }

        public Builder setPhotoUri(String photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Builder setEmailVerified(boolean emailVerified) {
            isEmailVerified = emailVerified;
            return this;
        }

        public User create() {
            return new User(this);
        }
    }
}
