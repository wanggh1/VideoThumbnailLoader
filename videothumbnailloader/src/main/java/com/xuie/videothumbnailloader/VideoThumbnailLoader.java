package com.xuie.videothumbnailloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.xuie.videothumbnailloader.util.BitmapUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by xuie on 16-9-7.
 */
public class VideoThumbnailLoader {

    private static final String TAG = "VideoThumbnailLoader";
    private MemoryCache mMCache;//一级缓存,内存缓存
    private VideoThumbnailConfiguration configuration;

    private static VideoThumbnailLoader ins = new VideoThumbnailLoader();

    public static VideoThumbnailLoader get() {
        return ins;
    }

    private VideoThumbnailLoader() {
    }

    public void init(VideoThumbnailConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("VideoThumbnailLoader configuration can not be initialized with null");
        }

        this.configuration = configuration;

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(configuration.context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        L.writeLogs(false);

        mMCache = ImageLoader.getInstance().getMemoryCache();
    }

    public void display(String url, ImageView iv, int width, int height, ThumbnailListener thumbnailListener) {
        //使用AsyncTask自带的线程池
        new ThumbnailLoadTask(url, iv, width, height, thumbnailListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class ThumbnailLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView iv;
        private ThumbnailListener thumbnailListener;
        private int width;
        private int height;

        public ThumbnailLoadTask(String url, ImageView iv, int width, int height, ThumbnailListener thumbnailListener) {
            this.url = url;
            this.iv = iv;
            this.width = width;
            this.height = height;
            this.thumbnailListener = thumbnailListener;
        }

        @Override protected Bitmap doInBackground(Void... params) {
            /**
             * 注意,由于我们使用了缓存,所以在加载缩略图之前,我们需要去缓存里读取,如果缓存里有,我们则直接获取,如果没有,则去加载.并且加载完成之后记得放入缓存.
             */
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(url)) {
                String key = getMemoryKey(url);
                bitmap = mMCache.get(key);//先去内存缓存取
                if (bitmap == null || bitmap.isRecycled()) {
//                    File file = TLFileUtils.getExternalFile(path, "xxx.png");//创建文件,这里由于项目原因,我就随便写一个,实际情况不是这样,大家留意一下
                    File file = configuration.saveBitmapFileDir;//创建文件,这里由于项目原因,我就随便写一个,实际情况不是这样,大家留意一下
                    if (file.exists()) {//去磁盘缓存取
                        bitmap = BitmapFactory.decodeFile(file.getPath());
                        if (null == bitmap) {
//                            bitmap = getVideoThumbnail(url, width, height, MediaStore.Video.Thumbnails.MICRO_KIND);
                            bitmap = getVideoThumbnail(url, width, height, configuration.kind);
                            try {
                                BitmapUtils.saveBitmapToFile(file, bitmap, Bitmap.CompressFormat.PNG, configuration.quality);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        bitmap = getVideoThumbnail(url, width, height, configuration.kind);
                        if (null == bitmap) {
                            bitmap = getVideoThumbnail(url, width, height, configuration.kind);
                            try {
                                BitmapUtils.saveBitmapToFile(file, bitmap, Bitmap.CompressFormat.PNG, configuration.quality);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (null != bitmap) {
                        mMCache.put(key, bitmap);//存入内存缓存
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
            }

            if (thumbnailListener != null)
                thumbnailListener.onThumbnailLoadCompleted(url, iv, bitmap);//回调
        }
    }

    /**
     * 获取视频的缩略图
     * @param videoPath 视频路径
     * @param width 视频宽度
     * @param height 视频高度
     * @param kind      eg:MediaStore.Video.Thumbnails.MICRO_KIND   MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * imageloader 的内存缓存的 key 以_ 结尾  截取key比较的时候如果没有加_ 会报错崩溃,所以自己自定义
     *
     * @param filePath 文件地址
     */
    private String getMemoryKey(String filePath) {
        String key;
        int index = filePath.lastIndexOf("/");
        key = filePath.substring(index + 1, filePath.length()) + "_";
        return key;
    }

    //自己定义一个回调,通知外部图片加载完毕
    public interface ThumbnailListener {
        void onThumbnailLoadCompleted(String url, ImageView iv, Bitmap bitmap);
    }
}

