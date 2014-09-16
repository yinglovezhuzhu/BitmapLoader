/*
 * Copyright (C) 2014 The Android Open Source Project.
 *
 *        yinglovezhuzhu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensource.bitmaploader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author xiaoying
 */
public class BitmapUtil {

    private BitmapUtil() {
    }

    /**
     * Change a bitmap to mutable.
     *
     * @param context
     * @param inBitmap The bitmap need to change
     * @return if success, return a mutable bitmap, others return the old bitmap.
     */
    public static Bitmap convertToMutable(Context context, Bitmap inBitmap) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = null;
            if (Utils.hasExternalStorage()) {
                file = new File(Utils.getExternalCacheDir(context), "temp.tmp");
            } else {
                file = new File(Utils.getCacheDir(context), "temp.tmp");
            }

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = inBitmap.getWidth();
            int height = inBitmap.getHeight();
            Config config = inBitmap.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, inBitmap.getRowBytes() * height);
            inBitmap.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            inBitmap.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
//            imgIn = Bitmap.createBitmap(width, height, config);
            inBitmap = Bitmap.createBitmap(width, height, config);
            map.position(0);
            //load it back from temporary
//            imgIn.copyPixelsFromBuffer(map);
            inBitmap.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inBitmap;
    }

    /**
     * Get bitmap size, width and height.
     *
     * @param url
     * @return
     */
    public static Size getBitmapSize(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        return new Size(options.outWidth, options.outHeight);
    }


    /**
     * Get the size in bytes of a bitmap.
     *
     * @param bitmap
     * @return size in bytes
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Make a round corner bitmap
     *
     * @param bitmap Source bitmap.
     * @param ratio  Corner radius ratio of Diameter accounting. when it's value is 2,
     *               the bitmap which returned is a circle.
     * @return the round corner bitmap. null if input bitmap is invalid.
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int ratio) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        int diameter = width > height ? height : width; // Get the diameter(The
        // smallest edge)

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Config.ARGB_8888); // Create an output bitmap.
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final int x = (width - diameter) / 2;
        final int y = (height - diameter) / 2;
        final Rect srcRect = new Rect(x, y, x + diameter, y + diameter); // Center part of the source bitmap.
        final Rect destRect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);

        float r = (float) diameter / ratio;
        canvas.drawRoundRect(new RectF(destRect), r, r, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, destRect, paint);
        return output;
    }

    public static class Size {
        public int width = 0;
        public int height = 0;

        public Size() {
        }

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /**
         * @return the width
         */
        public int getWidth() {
            return width;
        }

        /**
         * @param width the width to set
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        /**
         * @param height the height to set
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Size [width=" + width + ", height=" + height + "]";
        }

    }
}
