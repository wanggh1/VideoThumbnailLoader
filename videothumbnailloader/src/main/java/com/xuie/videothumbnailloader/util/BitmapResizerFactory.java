package com.xuie.videothumbnailloader.util;

/**
 * Created by xuie on 16-9-6.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import java.io.InputStream;

public class BitmapResizerFactory {
    private static final String TAG = "BitmapResizerFactory";

    /**
     * This method creates a circular bitmap from the bitmap passed as parameter with center as bitmap center and radius
     * equal to half of shorter side of bitmap
     *
     * @param bitmap
     * @return Returns a circular bitmap
     */
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        Bitmap output = createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        if (output == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float radius = (width < height) ? width / 2 : height / 2;

        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * Converts a text view into a bitmap
     *
     * @param textView
     * @return
     */
    public static Bitmap getBitMapFromTV(View textView) {
        // capture bitmap of generated textview
        int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        Bitmap b = createBitmap(textView.getWidth(), textView.getHeight(), Config.ARGB_8888);
        if (b == null) {
            return null;
        }
        Canvas canvas = new Canvas(b);
        canvas.translate(-textView.getScrollX(), -textView.getScrollY());
        textView.draw(canvas);
        textView.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = textView.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Config.ARGB_8888, true);
        if (cacheBmp != null) {
            cacheBmp.recycle();
        }
        textView.destroyDrawingCache(); // destory drawable
        return viewBmp;
    }

    /**
     * Returns a bitmapDrawable created from the string passed as parameter
     *
     * @param encodedString
     * @return
     */
    public static BitmapDrawable stringToDrawable(String encodedString) {
        if (TextUtils.isEmpty(encodedString)) {
            return null;
        }
        byte[] thumbnailBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return getBitmapDrawable(decodeBitmapFromByteArray(thumbnailBytes, Config.RGB_565));
    }

    /**
     * Returns a bitmap created from the string passed as parameter
     *
     * @param thumbnailString
     * @return
     */
    public static Bitmap stringToBitmap(String thumbnailString) {
        byte[] encodeByte = Base64.decode(thumbnailString, Base64.DEFAULT);
        return decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    /**
     * Rotates a bitmap by <code>degrees</code> angle
     *
     * @param bitmap
     * @param degrees
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            Bitmap b2 = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (b2 != null) {
                if (bitmap != b2) {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
        }
        return bitmap;
    }

    public static BitmapDrawable getBitmapDrawable(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(bitmap);
    }

    /**
     * Crops the bitmap from all sides to make it a square bitmap
     *
     * @param thumbnail
     * @return
     */
    public static Bitmap makeSquareThumbnail(Bitmap thumbnail) {
        return makeSquareThumbnail(thumbnail, -1);
    }

    /**
     * Crops the bitmap from all sides to make it a square bitmap , if parameter <code>dimensionLimit</code> is given as
     * -1 then the side of the square bitmap will be equal to the shorter side of the original bitmap otherwise it will
     * be equal to dimensionLimit
     *
     * @param thumbnail
     * @param dimensionLimit
     * @return
     */
    private static Bitmap makeSquareThumbnail(Bitmap thumbnail, int dimensionLimit) {
        if (dimensionLimit == -1) {
            dimensionLimit = thumbnail.getWidth() < thumbnail.getHeight() ? thumbnail.getWidth() : thumbnail.getHeight();
        }

        int startX = thumbnail.getWidth() > dimensionLimit ? (int) ((thumbnail.getWidth() - dimensionLimit) / 2) : 0;
        int startY = thumbnail.getHeight() > dimensionLimit ? (int) ((thumbnail.getHeight() - dimensionLimit) / 2) : 0;

        Log.d("Utils", "StartX: " + startX + " StartY: " + startY + " WIDTH: " + thumbnail.getWidth() + " Height: " + thumbnail.getHeight());
        Log.d("Utils", "dimensionLimit : " + dimensionLimit);

        Bitmap squareThumbnail = createBitmap(thumbnail, startX, startY, startX + dimensionLimit, startY + dimensionLimit);

        if (squareThumbnail != thumbnail) {
            thumbnail.recycle();
        }
        thumbnail = null;
        return squareThumbnail;
    }

    public static Bitmap scaleDownBitmapInDp(String filename, int reqWidthInDp, int reqHeightInDp, boolean finResMoreThanReq, boolean scaleUp) {
        return scaleDownBitmapInDp(filename, reqWidthInDp, reqHeightInDp, Config.ARGB_8888, finResMoreThanReq, scaleUp);
    }

    public static Bitmap scaleDownBitmapInDp(String filename, int dimensionInDp, Config config) {
        int dimension = (int) (dimensionInDp * BitmapUtils.getDensityMultiplier());
        return scaleDownBitmap(filename, dimension, config);
    }

    public static Bitmap scaleDownBitmapInDp(String filename, int reqWidthInDp, int reqHeightInDp, Config config, boolean finResMoreThanReq, boolean scaleUp) {
        int reqHeight = (int) (reqHeightInDp * BitmapUtils.getDensityMultiplier());
        int reqWidth = (int) (reqWidthInDp * BitmapUtils.getDensityMultiplier());
        return scaleDownBitmap(filename, reqWidth, reqHeight, config, finResMoreThanReq, scaleUp);
    }

    /**
     * This method accepts a file path and returns scaled down bitmap if finResMoreThanReq is set to true than return
     * bitmap resolution will be atleast reqHeight and reqWidth and if set to false will be at most reqWidth and
     * reqHeight
     *
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @param finResMoreThanReq
     * @param scaleUp
     * @return
     */
    public static Bitmap scaleDownBitmap(String filename, int reqWidth, int reqHeight, boolean finResMoreThanReq, boolean scaleUp) {
        return scaleDownBitmap(filename, reqWidth, reqHeight, Config.ARGB_8888, finResMoreThanReq, scaleUp);
    }

    /**
     * This method accepts a file path and returns a square bitmap , it first scales down the bitmap as much as possible
     * and then crops it to form a square bitmap see {@link #makeSquareThumbnail(Bitmap, int)}
     *
     * @param filename
     * @param dimension
     * @param config
     * @return
     */
    public static Bitmap scaleDownBitmap(String filename, int dimension, Config config) {
        Bitmap bitmap = scaleDownBitmap(filename, dimension, dimension, config, true, false);
        return makeSquareThumbnail(bitmap, dimension);
    }

    /**
     * This method accepts a file path and returns a scaled down bitmap if finResMoreThanReq is set to true than return
     * bitmap resolution will be atleast reqHeight and reqWidth and if set to false will be at most reqWidth and
     * reqHeight.
     *
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @param config
     * @param finResMoreThanReq
     * @param scaleUp           true if required width and height is greater than source file width and height And you want to scale
     *                          the resultant bitmap to required width and height
     * @return
     */
    public static Bitmap scaleDownBitmap(Resources res, int resId, int reqWidth, int reqHeight, Config config, boolean finResMoreThanReq, boolean scaleUp) {
        Bitmap unscaledBitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight, config);

        if (unscaledBitmap == null) {
            return null;
        }

        Bitmap small = createScaledBitmap(unscaledBitmap, reqWidth, reqHeight, config, true, finResMoreThanReq, scaleUp);

        if (unscaledBitmap != small) {
            Log.d(TAG, "UnscaledBitmap is not same as Smaller bitmap, Recycling Unscaled !!");
            unscaledBitmap.recycle();
        }

        return small;

    }

    /**
     * This method accepts a file path and returns a scaled down bitmap if finResMoreThanReq is set to true than return
     * bitmap resolution will be atleast reqHeight and reqWidth and if set to false will be at most reqWidth and
     * reqHeight.
     *
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @param config
     * @param finResMoreThanReq
     * @param scaleUp           true if required width and height is greater than source file width and height And you want to scale
     *                          the resultant bitmap to required width and height
     * @return
     */
    public static Bitmap scaleDownBitmap(String filename, int reqWidth, int reqHeight, Config config, boolean finResMoreThanReq, boolean scaleUp) {
        Bitmap unscaledBitmap = decodeSampledBitmapFromFile(filename, reqWidth, reqHeight, config);

        if (unscaledBitmap == null) {
            return null;
        }

        Bitmap small = createScaledBitmap(unscaledBitmap, reqWidth, reqHeight, config, true, finResMoreThanReq, scaleUp);

        if (unscaledBitmap != small) {
            Log.d(TAG, "UnscaledBitmap is not same as Smaller bitmap, Recycling Unscaled !!");
            unscaledBitmap.recycle();
        }

        return small;

    }

    /**
     * This method accepts a bitmap and returns a scaled down bitmap if finResMoreThanReq is set to true than return
     * bitmap resolution will be atleast reqHeight and reqWidth and if set to false will be at most reqWidth and
     * reqHeight.
     *
     * @param unscaledBitmap
     * @param reqWidth
     * @param reqHeight
     * @param config
     * @param filter
     * @param finResMore
     * @param scaleUp        true if required width and height is greater than source file width and height And you want to scale
     *                       the resultant bitmap to required width and height
     * @return
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int reqWidth, int reqHeight, Config config, boolean filter, boolean finResMore, boolean scaleUp) {
        if (unscaledBitmap == null) {
            return null;
        }

        if (scaleUp || (reqHeight < unscaledBitmap.getHeight() && reqWidth < unscaledBitmap.getWidth())) {
            Rect srcRect = new Rect(0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight());

            Rect reqRect = calculateReqRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), reqWidth, reqHeight, finResMore);

            Bitmap scaledBitmap = createBitmap(reqRect.width(), reqRect.height(), config);

            if (scaledBitmap == null) {
                return null;
            }

            Canvas canvas = new Canvas(scaledBitmap);
            Paint p = new Paint();
            p.setFilterBitmap(filter);
            canvas.drawBitmap(unscaledBitmap, srcRect, reqRect, p);
            return scaledBitmap;
        } else {
            return unscaledBitmap;
        }
    }

    /**
     * This method returns a android.graphics.Rect object whose height and width is calculated by maintaining source's
     * aspect ratio and if parameter <code>finResMore</code> is true then returned Rect object width and height will be
     * equal to more than required width and height
     *
     * @param srcWidth   source bitmap width
     * @param srcHeight  source bitmap height
     * @param reqWidth   Required bitmap width
     * @param reqHeight  Required bitmap height
     * @param finResMore if true means returned Rect object will have width and height greater than or equal to required width
     *                   and height while maintaining the aspect ratio
     * @return
     */
    private static Rect calculateReqRect(int srcWidth, int srcHeight, int reqWidth, int reqHeight, boolean finResMore) {
        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) reqWidth / (float) reqHeight;

        if (finResMore) {
            if (srcAspect > dstAspect) {
                return new Rect(0, 0, (int) (reqHeight * srcAspect), reqHeight);
            } else {
                return new Rect(0, 0, reqWidth, (int) (reqWidth / srcAspect));
            }
        } else {
            if (srcAspect > dstAspect) {
                return new Rect(0, 0, reqWidth, (int) (reqWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (reqHeight * srcAspect), reqHeight);
            }
        }
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     *
     * @param res       The resources object containing the image data
     * @param resId     The resource id of the image data
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache     The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or
     * greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight, Config.ARGB_8888);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight, Config con) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeResource(res, resId, options);

        options.inPreferredConfig = con;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap result = null;
        try {
            result = decodeResource(res, resId, options);
        } catch (IllegalArgumentException e) {
            result = decodeResource(res, resId);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from resources: ", e);
        }
        return result;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename  The full path of the file to decode
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache     The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or
     * greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromFile(filename, reqWidth, reqHeight, Config.ARGB_8888);
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, Config con) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeFile(filename, options);

        options.inPreferredConfig = con;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap result = null;
        try {
            result = decodeFile(filename, options);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException in decoding Bitmap from file: ", e);
            result = decodeFile(filename, options);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from file: ", e);
        }
        return result;
    }

    /**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename  The full path of the file to decode
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache     The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or
     * greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] bytearray, int reqWidth, int reqHeight) {
        return decodeSampledBitmapFromByteArray(bytearray, reqWidth, reqHeight, Config.ARGB_8888);
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] bytearray, int reqWidth, int reqHeight, Config con) {
        if (bytearray == null)
            return null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeByteArray(bytearray, 0, bytearray.length, options);

        options.inPreferredConfig = con;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap result = null;
        try {
            result = decodeByteArray(bytearray, 0, bytearray.length, options);
        } catch (IllegalArgumentException e) {
            result = decodeByteArray(bytearray, 0, bytearray.length);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from ByteArray: ", e);
        }
        return result;
    }

    /**
     * This method decodes a bitmap from byte array with particular configuration config passed as a parameter. Bitmap
     * will not be sampled , only configuration will be config. To sample down bitmap use
     * decodeSampledBitmapFromByteArray
     *
     * @param bytearray
     * @param con
     * @return
     */
    public static Bitmap decodeBitmapFromByteArray(byte[] bytearray, Config config) {
        if (bytearray == null)
            return null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        Bitmap result = null;
        try {
            result = decodeByteArray(bytearray, 0, bytearray.length, options);
        } catch (IllegalArgumentException e) {
            result = decodeByteArray(bytearray, 0, bytearray.length);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from ByteArray: ", e);
        }
        return result;
    }

    /**
     * This method uses the configuration given by config to decode a bitmap from file.
     *
     * @param filename
     * @param con
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String filename, Config config) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        Bitmap result = null;
        try {
            result = decodeFile(filename, options);
        } catch (IllegalArgumentException e) {
            result = decodeFile(filename);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from file: ", e);
        }
        return result;
    }

    /**
     * This method uses the configuration given by config to decode a bitmap from resource.
     *
     * @param filename
     * @param con
     * @return
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, Config config) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;

        Bitmap result = null;
        try {
            result = decodeResource(res, resId, options);
        } catch (IllegalArgumentException e) {
            result = decodeResource(res, resId);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from file: ", e);
        }
        return result;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding bitmaps using the
     * decode* methods from {@link BitmapFactory}. This implementation calculates the closest inSampleSize that is a
     * power of 2 and will result in the final decoded bitmap having a width and height equal to or larger than the
     * requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode* method with
     *                  inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }

        }
        return inSampleSize;
    }

    /**
     * Decode and sample down a bitmap from resources to the requested inSampleSize.
     *
     * @param res          The resources object containing the image data
     * @param resId        The resource id of the image data
     * @param inSampleSize The value to be used for inSampleSize
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions that are equal to or
     * greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int inSampleSize) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;
        Bitmap result = null;
        try {
            result = decodeResource(res, resId, options);
        } catch (IllegalArgumentException e) {
            result = decodeResource(res, resId);
        } catch (Exception e) {
            Log.e(TAG, "Exception in decoding Bitmap from resources: ", e);
        }
        return result;
    }

    public static Bitmap createBitmap(int width, int height, Config con) {
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(width, height, con);
            Log.wtf(TAG, "Bitmap size in bytes in createBitmap : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = Bitmap.createBitmap(width, height, con);
                Log.wtf(TAG, "Bitmap size in bytes in createBitmap : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, " Exception in createBitmap : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in createBitmap : ", e);
        }
        return b;
    }

    private static Bitmap createBitmap(Bitmap thumbnail, int startX, int startY, int i, int j) {
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(thumbnail, startX, startY, i, j);
            Log.wtf(TAG, "Bitmap size in createBitmap : " + BitmapUtils.getBitmapSize(b));

        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = Bitmap.createBitmap(thumbnail, startX, startY, i, j);
                Log.wtf(TAG, "Bitmap size in createBitmap : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in createBitmap : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in createBitmap : ", e);
        }
        return b;
    }

    public static Bitmap createBitmap(Bitmap bm, int i, int j, int width, int height, Matrix m, boolean c) {
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(bm, i, j, width, height, m, c);
            Log.wtf(TAG, "Bitmap size in createBitmap : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = Bitmap.createBitmap(bm, i, j, width, height, m, c);
                Log.wtf(TAG, "Bitmap size in createBitmap : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in createBitmap : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in createBitmap : ", e);
        }
        return b;
    }

    public static Bitmap decodeFile(String path) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(path);
            Log.d(TAG, "Bitmap size in decodeFile (KB) : " + BitmapUtils.getBitmapSize(b) / 1024);
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeFile(path);
                Log.d(TAG, "Bitmap size in decodeFile (KB) : " + BitmapUtils.getBitmapSize(b) / 1024);
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeFile : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeFile : ", e);
        }
        return b;
    }

    public static Bitmap decodeFile(String path, BitmapFactory.Options opt) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(path, opt);
            Log.d(TAG, "Bitmap size in decodeFile (KB) : " + BitmapUtils.getBitmapSize(b) / 1024);
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "DecodeFile Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeFile(path, opt);
                Log.d(TAG, "Bitmap size in decodeFile : " + BitmapUtils.getBitmapSize(b) / 1024);
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeFile : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeFile : ", e);
        }
        return b;
    }

    public static Bitmap decodeStream(InputStream is) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(is);
            Log.wtf(TAG, "Bitmap size in decodeStream : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeStream(is);
                Log.wtf(TAG, "Bitmap size in decodeStream : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeStream : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeStream : ", e);
        }
        return b;
    }

    public static Bitmap decodeResource(Resources res, int id) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeResource(res, id);
            Log.wtf(TAG, "Bitmap size in decodeResource : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeResource(res, id);
                Log.wtf(TAG, "Bitmap size in decodeResource : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeResource : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeResource : ", e);
        }
        return b;
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opt) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeResource(res, id, opt);
            Log.wtf(TAG, "Bitmap size in decodeResource : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeResource(res, id, opt);
                Log.wtf(TAG, "Bitmap size in decodeResource : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeResource : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeResource : ", e);
        }
        return b;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeByteArray(data, offset, length);
            Log.wtf(TAG, "Bitmap size in decodeByteArray : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeByteArray(data, offset, length);
                Log.wtf(TAG, "Bitmap size in decodeByteArray : " + BitmapUtils.getBitmapSize(b));
            } catch (OutOfMemoryError ex) {
                Log.wtf(TAG, "Out of Memory even after System.gc");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeByteArray : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeByteArray : ", e);
        }
        return b;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, BitmapFactory.Options opt) {
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeByteArray(data, offset, length, opt);
            Log.d(TAG, "Bitmap size in decodeByteArray : " + BitmapUtils.getBitmapSize(b));
        } catch (OutOfMemoryError e) {
            Log.wtf(TAG, "Out of Memory");

            System.gc();

            try {
                b = BitmapFactory.decodeByteArray(data, offset, length, opt);
                Log.d(TAG, "Bitmap size in decodeByteArray (KB) : " + BitmapUtils.getBitmapSize(b) / 1024);
            } catch (OutOfMemoryError ex) {
                Log.e(TAG, "Out of Memory even after System.gc called");
            } catch (Exception exc) {
                Log.e(TAG, "Exception in decodeByteArray : ", exc);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in decodeByteArray : ", e);
        }
        return b;
    }

}
