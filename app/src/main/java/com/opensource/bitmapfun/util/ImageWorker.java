/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensource.bitmapfun.util;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.opensource.bitmapfun.BuildConfig;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an<br>
 * ImageView. It handles things like using a memory and disk cache, running the work in a background<br>
 * thread and setting a placeholder image.<br>
 * 
 */
public abstract class ImageWorker {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;
    private Bitmap mLoadingBitmap;
    private Bitmap mLoadFailedBitmap = null;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    
    private Bitmap.Config mDefaultBitmapConfig = Bitmap.Config.ARGB_8888;

    protected Context mContext;
    
    protected static ImageCache mImageCache = null;
    
    protected ImageWorkerAdapter mImageWorkerAdapter;
    
    protected ImageWorker(Context context) {
        mContext = context;
    }

    /**
     * 
     * Load an image specified by the data parameter into an ImageView (override<br>
     * <p>{@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk<br>
     * cache will be used if an {@link ImageCache} has been set using<br>
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it<br>
     * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the<br>
     * bitmap.<br>
     *
     * @param data The URL of the image to download.
     * @param isNative The data is native or not, true is native, false is Internet.
     * @param imageView The ImageView to bind the downloaded image to.
     * 
     * 
     * @deprecated This method can be ambiguous to {@link #loadImage(Object, ImageView, android.graphics.Bitmap.Config)} when<br>
     * the third parameter is null.Use {@link #loadImage(Object, ImageView, android.graphics.Bitmap.Config, LoadListener)}<br>
     * instead.
     * 
     * @see {@link #loadImage(Object, ImageView, android.graphics.Bitmap.Config, LoadListener)}
     */
    public void loadImage(Object data, ImageView imageView, LoadListener l) {
    	if(l != null) {
    		l.onStart(imageView, data);
    	}
        Bitmap bitmap = null;
        if (mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            // Bitmap found in memory cache
        	if(l != null) {
        		l.onLoaded(imageView, bitmap);
        	}
            imageView.setImageBitmap(bitmap);
            if(l != null) {
            	l.onSet(imageView, bitmap);
            }
        } else if (cancelPotentialWork(data, imageView)) {
        	final BitmapWorkerTask task = new BitmapWorkerTask(imageView, l);
        	final AsyncDrawable asyncDrawable =
        			new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
        	imageView.setImageDrawable(asyncDrawable);
        	task.execute(data);
        }
    }
    
    /**
     * 
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk
     * cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it
     * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the
     * bitmap.
     *
     * @param data The URL of the image to download.
     * @param isNative The data is native or not, true is native, false is Internet.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(Object data, ImageView imageView) {
    	loadImage(data, imageView, mDefaultBitmapConfig, null);
    }

    /**
     * 
     * Load an image specified from a set adapter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk
     * cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it
     * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the
     * bitmap. {@link ImageWorker#setAdapter(ImageWorkerAdapter)} must be called before using this
     * method.
     *
     * @param num The URL index of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(int num, ImageView imageView) {
    	loadImage(num, imageView, null);
    }
    
    /**
     * Load an image specified by the data parameter.
     * A memory and disk cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is not found in the memory or disk cache, it
     * would load from file.
     * @param data
     * @param imageView
     * @param config
     */
    public void loadImage(Object data, ImageView imageView, Bitmap.Config config, LoadListener l) {
    	if(l != null) {
    		l.onStart(imageView, data);
    	}
    	Bitmap bitmap = null;
    	
    	if (mImageCache != null) {
    		bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
    	}
    	
    	if (bitmap != null && !bitmap.isRecycled() && bitmap.getConfig() == config) {
    		// Bitmap found in memory cache
    		if(l != null) {
        		l.onLoaded(imageView, bitmap);
        	}
    		imageView.setImageBitmap(bitmap);
    		if(l != null) {
            	l.onSet(imageView, bitmap);
            }
    	} else if (cancelPotentialWork(data, imageView)) {
    		final BitmapWorkerTask task = new BitmapWorkerTask(imageView, config, l);
    		final AsyncDrawable asyncDrawable =
    				new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
    		imageView.setImageDrawable(asyncDrawable);
    		task.execute(data);
    	}
    }
    
