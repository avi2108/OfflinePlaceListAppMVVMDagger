package com.sample.groafersmocky.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.sample.groafersmocky.BuildConfig;
import com.sample.groafersmocky.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class CachedImageViewLoader {


    private static final String TAG = CachedImageViewLoader.class.getSimpleName();
    private static LruCache<String, Bitmap> mMemoryCache;
    private static DiskLruCache diskLruCache;
    private final static Object diskCacheLock = new Object();
    private static boolean diskCacheStarting = true;
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private int imageWidth, imageHeight;
    private Context context;


    static {
        // Initialize disk cache in background on  first time class loaded
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (diskCacheLock) {
                    try {
                        final String cachePath =
                                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                                        !Environment.isExternalStorageRemovable() ? MyApplication.getContext().getExternalCacheDir().getPath() :
                                        MyApplication.getContext().getCacheDir().getPath();

                        File cacheDir = new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
                        diskLruCache = DiskLruCache.openCache(MyApplication.getContext(), cacheDir, DISK_CACHE_SIZE);
                        mMemoryCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
                            /**
                             * Measure item size in bytes rather than units which is more practical for a bitmap
                             * cache
                             */
                            @Override
                            protected int sizeOf(String key, Bitmap bitmap) {
                                return bitmap.getByteCount(); //todo may add /1024
                            }
                        };
                        diskCacheStarting = false; // Finished initialization
                        diskCacheLock.notifyAll(); // Wake any waiting threads
                    } catch (Exception e) {
                    }
                }
            }
        }).start();

    }



    private CachedImageViewLoader() { };

    public CachedImageViewLoader(Context context) {
        this.context = context;
    }





    public static CachedImageViewLoader with(Context context) {

        return new CachedImageViewLoader(context);
    }

    /**
     * load image bitmap of imageUrl from cache if not then initiate an BackgroundTask to fetch from imageUrl
     * @param imageUrl
     * @param imageView
     */
    public void loadImage(String imageUrl, ImageView imageView) {


        imageWidth = imageView.getWidth();
        imageHeight = imageView.getHeight();
        new Thread(new Runnable() {
            @Override
            public void run() {
                  //check from LRU initially for the bitmap
               final Bitmap bitmap = getBitmapFromMemCache(String.valueOf(imageUrl));
                if (bitmap != null)
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                else if (cancelPotentialWork(imageUrl, imageView)) { //if lRU didnt have the bitmap then initiate asynctask
                    final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    final AsyncDrawable asyncDrawable =
                            new AsyncDrawable(context.getResources(), getLoadingBitmap(), task);
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView. setImageDrawable(asyncDrawable);
                            task.execute(imageUrl);
                        }
                    });
                }

            }
        }).start();
    }


    /**
     * placeholder color tiles bitmap
     * @return
     */
    private Bitmap getLoadingBitmap() {

        Random rnd = new Random();
        int r = rnd.nextInt(128) + 128; // 128 ... 255
        int g = rnd.nextInt(128) + 128; // 128 ... 255
        int b = rnd.nextInt(128) + 128; // 128 ... 255
        Bitmap bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.rgb(r, g, b));
        return bitmap;
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);

            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {

        // Add to memory cache
        if (mMemoryCache != null && mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
        // Also add to disk cache
//        synchronized (diskCacheLock) {
        try {
            if (diskLruCache != null && diskLruCache.get(key) == null) {
                diskLruCache.put(key, bitmap);
            }
        } catch (Exception e) {
        }

//        }
    }

    private Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Memory cache hit");
                }
                return memBitmap;
            }
        }
        return null;
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        try {
            if (diskLruCache != null) {
                return diskLruCache.get(key);
            }
        } catch (Exception e) {
        }

//        }
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     * Fetches bitmap if its in DiskCache else initiates image download task
     */
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String data;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            //check If the image cache is available in Disk
            if (getAttachedImageView() != null)
                bitmap = getBitmapFromDiskCache(dataString);

            // If the bitmap was not found in the cache then initiate download task
            if (bitmap == null && getAttachedImageView() != null) {
                bitmap = processBitmap(data);
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use.
            if (bitmap != null) {
                addBitmapToCache(dataString, bitmap);
            }

            return bitmap;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final ImageView imageView = getAttachedImageView();
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }


        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * download bitmap from url
     *
     * @param data url
     * @return
     */
    private Bitmap processBitmap(String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }

        // Download a bitmap, write it to a file
        final File f = downloadBitmap(context, data);

        if (f != null) {
            // Return a sampled down version
            return decodeSampledBitmapFromFile(f.toString(), imageWidth, imageHeight);
        }

        return null;
    }


    public static File downloadBitmap(Context context, String urlString) {

        final File cacheFile = new File(diskLruCache.createFilePath(urlString));

        if (diskLruCache.containsKey(urlString)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "downloadBitmap - found in http cache - " + urlString);
            }
            return cacheFile;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "downloadBitmap - downloading - " + urlString);
        }

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            final InputStream in =
                    new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(cacheFile), IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }

            return cacheFile;

        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error in downloadBitmap - " + e);
                }
            }
        }

        return null;
    }

    /**
     * returns decoded bitmap with imageview's height and width to save memory
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public synchronized static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }


            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

}
