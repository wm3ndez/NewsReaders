package com.wmendez.newsreader.lib.helpers;


import android.os.Parcel;
import android.os.Parcelable;

public class Entry implements Parcelable {
    public String title;
    public String link;
    public String description;
    public String category;
    public long pubDate;
    public String image;
    public boolean isNew;
    public boolean isFavorite = false;

    public Entry(String title, String link, String description, String category, long pubDate, String image, boolean isNew, boolean isFavorite) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.category = category;
        this.image = image;
        this.pubDate = pubDate;
        this.isNew = isNew;
        this.isFavorite = isFavorite;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(link);
        out.writeString(description);
        out.writeString(category);
        out.writeLong(pubDate);
        out.writeString(image);
        out.writeByte((byte) (isNew ? 1 : 0));
        out.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    private Entry(Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();
        category = in.readString();
        pubDate = in.readLong();
        image = in.readString();
        isNew = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
    }

}
