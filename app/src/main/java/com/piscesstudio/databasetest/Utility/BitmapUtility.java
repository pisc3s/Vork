package com.piscesstudio.databasetest.Utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BitmapUtility {

    private static void loadImage(Resources resources, int resId, ImageView imageView, int viewDimension) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapTask task = new BitmapTask(imageView, resources, null);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(resources, null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId, viewDimension, viewDimension);
        }
    }

    public static void loadImage(Resources resources, byte[] data, ImageView imageView, int viewDimension){
        if (cancelPotentialWork(data, imageView)) {
            final BitmapTask task = new BitmapTask(imageView, resources, data);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(resources, null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(0, viewDimension, viewDimension);
        }
    }

    private static boolean cancelPotentialWork(int resId, ImageView imageView) {
        final BitmapTask bitmapTask = getBitmapTask(imageView);

        if (bitmapTask != null) {
            final int bitmapData = bitmapTask.resId;
            if (bitmapData == 0 || bitmapData != resId) {
                bitmapTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean cancelPotentialWork(byte[] data, ImageView imageView) {
        final BitmapTask bitmapTask = getBitmapTask(imageView);

        if (bitmapTask != null) {
            final byte[] bitmapData = bitmapTask.data;
            if (bitmapData == null || bitmapData != data) {
                bitmapTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static Bitmap decodeSampledBitmap(Resources res, int resId, int reqSize) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static Bitmap decodeSampledBitmap(byte[] data, int reqSize) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqSize) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqSize || width > reqSize) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqSize
                    && (halfWidth / inSampleSize) > reqSize) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static BitmapTask getBitmapTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapTask();
            }
        }
        return null;
    }

    static class BitmapTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int resId = 0, reqSize;
        private byte[] data;
        private Resources resources;

        public BitmapTask(ImageView imageView, Resources res, byte[] data) {
            imageViewReference = new WeakReference<>(imageView);
            resources = res;
            this.data = data;
        }

        @Override
        protected Bitmap doInBackground(Integer... objects) {
            resId = objects[0];
            reqSize = objects[1];

            if(resId == 0 && data != null) {
                return decodeSampledBitmap(data, reqSize);
            } else {
                return decodeSampledBitmap(resources, resId, reqSize);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapTask bitmapTask = getBitmapTask(imageView);
                if (this == bitmapTask && imageView != null) {
//                    CircularImage roundedImage = new CircularImage(bitmap);
//                    imageView.setImageDrawable(roundedImage);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapTask bitmapTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapTask);
        }

        public BitmapTask getBitmapTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

}
