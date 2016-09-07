package com.xuie.videothumbnailloaderdemo;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuie on 16-8-31.
 */

public class MediaSource {

    private static final String TAG = "MediaSource";

    private static MediaSource instance;

    public static MediaSource getInstance() {
        if (instance == null) {
            synchronized (MediaSource.class) {
                instance = new MediaSource();
            }
        }
        return instance;
    }

    private List<Media> mVideoList = new ArrayList<>();

    private MediaSource() {
        loadVideo();
    }

    public List<Media> getVideoList() {
        return mVideoList;
    }

    private void loadVideo() {
        Cursor cursor = null;
        try {
            cursor = App.getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                Log.e(TAG, "loadMedias cursor is null ");
                return;
            }

            mVideoList.clear();

            while (cursor.moveToNext()) {
                Media media = new Media();
                media.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                media.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                media.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                media.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)));
                media.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                media.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
//                media.setAbulmId(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM_ID)));
                media.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                mVideoList.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
