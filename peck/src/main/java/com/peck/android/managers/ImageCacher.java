package com.peck.android.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.peck.android.PeckApp;
import com.peck.android.interfaces.Singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Calendar;
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

    protected static void writeCacheToDisk() {
        Gson gson = new Gson();
        String ret = gson.toJson(cache, cache.getClass());

        File cacheFil = new File(PeckApp.AppContext.getContext().getCacheDir(), Long.toString(Calendar.getInstance().getTimeInMillis()));
        PrintStream printStream = null;

        try {
            printStream = new PrintStream(cacheFil);
            printStream.println(ret);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't write the image cache to disk");
        } finally {
            try {
            printStream.close(); } catch (Throwable ignore) {}
        }

    }

    private static void readCacheFromDisk() {
        File[] fileList = PeckApp.AppContext.getContext().getCacheDir().listFiles();
        File i = new File("0");
        for ( File file : fileList ) {
            if ( Long.parseLong(file.getName()) > Long.parseLong(i.getName())) i = file;
        }

        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();

        try {
            cache = gson.fromJson(jsonParser.parse(new FileReader(i)), (Class<LruCache<Integer, Bitmap>>) cache.getClass());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Couldn't read cache from disk\n" + e.toString());
        }
    }


}
