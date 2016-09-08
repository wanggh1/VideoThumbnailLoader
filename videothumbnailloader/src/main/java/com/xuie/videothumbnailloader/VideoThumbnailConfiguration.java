package com.xuie.videothumbnailloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.OutputStream;

/**
 * Created by xuie on 16-9-8.
 */
public class VideoThumbnailConfiguration {
    Context context;
    File saveBitmapFileDir;
    int quality;
    int kind;

    public VideoThumbnailConfiguration(Builder builder) {
        context = builder.context;
        saveBitmapFileDir = builder.saveBitmapFileDir;
        quality = builder.quality;
        kind = builder.kind;
    }

    public static class Builder {
        private Context context;
        private File saveBitmapFileDir = new File(Environment.getDataDirectory().getPath() + "VideoThumbnailCache");
        /**
         * {@linkplain android.graphics.Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)}
         */
        private int quality = 100;
        /**
         * {@linkplain android.provider.MediaStore.Video.Thumbnails#KIND}
         */
        private int kind = MediaStore.Video.Thumbnails.MINI_KIND;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setSaveBitmapFileDir(File saveBitmapFileDir) {
            this.saveBitmapFileDir = saveBitmapFileDir;
            return this;
        }

        public Builder setQuality(int quality) {
            this.quality = quality;
            return this;
        }

        public Builder setKind(int kind) {
            this.kind = kind;
            return this;
        }

        public VideoThumbnailConfiguration build() {
            return new VideoThumbnailConfiguration(this);
        }

    }

}
