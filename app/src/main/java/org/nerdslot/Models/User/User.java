package org.nerdslot.Models.User;

import android.os.Parcel;
import android.os.Parcelable;

import org.nerdslot.Models.src.Model;

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
    private String idpToken;
    private String name;
    private String email;
    private Profile provider;
    private String phone;
    private String photoUri;
    private long created_at;
    private long updated_at;

    public User(){}

    // Parcelable Methods
    protected User(Parcel in) {
        uid = in.readString();
        idpToken = in.readString();
        name = in.readString();
        email = in.readString();
        provider = in.readParcelable(Profile.class.getClassLoader());
        phone = in.readString();
        photoUri = in.readString();
        created_at = in.readLong();
        updated_at = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(idpToken);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeParcelable(provider, flags);
        dest.writeString(phone);
        dest.writeString(photoUri);
        dest.writeLong(created_at);
        dest.writeLong(updated_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIdpToken() {
        return idpToken;
    }

    public void setIdpToken(String idpToken) {
        this.idpToken = idpToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Profile getProvider() {
        return provider;
    }

    public void setProvider(Profile provider) {
        this.provider = provider;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }
}
