package com.saatco.murshadik.utils;

/**
 * Created by apple on 31/12/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.saatco.murshadik.utils.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Junaid on 28-Dec-17.
 */

public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();


    public static String encodeImage(Bitmap bmp) {
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bYtE);
        //  bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap getCircularBitmapImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        // don't need to source.recycle()
        // https://github.com/bumptech/glide/issues/1079
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    /**
     * Reduce image size by scaling for correctly uploading to the server
     * @param ctx
     * @param file
     * @return
     */
    public static Bitmap getScaledImage(Context ctx, File file) {
        try {


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 4; // subsample the original image, returning a smaller image to save memory.

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            final int REQUIRED_SIZE =  50;

            int scale = 1;
            while(options.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    options.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options newOptions = new BitmapFactory.Options();
            newOptions.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, newOptions);
            inputStream.close();


            return selectedBitmap;
        } catch (Exception e) {
         //   Log.e(ImageUtils.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

    public static File getScaledImage2(Context ctx, File file){
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 10; // subsample the original image, returning a smaller image to save memory.

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            final int REQUIRED_SIZE =  50;

            int scale = 1;
            while(options.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    options.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options newOptions = new BitmapFactory.Options();
            newOptions.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, newOptions);
            inputStream.close();

            // fixed bug with wrong rotation on Samsung devices
            selectedBitmap = rotateImageIfWrongOrientation(file.getPath(), selectedBitmap);

            // here we get a file for this compressed bitmap from cache dir
            file = StorageUtil.bitmapToFile(ctx, selectedBitmap);

            return file;
        } catch (Exception e) {
            Log.e(ImageUtils.class.getSimpleName(), e.getMessage());
            return null;
        }
    }


    private static Bitmap rotateImageIfWrongOrientation(String path, Bitmap bitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei != null ? ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED) : 0;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d(TAG, "ORIENTATION_ROTATE_90");
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }
        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Log.d(TAG, "rotate image for " + angle);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static void getImageFromUrl(Context context, String url, MyCallbackHandler<Drawable> myCallbackHandler){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.isMemoryCacheable();
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(url).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                myCallbackHandler.onResponse(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                myCallbackHandler.onResponse(null);
            }
        });
    }
}
