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
    private String title;
    private String slug;
    private String magazineUri;
    private String coverUri;

    public Magazine() {
    }

    private Magazine(@NonNull Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.slug = builder.slug;
        this.magazineUri = builder.magazineUri;
        this.coverUri = builder.coverUri;
    }

    // Parcelable Implementation
    protected Magazine(Parcel in) {
        id = in.readString();
        title = in.readString();
        slug = in.readString();
        magazineUri = in.readString();
        coverUri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(slug);
        dest.writeString(magazineUri);
        dest.writeString(coverUri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getMagazineUri() {
        return magazineUri;
    }

    public void setMagazineUri(String magazineUri) {
        this.magazineUri = magazineUri;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    // Builder Class
    public static class Builder {
        private String id;
        private String title;
        private String slug;
        private String magazineUri;
        private String coverUri;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
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

        public Builder setCoverUri(String coverUri) {
            this.coverUri = coverUri;
            return this;
        }

        public Magazine build() {
            return new Magazine(this);
        }
    }
}
