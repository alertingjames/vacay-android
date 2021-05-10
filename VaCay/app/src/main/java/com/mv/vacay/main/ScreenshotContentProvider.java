package com.mv.vacay.main;

/**
 * Created by a on 4/16/2017.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.mv.vacay.R;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Peter (Varren) on 01.04.2015.
 */
public class ScreenshotContentProvider extends ContentProvider {
    public static final String MIME_TYPE = "image/png";
    private static String[] mimeTypes = {MIME_TYPE};
    public static final String FILE_NAME = "screenshot.png";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("Content Provider", "query: " + uri.toString());

        if (projection == null || projection.length <= 1)//don't know why twitter sends me just 1 param and cant load img
            projection = new String[]{
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.MIME_TYPE
            };

        MatrixCursor result = new MatrixCursor(projection);

        File tempFile = getFile(getContext());
        Log.e("ScreenshotCP.query", "Open File from: " + tempFile);

        // TODO REED INFO MSG 2
        // "File.setReadable(true, false);" will be useful for Facebook
        // Facebook app will not send "openFile(Uri uri, String mode)" to get img
        // It will just use MediaStore.MediaColumns.DATA param in MatrixCursor
        // And ask filepath directly. I couldn't find better solution(
        tempFile.setReadable(true, false);

        Object[] row = new Object[projection.length];
        for (int i = 0; i < projection.length; i++) {

            if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns.DISPLAY_NAME) == 0) {
                row[i] = getContext().getString(R.string.app_name);
            } else if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns.SIZE) == 0) {
                row[i] = tempFile.length();
            } else if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns.DATA) == 0) {
                row[i] = tempFile;
            } else if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns.MIME_TYPE) == 0) {
                row[i] = MIME_TYPE;
            } else if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns.DATE_ADDED) == 0 ||
                    projection[i].compareToIgnoreCase(MediaStore.MediaColumns.DATE_MODIFIED) == 0 ||
                    projection[i].compareToIgnoreCase("datetaken") == 0) {
                row[i] = System.currentTimeMillis();
            } else if (projection[i].compareToIgnoreCase(MediaStore.MediaColumns._ID) == 0) {
                row[i] = 1;
            } else if (projection[i].compareToIgnoreCase("orientation") == 0) {
                row[i] = "0";
            }
            Log.e("projection:", projection[i] + " " + row[i]);
        }
        result.addRow(row);


        return result;
    }

    @Override
    public String getType(Uri uri) {
        return MIME_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new RuntimeException("Screenshot.insert not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("Screenshot.delete not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Screenshot.update not supported");
    }

    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return mimeTypes;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File tempFile = getFile(getContext());
        return ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    public static File getFile(Context context) {
        return new File(context.getFilesDir(), FILE_NAME);
    }
}

