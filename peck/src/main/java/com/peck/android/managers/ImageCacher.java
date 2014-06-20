package com.peck.android.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

/**
 * Created by mammothbane on 6/20/2014.
 */
public class ImageCacher implements Singleton {
    private static ImageCacher cacher = new ImageCacher();

    private static final Bitmap imageNotAvailable;
    private static final String TAG = "ImageCacher";
    private static Bitmap userImage;

    private static LruCache<Integer, Bitmap> cache = new LruCache<Integer, Bitmap>(PeckApp.Constants.Graphics.CACHE_SIZE);
    private static Vector<Integer> noImageAvailable = new Vector<Integer>(PeckApp.Constants.Graphics.INT_CACHE_SIZE);

    private ImageCacher() { }

    static {
        imageNotAvailable = BitmapFactory.decodeResource(PeckApp.AppContext.getContext().getResources(),
                PeckApp.Constants.Graphics.FILLER);
    }

    public static void init(String defaultImageName, int userId) {
        if (defaultImageName != null)
            if (new File(defaultImageName).exists()) {
                Log.i(TAG, "loading from file saved to disk");
                FileInputStream in = null;
                try {
                    in = new FileInputStream(defaultImageName);
                    userImage = BitmapFactory.decodeFile(defaultImageName);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    userImage = get(userId);
                } finally {
                    try {
                        in.close();
                    } catch (Throwable ignore) {
                    }
                }
            } else {
                Log.i(TAG, "loading default");
                userImage = get(userId);
            }

    }

    public static ImageCacher getCacher() { return cacher; }

    public static Bitmap get(int userId) {
        if (userId == PeckSessionManager.getUserId()) return userImage;
        if (noImageAvailable.contains(userId)) return imageNotAvailable;

        Bitmap ret = cache.get(userId);
        if (ret == null) {
            try {
                ret = PeckSessionManager.getImage(userId);
                cache.put(userId, ret);
            } catch (PeckSessionManager.NotAvailableException e) {
                Log.i(getCacher().getClass().getName(), "Image for user " + userId + " was unreachable.");
                noImageAvailable.add(userId);
                ret = imageNotAvailable;
            }
        }
        return ret;
    }

    public static Bitmap forceUpdate(int resId) {
        noImageAvailable.remove(Integer.valueOf(resId));
        return get(resId);
    }

    public static boolean isAvailable(int userId) {
        //returns false if we know we can't get the user's image, true otherwise
        return !noImageAvailable.contains(userId);

    }



}
