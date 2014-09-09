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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A simple disk LRU bitmap cache to illustrate how a disk cache would be used for bitmap caching. A
 * much more robust and efficient disk LRU cache solution can be found in the ICS source code
 * (libcore/luni/src/main/java/libcore/io/DiskLruCache.java) and is preferable to this simple
 * implementation.
 */
public class DiskLruCache {
    private static final String TAG = "DiskLruCache";
    private static final String CACHE_FILENAME_PREFIX = "Cache_";
    /**
     * A filename filter to use to identify the cache filenames which have CACHE_FILENAME_PREFIX
     * prepended.
     */
    private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };
    private static final int MAX_REMOVALS = 4;
    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;
    private final Map<String, String> mLinkedHashMap =
            Collections.synchronizedMap(new LinkedHashMap<String, String>(
                    INITIAL_CAPACITY, LOAD_FACTOR, true));
    private final File mCacheDir;
    private int maxCacheItemSize = 128; // 128 item default
    private int cacheSize = 0;
    private int cacheByteSize = 0;
    private long maxCacheByteSize = 1024 * 1024 * 5; // 5MB default
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;

    /**
     * Constructor that should not be called directly, instead use
     * {@link DiskLruCache#openCache(android.content.Context, java.io.File, long)} which runs some extra checks before
     * creating a DiskLruCache instance.
     *
     * @param cacheDir
     * @param maxByteSize
     */
    private DiskLruCache(File cacheDir, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheByteSize = maxByteSize;
    }

    /**
     * Used to fetch an instance of DiskLruCache.
     *
     * @param context
     * @param cacheDir
     * @param maxByteSize
     * @return
     */
    public static DiskLruCache openCache(Context context, File cacheDir, long maxByteSize) {
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        if (cacheDir.isDirectory() && cacheDir.canWrite()
                && Utils.getUsableSpace(cacheDir) > maxByteSize) {
            return new DiskLruCache(cacheDir, maxByteSize);
        }

        return null;
    }

    /**
     * Removes all disk cache entries from the application cache directory in the uniqueName
     * sub-directory.
     *
     * @param context
     * @param cachePath
     * @param uniqueName
     */
    public static void clearCache(Context context, File cachePath, String uniqueName) {
        File cacheDir = getDiskCacheDir(context, cachePath, uniqueName);
        clearCache(cacheDir);
    }

    /**
     * Removes all disk cache entries from the given directory. This should not be called directly,
     * call {@link DiskLruCache#clearCache(android.content.Context, java.io.File, String)} or {@link DiskLruCache#clearCache()}
     * instead.
     *
     * @param cacheDir The directory to remove the cache files from
     */
    private static void clearCache(File cacheDir) {
        final File[] files = cacheDir.listFiles(cacheFileFilter);
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * Get a usable cache directory.(If the custom directory exist or it can be created, get the custom directory, otherwise, get the system cache directory {@link DiskLruCache#getSystemCacheDir(android.content.Context)})
     *
     * @param context
     * @param cachePath
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, File cachePath, String uniqueName) {

        if (cachePath != null && (cachePath.exists() || cachePath.mkdirs())) {
            return new File(cachePath, uniqueName);
        }
        return new File(getSystemCacheDir(context), uniqueName);
    }

    /**
     * Get System cache directory (external if available, internal otherwise).
     *
     * @param context
     * @return
     */
    private static File getSystemCacheDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        return Utils.hasExternalStorage() || !Utils.isExternalStorageRemovable() ?
                Utils.getExternalCacheDir(context) : context.getCacheDir();
    }

    /**
     * Creates a constant cache file path given a target cache directory and an image key.
     *
     * @param cacheDir
     * @param key
     * @return
     */
    public static String createFilePath(File cacheDir, String key) {
        try {
            // Use URLEncoder to ensure we have a valid filename, a tad hacky but it will do for
            // this example
            String fileName = CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8");
            if(fileName.length() > 255) {
                int lastIndex1 = key.lastIndexOf("/");
                int lastIndex2 = key.lastIndexOf("=");
                int lastIndex = lastIndex1 > lastIndex2 ? lastIndex1 : lastIndex2;
                String urlName = key.substring(lastIndex + 1, key.length());
                if(urlName.length() > 0) {
                    fileName = CACHE_FILENAME_PREFIX + URLEncoder.encode(urlName, "UTF-8");
                    if(fileName.length() > 255) {
                        fileName = CACHE_FILENAME_PREFIX + System.currentTimeMillis();
                    }
                } else {
                    fileName = CACHE_FILENAME_PREFIX + System.currentTimeMillis();
                }
            }
            return cacheDir.getAbsolutePath() + File.separator + fileName;
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "createFilePath - " + e);
        }

        return null;
    }

    /**
     * Add a bitmap to the disk cache.
     *
     * @param key  A unique identifier for the bitmap.
     * @param data The bitmap to store.
     */
    public void put(String key, Bitmap data) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {
                try {
                    final String file = createFilePath(mCacheDir, key);
                    if (writeBitmapToFile(data, file)) {
                        put(key, file);
                        flushCache();
                    }
                } catch (final FileNotFoundException e) {
                    Log.e(TAG, "Error in put: " + e.getMessage());
                } catch (final IOException e) {
                    Log.e(TAG, "Error in put: " + e.getMessage());
                }
            }
        }
    }

    private void put(String key, String file) {
        mLinkedHashMap.put(key, file);
        cacheSize = mLinkedHashMap.size();
        cacheByteSize += new File(file).length();
    }

    /**
     * Flush the cache, removing oldest entries if the total size is over the specified cache size.
     * Note that this isn't keeping track of stale files in the cache directory that aren't in the
     * HashMap. If the images and keys in the disk cache change often then they probably won't ever
     * be removed.
     */
    private void flushCache() {
        Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;

        while (count < MAX_REMOVALS &&
                (cacheSize > maxCacheItemSize || cacheByteSize > maxCacheByteSize)) {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
            if (ImageWorker.DEBUG) {
                Log.d(TAG, "flushCache - Removed cache file, " + eldestFile + ", "
                        + eldestFileSize);
            }
        }
    }

    /**
     * Get an image from the disk cache.
     *
     * @param key The unique identifier for the bitmap
     * @return The bitmap or null if not found
     */
    public Bitmap get(String key, Bitmap.Config config) {
        synchronized (mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (config != null) {
                options.inPreferredConfig = config;
            }
            if (file != null) {
                if (ImageWorker.DEBUG) {
                    Log.d(TAG, "Disk cache hit");
                }
                return BitmapFactory.decodeFile(file, options);
            } else {
                final String existingFile = createFilePath(mCacheDir, key);
                if (new File(existingFile).exists()) {
                    put(key, existingFile);
                    if (ImageWorker.DEBUG) {
                        Log.d(TAG, "Disk cache hit (existing file)");
                    }
                    return BitmapFactory.decodeFile(existingFile, options);
                }
            }
            return null;
        }
    }

    /**
     * Get bitmap cache file on disk.
     *
     * @param key
     * @return
     */
    public File getDiskCacheFile(String key) {
        synchronized (mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            if (file != null) {
                if (ImageWorker.DEBUG) {
                    Log.d(TAG, "Disk cache hit");
                }
                return new File(file);
            } else {
                final String existingFile = createFilePath(mCacheDir, key);
                File cacheFile = new File(existingFile);
                if (cacheFile.exists()) {
                    put(key, existingFile);
                    if (ImageWorker.DEBUG) {
                        Log.d(TAG, "Disk cache hit (existing file)");
                    }
                    return cacheFile;
                }
            }
            return null;
        }
    }

    /**
     * Checks if a specific key exist in the cache.
     *
     * @param key The unique identifier for the bitmap
     * @return true if found, false otherwise
     */
    public boolean containsKey(String key) {
        // See if the key is in our HashMap
        if (mLinkedHashMap.containsKey(key)) {
            return true;
        }

        // Now check if there's an actual file that exists based on the key
        final String existingFile = createFilePath(mCacheDir, key);
        if (new File(existingFile).exists()) {
            // File found, add it to the HashMap for future use
            put(key, existingFile);
            return true;
        }
        return false;
    }

    /**
     * Removes all disk cache entries from this instance cache dir
     */
    public void clearCache() {
        DiskLruCache.clearCache(mCacheDir);
    }

    /**
     * Create a constant cache file path using the current cache directory and an image key.
     *
     * @param key
     * @return
     */
    public String createFilePath(String key) {
        return createFilePath(mCacheDir, key);
    }

    /**
     * Sets the target compression format and quality for images written to the disk cache.
     *
     * @param compressFormat
     * @param quality
     */
    public void setCompressParams(CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }

    /**
     * Sets the max cache item size.
     * @param size
     */
    public void setMaxCacheItemSize(int size) {
        this.maxCacheItemSize = size;
    }

    /**
     * Writes a bitmap to a file. Call {@link DiskLruCache#setCompressParams(android.graphics.Bitmap.CompressFormat, int)}
     * first to set the target bitmap compression and format.
     *
     * @param bitmap
     * @param file
     * @return
     */
    private boolean writeBitmapToFile(Bitmap bitmap, String file)
            throws IOException, FileNotFoundException {

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
