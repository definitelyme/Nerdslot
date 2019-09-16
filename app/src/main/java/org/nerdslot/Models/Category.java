package org.nerdslot.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.nerdslot.Models.src.Model;

public class Category extends Model implements Parcelable {
    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    private String id;
    private String name;
    private String description;
    
    public Category() {}

    private Category(@NonNull Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
    }

    // Parcelable Implementation
    protected Category(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Builder Class
    public static class Builder{
        private String id;
        private String name;
        private String description;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Category build(){
            return new Category(this);
        }
    }
}
