package com.xuie.videothumbnailloader.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xuie on 16-9-6.
 */

public class BitmapUtils {
    private static float densityMultiplier = 1.0f;

    private static int densityDpi;

    public static final String DRAWABLES = "drawables";

    /**
     * Used for setting the density multiplier, which is to be multiplied with any pixel value that is programmatically
     * given
     *
     * @param displayMetrics
     */
    public static void setDensityMultiplier(DisplayMetrics displayMetrics) {
        BitmapUtils.densityMultiplier = displayMetrics.scaledDensity;
        BitmapUtils.densityDpi = displayMetrics.densityDpi;
    }

    public static int getDensityDpi() {
        return BitmapUtils.densityDpi;
    }

    public static float getDensityMultiplier() {
        return BitmapUtils.densityMultiplier;
    }

    public static boolean isBitmapSquare(Bitmap bitmap) {
        return (bitmap.getWidth() == bitmap.getHeight());
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        return bitmapToBytes(bitmap, format, 50);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        if (bitmap == null) {
            byte[] b = new byte[]{0};
            return b;
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(format, quality, bao);
        return bao.toByteArray();
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap) throws IOException {
        saveBitmapToFile(file, bitmap, Bitmap.CompressFormat.PNG, 70);
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);

        byte[] b = BitmapUtils.bitmapToBytes(bitmap, compressFormat, quality);
        if (b == null) {
            fos.close();
            throw new IOException();
        }
        fos.write(b);
        fos.flush();
        fos.close();
    }

    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat) onward this returns
     * the allocated memory size of the bitmap which can be larger than the actual bitmap data byte count (in the case
     * it was re-used).
     *
     * @param value
     * @return size in bytes
     */
    public static int getDrawableSize(Drawable bd) {
        if (bd == null)
            return 0;

        if (bd instanceof BitmapDrawable)
            return getBitmapSize(((BitmapDrawable) bd).getBitmap());
        else
            return 0;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        // From KitKat onward use getAllocationByteCount() as allocated bytes
        // can potentially be
        // larger than bitmap byte count.
        if (Utils.isKitkatOrHigher()) {
            return bitmap.getAllocationByteCount();
        }

        if (Utils.isHoneycombMR1OrHigher()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Returns a bitmap from resource name
     *
     * @param context
     * @param resName Name of the resource for which bitmap is needed
     * @return
     */
    public static Bitmap getBitmapFromResourceName(Context context, String resName) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(resName, DRAWABLES, context.getPackageName());
        return BitmapResizerFactory.decodeBitmapFromResource(resources, resourceId, Bitmap.Config.ARGB_8888);
    }
}
