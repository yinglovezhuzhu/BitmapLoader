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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.ImageView;

/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 * 
 * @author yinglovezhuzhu@gmail.com
 * @version 1.0
 * 
 */
public class ImageFetcher extends ImageResizer {
	
    public static final String HTTP_CACHE_DIR = "http";
    private static final String TAG = "ImageFetcher";
    private static final int DEFAULT_BUFF_SIZE = 1024 * 8; //8KB
    private static final int HTTP_CACHE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int DEFAULT_HTTP_CACHE_ITEM_SIZE = 128;
    
    private static final Map<String, Integer> mAssetsRecords = new HashMap<String, Integer>();
    
    private Context mContext;

    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageFetcher(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init(context);
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    public ImageFetcher(Context context, int imageSize) {
        super(context, imageSize);
        init(context);
    }
    
    /**
     * Load image from assets
     * @param name
     * @param imageView
     */
    public void loadImageFromAssets(String name, ImageView imageView) {
    	loadImageFromAssets(name, imageView, mDefaultBitmapConfig, null);
    }
    
    /**
     * Load Image from assets
     * @param name
     * @param imageView
     * @param l
     */
    public void loadImageFromAssets(String name, ImageView imageView, LoadListener l) {
    	loadImageFromAssets(name, imageView, mDefaultBitmapConfig, l);
    }
    
    /**
     * Load image from assets
     * @param name
     * @param imageView
     * @param config
     * @param l
     */
    public void loadImageFromAssets(String name, ImageView imageView, Config config, LoadListener l) {
    	mAssetsRecords.put(name, getRecourdeCount(name) + 1);
    	loadImage(name, imageView, config, l);
    }
    

    /**
     * Download a bitmap from a URL, write it to a disk and return the File pointer. This
     * implementation uses a simple disk cache.
     *
     * @param context   The context to use
     * @param urlString The URL to fetch
     * @return A File pointing to the fetched bitmap
     */
    public File downloadBitmap(Context context, String urlString, LoadListener l) {

        final File cacheDir = DiskLruCache.getDiskCacheDir(context, mImageCache == null ?
                null : mImageCache.getImageCacheParams().cachePath, HTTP_CACHE_DIR);

        final DiskLruCache cache = DiskLruCache.openCache(context, cacheDir,
                null == mImageCache ? HTTP_CACHE_SIZE : mImageCache.getImageCacheParams().httpCacheSize);

        cache.setMaxCacheItemSize(null == mImageCache ? DEFAULT_HTTP_CACHE_ITEM_SIZE : mImageCache.getImageCacheParams().httpCacheItemSize);

        final String cacheFilename = cache.createFilePath(urlString);

        if(null == cacheFilename) {
            Log.e(TAG, "downloadBitmap - create cache file path failed");
            return null;
        }

        final File cacheFile = new File(cacheFilename);

        if (cache.containsKey(urlString)) {
            if (ImageWorker.DEBUG) {
                Log.d(TAG, "downloadBitmap - found in http cache - " + urlString);
            }
            return cacheFile;
        }

        if (ImageWorker.DEBUG) {
            Log.d(TAG, "downloadBitmap - downloading - " + urlString);
        }

        Utils.disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Referer", urlString);
            // 设置用户代理
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; "
                    + "MSIE 8.0; Windows NT 5.2;"
                    + " Trident/4.0; .NET CLR 1.1.4322;"
                    + ".NET CLR 2.0.50727; " + ".NET CLR 3.0.04506.30;"
                    + " .NET CLR 3.0.4506.2152; " + ".NET CLR 3.5.30729)");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final InputStream in =
                        new BufferedInputStream(urlConnection.getInputStream(), Utils.IO_BUFFER_SIZE);
                out = new BufferedOutputStream(new FileOutputStream(cacheFile), Utils.IO_BUFFER_SIZE);

                int b;
                long total = urlConnection.getContentLength();
                if (total < 0) {
                    urlConnection.connect();
                    total = urlConnection.getContentLength();
                }
                long downloaded = 0;
                if (total < 0) {
                    byte[] buff = new byte[DEFAULT_BUFF_SIZE];
                    int count = 0;
                    while ((count = in.read(buff)) != -1) {
                        out.write(buff, 0, count);
                        downloaded += count;
                        if (l != null) {
                            l.onProgressUpdate(urlString, total, downloaded);
                        }
                    }
                } else {
                    long size = total / 100;
                    if (size > 1) {
                        byte[] buff = new byte[(int) size];
                        int count = 0;
                        while ((count = in.read(buff)) != -1) {
                            out.write(buff, 0, count);
                            downloaded += count;
                            if (l != null) {
                                l.onProgressUpdate(urlString, total, downloaded);
                            }
                        }
                    } else {
                        while ((b = in.read()) != -1) {
                            out.write(b);
                            downloaded++;
                            if (l != null) {
                                l.onProgressUpdate(urlString, total, downloaded);
                            }
                        }
                    }
                }
                return cacheFile;
            } else {
                return null;
            }
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
            FileUtil.deleteFile(cacheFile); //delete file if download failed
            if (l != null) {
                l.onError(urlString, e);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error in downloadBitmap - " + e);
                    if (l != null) {
                        l.onError(urlString, e);
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * Copy a bitmap from assets to cache
     * @param context
     * @param name
     * @param l
     * @return
     */
    public File copyAssetsBitmap(Context context, String name, LoadListener l) {
    	final File cacheDir = DiskLruCache.getDiskCacheDir(context, mImageCache == null ?
                null : mImageCache.getImageCacheParams().cachePath, HTTP_CACHE_DIR);

        final DiskLruCache cache = DiskLruCache.openCache(context, cacheDir,
                null == mImageCache ? HTTP_CACHE_SIZE : mImageCache.getImageCacheParams().httpCacheSize);

        cache.setMaxCacheItemSize(null == mImageCache ? DEFAULT_HTTP_CACHE_ITEM_SIZE : mImageCache.getImageCacheParams().httpCacheItemSize);

        final String cacheFilename = cache.createFilePath(name);

        if(null == cacheFilename) {
            Log.e(TAG, "copyAssetsBitmap - create cache file path failed");
            return null;
        }

        final File cacheFile = new File(cacheFilename);

        if (cache.containsKey(name)) {
            if (ImageWorker.DEBUG) {
                Log.d(TAG, "copyAssetsBitmap - found in http cache - " + name);
            }
            return cacheFile;
        }

        if (ImageWorker.DEBUG) {
            Log.d(TAG, "copyAssetsBitmap - copying - " + name);
        }
        
        InputStream inStream = null;
    	FileOutputStream outStream = null;
        try {
        	inStream = context.getResources().getAssets().open(name);
        	int total = inStream.available();
        	outStream = new FileOutputStream(cacheFile);
            byte [] buffer = new byte[DEFAULT_BUFF_SIZE];
            int size;
            int downloadedSize = 0;
            while((size = inStream.read(buffer)) != -1) {
            	outStream.write(buffer, 0, size);
            	downloadedSize += size;
            	if (l != null) {
                    l.onProgressUpdate(name, total, downloadedSize);
                }
            }
            outStream.flush();
            return cacheFile;
        } catch (IOException e) {
            e.printStackTrace();
            if (l != null) {
                l.onError(name, e);
            }
        } finally {
        	if(null != outStream) {
        		try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					if (l != null) {
						l.onError(name, e);
					}
				}
        	}
        	if(null != inStream) {
        		try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					if (l != null) {
						l.onError(name, e);
					}
				}
        	}
        }
        return null;
    }
    
