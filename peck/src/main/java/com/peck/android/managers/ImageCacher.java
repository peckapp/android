package com.peck.android.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Vector;

/**
 * Created by mammothbane on 6/20/2014.
 */
public class ImageCacher implements Singleton {
    private static ImageCacher cacher = new ImageCacher();

    private static final Bitmap imageNotAvailable;
    private static final String TAG = "ImageCacher";
    private static final String CACHE_NAME = "img_cache";
    private static final File CACHE_DIR = new File(PeckApp.AppContext.getContext().getCacheDir(), CACHE_NAME);
    private static Bitmap userImage;

    private static LruCache<Integer, Bitmap> cache = new LruCache<Integer, Bitmap>(PeckApp.Constants.Graphics.CACHE_SIZE);
    private static Vector<Integer> noImageAvailable = new Vector<Integer>(PeckApp.Constants.Graphics.INT_CACHE_SIZE);

    private ImageCacher() { }

    static {
        imageNotAvailable = BitmapFactory.decodeResource(PeckApp.AppContext.getContext().getResources(),
                PeckApp.Constants.Graphics.FILLER);
        CACHE_DIR.mkdir();

    }

    public static void init(String defaultImageName, final int userId) {
        if (defaultImageName != null)
            if (new File(defaultImageName).exists()) {
                Log.i(TAG, "loading from file saved to disk");
                FileInputStream in = null;
                try {
                    in = new FileInputStream(defaultImageName);
                    userImage = BitmapFactory.decodeFile(defaultImageName);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    get(userId, new Callback<Bitmap>() {
                        @Override
                        public void callBack(Bitmap obj) {
                            userImage = obj;
                        }
                    });
                } finally {
                    try {
                        in.close();
                    } catch (Throwable ignore) {
                    }
                }
            } else {
                Log.i(TAG, "loading default");
                get(userId, new Callback<Bitmap>() {
                    @Override
                    public void callBack(Bitmap obj) {
                        userImage = obj;
                    }
                });
            }

    }

    public static ImageCacher getCacher() { return cacher; }

    public static void get(final int userId, final Callback<Bitmap> callback) {
        Bitmap ret;

        if (userId == PeckSessionManager.getUser().getLocalId() && userImage != null) ret = userImage;
        else if (noImageAvailable.contains(userId)) ret = imageNotAvailable;
        else {
            ret = cache.get(userId);
            if (ret == null) {
                    PeckSessionManager.getImage(userId, new Callback<Bitmap>() {
                        @Override
                        public void callBack(Bitmap obj) {
                            if (obj != null) {
                            cache.put(userId, obj);
                            callback.callBack(obj); }
                            else callback.callBack(imageNotAvailable);
                        }
                    });
            }
        }
    }

    public static void forceUpdate(int resId, Callback<Bitmap> callback) {
        noImageAvailable.remove(Integer.valueOf(resId));
        get(resId, callback);
    }

    protected static void writeCacheToDisk() { //clears the cache
        FileOutputStream out;
        Map<Integer, Bitmap> snapshot = cache.snapshot();
        for (Integer i : snapshot.keySet()) {
            try {
                out = new FileOutputStream(new File(CACHE_DIR, i.toString()));
                snapshot.get(i).compress(Bitmap.CompressFormat.PNG, PeckApp.Constants.Graphics.PNG_COMPRESSION, out);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "couldn't write cached image for user " + i + " to disk\n" + e.toString());
            }
        }
    }

    private static void readCacheFromDisk() {
        for (File file : CACHE_DIR.listFiles()) {
            cache.put(Integer.parseInt(file.getName()), BitmapFactory.decodeFile(file.getPath()));
        }
    }


}
