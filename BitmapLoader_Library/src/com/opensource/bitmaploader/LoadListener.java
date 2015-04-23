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

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * This class is a listener to listen bitmap load.
 *
 * @author yinglovezhuzhu@gmail.com
 */
public interface LoadListener {
	/**
     * On load start<br>
     * <p>Load from local cache file or load from Internet.
     *
     * @param imageView
     * @param data
     */
    public void onStart(ImageView imageView, Object data);

    /**
     * Show download progress.<br>
     * <p>This call back method only for download bitmap from the Internet.
     *
     * @param url        The URL of the bitmap.
     * @param total      The total size of the downloading bitmap.<br>
     *                   return the download file total size or -1 unknown size.
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
     *
     * @param imageView
     * @param bitmap
     */
    public void onLoaded(ImageView imageView, Bitmap bitmap);

    /**
     * Bitmap set to an ImageView
     *
     * @param imageView
     * @param bitmap
     */
    public void onSet(ImageView imageView, Bitmap bitmap);

    /**
     * Task has been canceled.
     *
     * @param imageView
     * @param data
     */
    public void onCanceld(ImageView imageView, Object data);
}
