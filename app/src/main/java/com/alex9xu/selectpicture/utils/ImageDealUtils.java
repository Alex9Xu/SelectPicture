package com.alex9xu.selectpicture.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Alex on 2016/8/16
 */
public class ImageDealUtils {

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            // Log.e(TAG, "degree = " + degree);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotatingImageView(int degree, Bitmap bitmap) {
        Matrix m = new Matrix();
        m.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options opts, int reqWidth) {
        int width = opts.outWidth;
        int inSampleSize = 1;
        int widthRatio = Math.round((float) width / (float) reqWidth);
        inSampleSize = inSampleSize < widthRatio ? widthRatio : inSampleSize;
        return inSampleSize;
    }

    public static void rotatingImageViewByPath(int degree, String path) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = calculateInSampleSize(opts, ConstValues.UPLOAD_PHOTO_WIDTH);
        opts.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, opts);
        bm = rotatingImageView(degree, bm);

        // create a file to write bitmap data
        File f = new File(path);

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, bos);
        byte[] bitmapdata = bos.toByteArray();

        // write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
