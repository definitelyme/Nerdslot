package org.nerdslot.Models.Issue;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.nerdslot.Models.src.Model;

public class Magazine extends Model implements Parcelable {
    public static final Creator<Magazine> CREATOR = new Creator<Magazine>() {
        @Override
        public Magazine createFromParcel(Parcel in) {
            return new Magazine(in);
        }

        @Override
        public Magazine[] newArray(int size) {
            return new Magazine[size];
        }
    };
    private String id;
    private String slug;
    private String magazineUri;
    private String images;
    private int imageCount;

    public Magazine() {
    }

    private Magazine(@NonNull Builder builder) {
        this.id = builder.id;
        this.slug = builder.slug;
        this.magazineUri = builder.magazineUri;
        this.images = builder.images;
        this.imageCount = builder.imageCount;
    }

    // Parcelable Implementation
    protected Magazine(Parcel in) {
        id = in.readString();
        slug = in.readString();
        magazineUri = in.readString();
        images = in.readString();
        imageCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(slug);
        dest.writeString(magazineUri);
        dest.writeString(images);
        dest.writeInt(imageCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getImages() {
        return images;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setMagazineUri(String magazineUri) {
        this.magazineUri = magazineUri;
    }

    // Builder Class
    public static class Builder {
        private String id;
        private String slug;
        private String magazineUri;
        private String images;
        private int imageCount;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setSlug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder setMagazineUri(String magazineUri) {
            this.magazineUri = magazineUri;
            return this;
        }

        public Builder setImages(String image) {
            this.images = image;
            return this;
        }

        public Builder setImageCount(int count) {
            this.imageCount = count;
            return this;
        }

        public Magazine build() {
            return new Magazine(this);
        }
    }
}
