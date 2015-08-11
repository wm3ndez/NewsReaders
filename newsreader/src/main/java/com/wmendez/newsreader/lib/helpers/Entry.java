package com.wmendez.newsreader.lib.helpers;


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wmendez.newsreader.lib.provider.Contract;

public class Entry implements Parcelable {
    public String diary;
    public String title;
    public String link;
    public String description;
    public String category;
    public long pubDate;
    public String image;
    public boolean isNew;

    public Entry(String diary, String title, String link, String description, String category, long pubDate, String image, boolean isNew) {
        this.diary = diary;
        this.title = title;
        this.link = link;
        this.description = description;
        this.category = category;
        this.image = image;
        this.pubDate = pubDate;
        this.isNew = isNew;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(diary);
        out.writeString(title);
        out.writeString(link);
        out.writeString(description);
        out.writeString(category);
        out.writeLong(pubDate);
        out.writeString(image);
        out.writeByte((byte) (isNew ? 1 : 0));
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
        diary = in.readString();
        title = in.readString();
        link = in.readString();
        description = in.readString();
        category = in.readString();
        pubDate = in.readLong();
        image = in.readString();
        isNew = in.readByte() != 0;
    }

    public static Entry fromCursor(Cursor cursor) {
        return new Entry(
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_NEWSPAPER)),
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_TITLE)),
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_URL)),
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_CATEGORY)),
                cursor.getLong(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_PUB_DATE)),
                cursor.getString(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_IMAGE)),
                cursor.getInt(cursor.getColumnIndex(Contract.NewsTable.COLUMN_NAME_IS_NEW)) == 1
        );

    }

}
