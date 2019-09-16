package org.nerdslot.Models.Issue;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.nerdslot.Models.src.Model;

public class Issue extends Model implements Parcelable {
    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };
    private String id;
    private String category_id;
    private String magazine_id;
    private String title;
    private String description;
    private String currency;
    private String price;
    private boolean featured;
    private String issueImageUri;
    private Double rateCount;

    public Issue() {}

    private Issue(@NonNull Builder builder) {
        this.id = builder.id;
        this.category_id = builder.category_id;
        this.magazine_id = builder.magazine_id;
        this.title = builder.title;
        this.description = builder.description;
        this.currency = builder.currency;
        this.price = builder.price;
        this.featured = builder.featured;
        this.issueImageUri = builder.issueImageUri;
        this.rateCount = builder.rateCount;
    }

    // Parcelable Implementation
    protected Issue(Parcel in) {
        id = in.readString();
        category_id = in.readString();
        magazine_id = in.readString();
        title = in.readString();
        description = in.readString();
        currency = in.readString();
        price = in.readString();
        featured = in.readByte() != 0;
        issueImageUri = in.readString();
        if (in.readByte() == 0) {
            rateCount = null;
        } else {
            rateCount = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category_id);
        dest.writeString(magazine_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(currency);
        dest.writeString(price);
        dest.writeByte((byte) (featured ? 1 : 0));
        dest.writeString(issueImageUri);
        if (rateCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rateCount);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getMagazine_id() {
        return magazine_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPrice() {
        return price;
    }

    public boolean isFeatured() {
        return featured;
    }

    public String getIssueImageUri() {
        return issueImageUri;
    }

    public Double getRateCount() {
        return rateCount;
    }

    // Builder Class
    public static class Builder {
        private String id;
        private String category_id;
        private String magazine_id;
        private String title;
        private String description;
        private String currency;
        private String price;
        private boolean featured;
        private String issueImageUri;
        private Double rateCount;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setCategory_id(String category_id) {
            this.category_id = category_id;
            return this;
        }

        public Builder setMagazine_id(String magazine_id) {
            this.magazine_id = magazine_id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder setFeatured(boolean featured) {
            this.featured = featured;
            return this;
        }

        public Builder setIssueImageUri(String issueImageUri) {
            this.issueImageUri = issueImageUri;
            return this;
        }

        public Builder setRateCount(Double rateCount) {
            this.rateCount = rateCount;
            return this;
        }

        public Issue build() {
            return new Issue(this);
        }
    }
}
