package gallery.decode.com.gallery;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

public class Media implements Parcelable {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private String mName;
    private int mType;
    private long mDuration;
    private String mUrl;

    public Media(int type, String name, long duration, String url) {
        mType = type;
        mName = name;
        mDuration = duration;
        mUrl = url;
    }

    protected Media(Parcel in) {
        mName = in.readString();
        mType = in.readInt();
        mDuration = in.readLong();
        mUrl = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getUrl() {
        return mUrl;
    }


    public static List<Media> getMedia(Context context, int type) {

        Cursor cursor;

        Uri uriUrl = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Video.Media.DURATION
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                (type == TYPE_IMAGE ? MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE : MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);

        CursorLoader cursorLoader = new CursorLoader(context, uriUrl, projection, selection, null, null);
        cursor = cursorLoader.loadInBackground();

        List<Media> media = new ArrayList<>();
        cursor.moveToFirst();
        do {
            media.add(new Media(type, cursor.getString(5), cursor.getLong(6), cursor.getString(1)));
        } while (cursor.moveToNext());

        cursor.close();

        return media;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeInt(mType);
        parcel.writeLong(mDuration);
        parcel.writeString(mUrl);
    }
}
