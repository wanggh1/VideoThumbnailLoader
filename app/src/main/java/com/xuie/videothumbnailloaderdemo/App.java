package com.xuie.videothumbnailloaderdemo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;

import com.xuie.videothumbnailloader.VideoThumbnailConfiguration;
import com.xuie.videothumbnailloader.VideoThumbnailLoader;

import java.io.File;

/**
 * Created by xuie on 16-9-7.
 */

public class App extends Application {
    private static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = this;

        VideoThumbnailConfiguration.Builder config = new VideoThumbnailConfiguration.Builder(this);
        config.setKind(MediaStore.Video.Thumbnails.MINI_KIND);
        config.setQuality(100);
        config.setSaveBitmapFileDir(new File(Environment.getExternalStorageDirectory().getPath() + "/VideothumbnailCached/"));
        VideoThumbnailLoader.get().init(config.build());
    }

    public static Context getContext() {
        return context;
    }
}
