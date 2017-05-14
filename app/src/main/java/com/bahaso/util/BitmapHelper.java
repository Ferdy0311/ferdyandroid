package com.bahaso.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.io.InputStream;


public class BitmapHelper {
    /**
     * Rotate an image if required.
     * Solution By Felix
     * http://stackoverflow.com/questions/14066038/why-image-captured-using-camera-intent-gets-rotated-on-some-devices-in-android
     * @param img
     * @return
     */
    private static Bitmap rotateImageIfRequired(Context context,Bitmap img) {

        // Detect rotation
        int rotation=getRotation(context);
        if(rotation!=0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }else{
            return img;
        }
    }

    /**
     * Get the rotation of the last image added.
     * @param context
     * @return
     */
    private static int getRotation(Context context) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();
        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            if(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
            }
            mediaCursor.close();
        }
        return rotation;
    }

    private static Bitmap cropAndRotateImageIfRequired(Context context, Bitmap originalBitmap,
                                                       int width, int height, Uri imageUri) {
        // Detect rotation
        int rotation = 0;
        if (null!= imageUri) {
            try {
                ExifInterface ei = new ExifInterface(getPath(context, imageUri));
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    // etc.
                }
            }catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

        // crop to thumbnail
        Bitmap cropBitmap = ThumbnailUtils.extractThumbnail(originalBitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        // recycle original bitmap after rotation detection
        originalBitmap.recycle();

        if(rotation!=0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImage = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(),
                    cropBitmap.getHeight(), matrix, true);
            cropBitmap.recycle();
            return rotatedImage;
        }
        return cropBitmap;

//        int rotation=getRotation(context);
//        if(rotation!=0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotation);
//            Bitmap rotatedImage = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(),
//                    cropBitmap.getHeight(), matrix, true);
//            cropBitmap.recycle();
//            return rotatedImage;
//        }
//        return cropBitmap;
    }

    private static Bitmap cropAndRotateImage(Context context, Bitmap originalBitmap,
                                             int width, int height) {
        // crop to thumbnail
        Bitmap cropBitmap = ThumbnailUtils.extractThumbnail(originalBitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        originalBitmap.recycle();

        // Detect rotation
        int rotation=getRotation(context);
        if(rotation!=0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImage = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.getWidth(),
                    cropBitmap.getHeight(), matrix, true);
            cropBitmap.recycle();
            return rotatedImage;
        }
        return cropBitmap;
    }

    public static final int MAX_HEIGHT = 1024;
    public static final int MAX_WIDTH = 1024;

    public static Bitmap editBitmapToThumbnail(Context context, Bitmap bitmap)
            throws IOException {
        Bitmap scaledBitmap = scaleBitmapToMaxWidth(bitmap);
        int size = Math.min( MAX_WIDTH,
                Math.min(scaledBitmap.getWidth(), scaledBitmap.getHeight()));
        return cropAndRotateImage(context, scaledBitmap, size, size);
    }

    public static Bitmap scaleBitmapToMaxWidth (Bitmap originalBitmap){
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int minWidthOrHeight = Math.min(width, height);
        if (minWidthOrHeight <= MAX_WIDTH * 1.5) {
            return originalBitmap;
        }
        else {
            // need to scale
            if (width < height) {
                //scale based on width, scale to MAX_WIDTH
                Bitmap scaledBitmap =
                        Bitmap.createScaledBitmap(originalBitmap, MAX_WIDTH,
                                height * MAX_WIDTH / width, false);
                originalBitmap.recycle();
                return scaledBitmap;
            }
            else {
                //scale based on width, scale to MAX_HEIGHT
                Bitmap scaledBitmap =
                        Bitmap.createScaledBitmap(originalBitmap, width * MAX_HEIGHT / height,
                                MAX_HEIGHT, false);
                originalBitmap.recycle();
                return scaledBitmap;
            }
        }
    }

    public static Bitmap editBitmapToThumbnail(Context context, Uri imageUri)
            throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        Bitmap scaledBitmap = scaleBitmapToMaxWidth(bitmap);
        int size = Math.min( MAX_WIDTH,
                Math.min(scaledBitmap.getWidth(), scaledBitmap.getHeight()));
        return cropAndRotateImageIfRequired(context, scaledBitmap, size, size, imageUri);
    }

    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img);
        return img;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static Bitmap cropCenter(Bitmap source) {
        Bitmap output;
        if (source.getWidth() >= source.getHeight()) {
            output = Bitmap.createBitmap(
                    source,
                    source.getWidth() / 2 - source.getHeight() / 2,
                    0,
                    source.getHeight(),
                    source.getHeight()
            );
        } else {
            output = Bitmap.createBitmap(
                    source,
                    0,
                    source.getHeight() / 2 - source.getWidth() / 2,
                    source.getWidth(),
                    source.getWidth()
            );
        }
        return output;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return retVal;
    }

//    private static String getFilePath(Context context, Uri input) {
//        String filePath = "";
//
//        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//        Cursor cursor = context.getContentResolver().query(
//                input, filePathColumn, null, null, null);
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        filePath = cursor.getString(columnIndex);
//        cursor.close();
//
//        return filePath;
//    }
//
//    private static String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    // http://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework/20559175#20559175

    @TargetApi(19)
    private static boolean isDocumentURI (Context context, Uri uri){
        return DocumentsContract.isDocumentUri(context, uri);
    }

    @TargetApi(19)
    private static String getDocumentID (Uri uri){
        return DocumentsContract.getDocumentId(uri);
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && isDocumentURI(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = getDocumentID(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = getDocumentID(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = getDocumentID(uri);
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
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
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
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
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
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
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
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    // http://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