    private void init(Context context) {
        mContext = context;
    }

    /**
     * 获取从Assets中加载的某个图片记录数量
     * @param name
     * @return
     */
    private int getRecourdeCount(String name) {
    	Integer count = mAssetsRecords.get(name);
    	return null == count ? 0 : count.intValue();
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private Bitmap processBitmap(String data, Bitmap.Config config, LoadListener l) {
        if (ImageWorker.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }

        if (Utils.isNetworkConnected(mContext)) {
            // Download a bitmap, write it to a file
            final File f = downloadBitmap(mContext, data, l);

            if (f != null) {
                // Return a sampled down version
                return decodeSampledBitmapFromFile(f.toString(), mImageWidth, mImageHeight, config);
            }
        } else {
            if (l != null) {
                l.onError(data, "Network connection not found");
            }
        }
        return null;
    }
    
    /**
     * Process bitmap from assets file
     * @param data
     * @param config
     * @param l
     * @return
     */
    private Bitmap processBitmapFromAssets(String data, Bitmap.Config config, LoadListener l) {
    	if (ImageWorker.DEBUG) {
    		Log.d(TAG, "processBitmap - " + data);
    	}
    	
    	try {
    		final File f = copyAssetsBitmap(mContext, data, l);
    		
    		if (f != null) {
    			// Return a sampled down version
    			return decodeSampledBitmapFromFile(f.toString(), mImageWidth, mImageHeight, config);
    		}
    	} catch (Exception e) {
    		Log.e(TAG, "Load bitmap from assets failed--" + data);
    	}
    	return null;
    }

    @Override
    protected Bitmap processBitmap(Object data, Bitmap.Config config, LoadListener l) {
    	String dataString = String.valueOf(data);
    	int assetsCount = getRecourdeCount(dataString);
    	if(assetsCount > 0) {
    		if(assetsCount > 1) {
    			mAssetsRecords.put(dataString, assetsCount - 1);
    		} else {
    			mAssetsRecords.remove(dataString);
    		}
    		return processBitmapFromAssets(dataString, config, l);
    	}
        return processBitmap(dataString, config, l);
    }
}
