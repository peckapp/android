package com.peck.android.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.Singleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Created by mammothbane on 6/20/2014.
 *
 * custom cache for all images in the app
 *
 */
public class ImageCacher implements Singleton {
    private static ImageCacher cacher = new ImageCacher();

    private static final Bitmap imageNotAvailable;
    private static final String TAG = "ImageCacher";
    private static final String CACHE_NAME = "img_cache";
    private static final File CACHE_DIR = new File(PeckApp.getContext().getCacheDir(), CACHE_NAME);
    private static Bitmap userImage;

    private static LruCache<Integer, Bitmap> cache = new LruCache<Integer, Bitmap>(PeckApp.Constants.Graphics.CACHE_SIZE) {

        @Override
        protected int sizeOf(Integer key, Bitmap value) {
            if (!value.equals(imageNotAvailable)) return value.getByteCount();
            else return 0;
        }

    };

    private ImageCacher() { }

    static {
        imageNotAvailable = BitmapFactory.decodeResource(PeckApp.getContext().getResources(),
                PeckApp.Constants.Graphics.FILLER);
        CACHE_DIR.mkdir();
        cache.put(0, imageNotAvailable);
    }

    public static void init(final int serverId) {
        userImage = imageNotAvailable;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                readCacheFromDisk();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (cache.get(serverId) != null) {
                    userImage = cache.remove(serverId);
                }
            }
        }.execute();
    }

    public static ImageCacher getCacher() { return cacher; }

    public static void get(final int userId, final Callback<Bitmap> callback) {
        if (userId == PeckSessionManager.getUser().getServerId() && userImage != null) callback.callBack(userImage);
        else {
            Bitmap ret = cache.get(userId);

            if (ret == null) {
                cache.put(userId, imageNotAvailable);
                PeckSessionManager.getImage(userId, new Callback<Bitmap>() {
                    @Override
                    public void callBack(Bitmap obj) {
                        if (obj != null) { cache.put(userId, obj); }
                        callback.callBack(cache.get(userId));
                    }
                });
            } else {
                callback.callBack(ret);
            }
        }
    }

    public static void forceUpdate(int resId, Callback<Bitmap> callback) {
        cache.remove(resId);
        get(resId, callback);
    }

    protected static void writeCacheToDisk() {
        FileOutputStream out = null;
        Map<Integer, Bitmap> snapshot = cache.snapshot();
        for (Integer i : snapshot.keySet()) {
            try {
                out = new FileOutputStream(new File(CACHE_DIR, i.toString()));
                snapshot.get(i).compress(Bitmap.CompressFormat.PNG, PeckApp.Constants.Graphics.PNG_COMPRESSION, out);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "couldn't write cached image for user " + i + " to disk\n" + e.toString());
            } finally {
                try {
                    out.close();
                } catch (Exception e) {}
            }
        }

        if (PeckSessionManager.getUser() != null && PeckSessionManager.getUser().getServerId() >= 0) {
            try {
                out = new FileOutputStream(new File(CACHE_DIR, Integer.toString(PeckSessionManager.getUser().getServerId())));
                userImage.compress(Bitmap.CompressFormat.PNG, PeckApp.Constants.Graphics.PNG_COMPRESSION, out);
            } catch (Exception e) {
                Log.e(TAG, "couldn't write app user's image to disk\n" + e.toString());
            } finally {
                try {
                    out.close();
                } catch (Exception e) {}
            }
        }

    }

    private static void readCacheFromDisk() {
        for (File file : CACHE_DIR.listFiles()) {
            cache.put(Integer.parseInt(file.getName()), BitmapFactory.decodeFile(file.getPath()));
        }
    }


}
