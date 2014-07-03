package com.peck.android.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.peck.android.PeckApp;
import com.peck.android.interfaces.Callback;
import com.peck.android.interfaces.HasImage;
import com.peck.android.interfaces.Singleton;
import com.peck.android.models.DBOperable;
import com.peck.android.models.User;
import com.peck.android.network.ServerCommunicator;

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
    private static final Bitmap imageNotAvailable;
    private static final String TAG = "ImageCacher";
    private static final String CACHE_NAME = "img_cache";
    private static final File CACHE_DIR = new File(PeckApp.getContext().getCacheDir(), CACHE_NAME);
    private static Bitmap userImage;

    private static LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(PeckApp.Constants.Graphics.CACHE_SIZE) {

        @Override
        protected int sizeOf(String key, Bitmap value) {
            if (!value.equals(imageNotAvailable)) return value.getByteCount();
            else return 0;
        }

    };

    private ImageCacher() { }

    static {
        imageNotAvailable = BitmapFactory.decodeResource(PeckApp.getContext().getResources(),
                PeckApp.Constants.Graphics.FILLER);
        CACHE_DIR.mkdir();
    }

    public static void init(final User user) {
        userImage = imageNotAvailable;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                readCacheFromDisk();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (cache.get(toToken(user)) != null) {
                    userImage = cache.remove(toToken(user));
                }
            }
        }.execute();
    }

    public static <T extends DBOperable & HasImage> void get(final T item, final Callback<Bitmap> callback) {
        if (toToken(item).equals(toToken(PeckSessionHandler.getUser())) && userImage != null) callback.callBack(userImage);
        else {
            Bitmap ret = cache.get(toToken(item));

            if (ret == null) {
                cache.put(toToken(item), imageNotAvailable);
                ServerCommunicator.getImage(item.getImageUrl(), new Callback<Bitmap>() {
                    @Override
                    public void callBack(Bitmap obj) {
                        if (obj != null) {
                            cache.put(toToken(item), obj);
                        }
                        callback.callBack(cache.get(toToken(item)));
                    }
                });
            } else {
                callback.callBack(ret);
            }
        }
    }

    public static <T extends DBOperable & HasImage> void forceUpdate(T t, Callback<Bitmap> callback) {
        cache.remove(toToken(t));
        get(t, callback);
    }

    protected static void writeCacheToDisk() {
        FileOutputStream out = null;
        Map<String, Bitmap> snapshot = cache.snapshot();
        for (String i : snapshot.keySet()) {
            try {
                out = new FileOutputStream(new File(CACHE_DIR, i));
                snapshot.get(i).compress(Bitmap.CompressFormat.PNG, PeckApp.Constants.Graphics.PNG_COMPRESSION, out);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "couldn't write cached image for user " + i + " to disk\n" + e.toString());
            } finally {
                try {
                    out.close();
                } catch (Exception e) {}
            }
        }

        if (PeckSessionHandler.getUser() != null && PeckSessionHandler.getUser().getServerId() >= 0) {
            try {
                out = new FileOutputStream(new File(CACHE_DIR, Integer.toString(PeckSessionHandler.getUser().getServerId())));
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
            cache.put((file.getName()), BitmapFactory.decodeFile(file.getPath()));
        }
    }

    private static <T extends DBOperable & HasImage> String toToken(T t) {
        return t.getClass().getSimpleName() + "[" + t.getLocalId() + "]";
    }


}