    /**
     * Load an image specified by the data parameter.
     * A memory and disk cache will be used if an {@link ImageCache} has been set using<br>
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is not found in the memory or disk cache, <br>
     * it would load from file.
     * @param data
     * @param imageView
     * @param config
     * @deprecated This method can be ambiguous to {@link #loadImage(Object, ImageView, LoadListener)} when<br>
     * the third parameter is null.Use {@link #loadImage(Object, ImageView, android.graphics.Bitmap.Config, LoadListener)}<br>
     * instead.
     * 
     * @see {@link #loadImage(Object, ImageView, android.graphics.Bitmap.Config, LoadListener)}
     */
    public void loadImage(Object data, ImageView imageView, Bitmap.Config config) {
    	loadImage(data, imageView, config, null);
    }
   
    /**
     * 
     * Load an image specified from a set adapter into an ImageView (override
     * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk
     * cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it
     * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the
     * bitmap. {@link ImageWorker#setAdapter(ImageWorkerAdapter)} must be called before using this
     * method.
     *
     * @param num The URL index of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(int num, ImageView imageView, LoadListener l) {
        if (mImageWorkerAdapter != null) {
        	if(l != null) {
        		l.onStart(imageView, mImageWorkerAdapter.getItem(num));
        	}
            loadImage(mImageWorkerAdapter.getItem(num), imageView, null, l);
        } else {
            throw new NullPointerException("Data not set, must call setAdapter() first.");
        }
    }
    
    /**
     * 
     * Load an image specified by the data parameter.
     * A memory and disk cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is not found in the memory or disk cache, it
     * would load from file.
     *
     * @param data The URL of the image to download.
     * @param config The config of bitmap
     * @param l Loading progress call back,only for download bitmap.
     * 
     * @return the bitmap
     */
    public Bitmap getImageBitmap(Object data, Bitmap.Config config, LoadListener l) {
    	if(l != null) {
    		l.onStart(null, data);
    	}
    	Bitmap bitmap = null;
    	String dataString = String.valueOf(data);
        if (mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(dataString);
            if(bitmap == null) {
            	// Bitmap not found in memory cache
            	bitmap = mImageCache.getBitmapFromDiskCache(dataString, config);
            }
        }

        if (bitmap == null || !bitmap.isRecycled()) {
        	// Bitmap not found in memory cache and disk cache
        	try {
        		bitmap = processBitmap(data, config, l);
        	} catch (OutOfMemoryError error) {
        		error.printStackTrace();
        		if(mImageCache != null) {
        			mImageCache.cleanMemCache();
        		}
        	}
        }
        
        if (bitmap != null && mImageCache != null) {
            mImageCache.addBitmapToCache(dataString, bitmap);
        }
        return bitmap;
    }
    
    /**
     * 
     * Load an image specified by the data parameter.
     * A memory and disk cache will be used if an {@link ImageCache} has been set using
     * {@link ImageWorker#setImageCache(ImageCache)}. If the image is not found in the memory or disk cache, it
     * would load from file.
     *
     * @param data The URL of the image to download.
     * @param config The config of bitmap
     * 
     * @return the bitmap
     */
    public Bitmap getImageBitmap(Object data, Bitmap.Config config) {
    	return getImageBitmap(data, config, null);
    }
    
