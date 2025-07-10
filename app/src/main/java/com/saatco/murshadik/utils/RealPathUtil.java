package com.saatco.murshadik.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Devlomi on 04/02/2018.
 */

//this class will get the real path from a Uri
//it will return the file path from device like this:
// /sdcard/folder/file.extension
//since the uri will not give us the file path
//credits goes to @tatocaster https://gist.github.com/tatocaster/32aad15f6e0c50311626
public class RealPathUtil {

    public static String getRealPath(Context context, Uri fileUri) {
        try {

            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11) {
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, fileUri);
            }
            // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19) {
                realPath = RealPathUtil.getRealPathFromURI_API11to18(context, fileUri);
            }
            // SDK > 19 (Android 4.4) and up
            else {
                realPath = RealPathUtil.getRealPathFromURI_API19(context, fileUri);
            }
            return realPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                // final String id = DocumentsContract.getDocumentId(uri);
               /* final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));*/

                Uri contentUri = null;

                final String id = DocumentsContract.getDocumentId(uri);

                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String[] getAudioPath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri
                , new String[]{MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.DURATION}, null, null, null);
        String[] audioArray = new String[2];
        try {

            cursor.moveToFirst();
            String path = cursor.getString(0);
            int durationInMs = cursor.getInt(1);
            String duration = Util.milliSecondsToTimer(durationInMs);
            audioArray[0] = path;
            audioArray[1] = duration;


        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return audioArray;
    }

    public interface JSONCallbackInterface {
        void onJSONReady(JSONObject jsonObject);
    }

    public static void readJsonFromUrl(String url, JSONCallbackInterface jsonCallbackInterface) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            String jsonText = null;
            try (InputStream is = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                jsonText = readAll(rd);

            } catch (IOException e) {
                e.printStackTrace();
            }
            String finalJsonText = jsonText;
            handler.post(() -> {
                //UI Thread work here
                try {
                    if (finalJsonText != null)
                        jsonCallbackInterface.onJSONReady(new JSONObject(finalJsonText));
                    else jsonCallbackInterface.onJSONReady(null);

                } catch (JSONException e) {
                    jsonCallbackInterface.onJSONReady(null);
                }
            });
        });

//        new AsyncTask<String, Void, JSONObject>() {
//            /**
//             * Override this method to perform a computation on a background thread. The
//             * specified parameters are the parameters passed to {@link #execute}
//             * by the caller of this task.
//             * <p>
//             * This will normally run on a background thread. But to better
//             * support testing frameworks, it is recommended that this also tolerates
//             * direct execution on the foreground thread, as part of the {@link #execute} call.
//             * <p>
//             * This method can call {@link #publishProgress} to publish updates
//             * on the UI thread.
//             *
//             * @param strings The parameters of the task.
//             * @return A result, defined by the subclass of this task.
//             * @see #onPreExecute()
//             * @see #onPostExecute
//             * @see #publishProgress
//             */
//            @Override
//            protected JSONObject doInBackground(String... strings) {
//                try (InputStream is = new URL(url).openStream()) {
//                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//                    String jsonText = readAll(rd);
//                    return new JSONObject(jsonText);
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            /**
//             * <p>Runs on the UI thread after {@link #doInBackground}. The
//             * specified result is the value returned by {@link #doInBackground}.
//             * To better support testing frameworks, it is recommended that this be
//             * written to tolerate direct execution as part of the execute() call.
//             * The default version does nothing.</p>
//             *
//             * <p>This method won't be invoked if the task was cancelled.</p>
//             *
//             * @param jsonObject The result of the operation computed by {@link #doInBackground}.
//             * @see #onPreExecute
//             * @see #doInBackground
//             */
//            @Override
//            protected void onPostExecute(JSONObject jsonObject) {
//                super.onPostExecute(jsonObject);
//                jsonCallbackInterface.onJSONReady(jsonObject);
//
//            }
//        }.execute(url);

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}


