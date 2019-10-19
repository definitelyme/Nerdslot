package org.nerdslot.Models.Issue;

import android.os.Parcel;
import android.os.Parcelable;

import org.nerdslot.Models.src.Model;

public class Featured extends Model implements Parcelable {
    public static final Creator<Featured> CREATOR = new Creator<Featured>() {
        @Override
        public Featured createFromParcel(Parcel in) {
            return new Featured(in);
        }

        @Override
        public Featured[] newArray(int size) {
            return new Featured[size];
        }
    };
    private String id;
    private String image;

    public Featured() {
    }

    public Featured(Builder builder) {
        this.id = builder.id;
        this.image = builder.image;
    }

    protected Featured(Parcel in) {
        id = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public static class Builder {
        private String id;
        private String image;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Featured build() {
            return new Featured(this);
        }
    }
}