    /**
     * Create a mutable bitmap.
     * @param data
     * @param width
     * @param height
     * @param config
     * @return
     * @deprecated nerve use this method
     */
    public Bitmap createImage(Object data, int width, int height, Bitmap.Config config) {
    	Bitmap bitmap = null;
    	String dataString = String.valueOf(data);
    	String key = String.valueOf(width) + "x" + String.valueOf(height) + "_" + dataString;
    	 if (mImageCache != null) {
             bitmap = mImageCache.getBitmapFromMemCache(key);
         }
    	 if(bitmap == null || bitmap.isRecycled()) {
    		 // Create a mutable bitmap.
    		 try {
         		bitmap = Bitmap.createBitmap(width, height, config);
         	} catch (OutOfMemoryError error) {
         		error.printStackTrace();
         		if(mImageCache != null) {
         			mImageCache.cleanMemCache();
         		}
         	}
    	 }
    	 if (bitmap != null && mImageCache != null) {
             mImageCache.addBitmapToMenCache(key, bitmap);
         }
    	return bitmap;
    }
    
    /**
     * Get disk cache file.
     * @param data 
     * @return disk cache file. null if cache is disable or disk cache is disable.
     */
    public File getDiskCacheFile(Object data) {
    	if(mImageCache == null) {
    		return null;
    	}
    	String dataString = String.valueOf(data);
    	return mImageCache.getDiskCacheFile(dataString);
    }
    
    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
    }
    
    /**
     * Get placeholder bitmap that shows when the background thread is running.
     * @return
     */
    public Bitmap getLoadingImage() {
    	return mLoadingBitmap;
    }
    
    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public void setLoadFailedImage(Bitmap bitmap) {
    	mLoadFailedBitmap = bitmap;
    }
    
    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public void setLoadFailedImage(int resId) {
    	mLoadFailedBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
    }
    
    /**
     * Get placeholder bitmap that shows when the the background thread is running.
     * @return
     */
    public Bitmap getLoadFailedImage() {
    	return mLoadFailedBitmap;
    }

    /**
     * Set the {@link ImageCache} object to use with this ImageWorker.
     *
     * @param cacheCallback
     */
    public void setImageCache(ImageCache cacheCallback) {
        mImageCache = cacheCallback;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }
    
    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     *
     * @param fadeIn
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *            {@link ImageWorker#loadImage(Object, ImageView)}
     * @param config The config of bitmap.
     * @param listener Loading progress call back,only for download bitmap.
     * 
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data, Bitmap.Config config, LoadListener l);
    
    
    public static void cancelWork(ImageView imageView) {
    	final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
    	if (bitmapWorkerTask != null) {
    		bitmapWorkerTask.cancel(true);
    		if (BuildConfig.DEBUG) {
    			final Object bitmapData = bitmapWorkerTask.mmData;
    			Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
    		}
    	}
    }

    /**
     * 
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
    	final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
    	
    	if (bitmapWorkerTask != null) {
    		final Object bitmapData = bitmapWorkerTask.mmData;
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
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTaskEx<Object, Void, Bitmap> {
        private Object mmData;
        private final WeakReference<ImageView> mmImageViewReference;
        private Bitmap.Config mmConfig = mDefaultBitmapConfig;
        private LoadListener mmListener;

        public BitmapWorkerTask(ImageView imageView, LoadListener l) {
        	imageView.setImageBitmap(mLoadingBitmap);
            mmImageViewReference = new WeakReference<ImageView>(imageView);
            this.mmListener = l;
        }
        
        public BitmapWorkerTask(ImageView imageView, Bitmap.Config config, LoadListener l) {
        	this(imageView, l);
        	this.mmConfig = config;
        }
        
        /**
         * Background processing.
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            mmData = params[0];
            final String dataString = String.valueOf(mmData);
            Bitmap bitmap = null;

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
            	try {
            		bitmap = mImageCache.getBitmapFromDiskCache(dataString, mmConfig);
            	} catch (OutOfMemoryError error) {
            		error.printStackTrace();
            		mImageCache.cleanMemCache();
            		if(mmListener != null) {
            			mmListener.onError(mmData, error);
            		}
//            		bitmap = mImageCache.getBitmapFromDiskCache(dataString, mmConfig);
            	}
            }

            // If the bitmap was not found in the cache and this task has not been cancelled by
            // another thread and the ImageView that was originally bound to this task is still
            // bound back to this task and our "exit early" flag is not set, then call the main
            // process method (as implemented by a subclass)
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
            	try {
            		bitmap = processBitmap(params[0], mmConfig, mmListener);
            	} catch (OutOfMemoryError e) {
            		e.printStackTrace();
            		if(mmListener != null) {
            			mmListener.onError(mmData, e);
            		}
            	}
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache(dataString, bitmap);
            }

            return bitmap;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            final ImageView imageView = getAttachedImageView();
            if(mmListener != null) {
            	mmListener.onLoaded(imageView, bitmap);
            }
            if (bitmap != null && imageView != null) {
                setImageBitmap(imageView, bitmap, mmListener);
            }
        }

    	@Override
    	protected void onCancelled() {
    		if(mmListener != null) {
    			mmListener.onCanceld(mmImageViewReference.get(), mmData);
    		}
    		super.onCancelled();
    	}

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = mmImageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
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

    /**
     * Called when the processing is complete and the final bitmap should be set on the ImageView.
     *
     * @param imageView
     * @param bitmap
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap, LoadListener l) {
    	if(bitmap == null || bitmap.isRecycled()) {
    		//If bitmap is null, set default failed bitmap.
    		bitmap = mLoadFailedBitmap;
    	}
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drwabale and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mContext.getResources(), bitmap)
                    });
            // Set background to loading bitmap
            imageView.setBackgroundDrawable(
                    new BitmapDrawable(mContext.getResources(), mLoadingBitmap));
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
        if(l != null) {
        	l.onSet(imageView, bitmap);
        }
    }

    /**
     * Set the simple adapter which holds the backing data.
     *
     * @param adapter
     */
    public void setAdapter(ImageWorkerAdapter adapter) {
        mImageWorkerAdapter = adapter;
    }

    /**
     * Get the current adapter.
     *
     * @return
     */
    public ImageWorkerAdapter getAdapter() {
        return mImageWorkerAdapter;
    }

    /**
     * A very simple adapter for use with ImageWorker class and subclasses.
     */
    public static abstract class ImageWorkerAdapter {
        public abstract Object getItem(int num);
        public abstract int getSize();
    }
    
    
    /**
     * 
     * @author yinglovezhuzhu@gmail.com
     *
     */
    public static interface LoadListener {
    	
    	/**
    	 * On load start<br>
    	 * <p>Load from local cache file or load from Internet.
    	 * @param imageView
    	 * @param data
    	 */
    	public void onStart(ImageView imageView, Object data);
    	
    	/**
    	 * Show download progress.<br>
    	 * <p>This call back method only for download bitmap from the Internet.
    	 * @param url The URL of the bitmap.
    	 * @param total The total size of the downloading bitmap.<br>
    	 * return the download file total size or -1 unknown size.
    	 * @param downloaded The downloaded size of the downloading bitmap.
    	 */
    	public void onProgressUpdate(Object url, long total, long downloaded);
    	
    	/**
    	 * Error occur when loading image<br>
    	 * 
    	 * @param data
    	 * @param errorMsg
    	 */
    	public void onError(Object data, Object errorMsg);
    	
    	/**
    	 * Bitmap load finish.
    	 * @param imageView
    	 * @param bitmap
    	 */
    	public void onLoaded(ImageView imageView, Bitmap bitmap);
    	
    	/**
    	 * Bitmap set to an ImageView
    	 * @param imageView
    	 * @param bitmap
    	 */
    	public void onSet(ImageView imageView, Bitmap bitmap);
    	
    	/**
    	 * Task has been canceled.
    	 * @param imageView
    	 * @param data
    	 */
    	public void onCanceld(ImageView imageView, Object data);
    }
}
