package com.opensource.bitmaploader;
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
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * This class is a simple listener to listen bitmap load.
 *
 * @author yinglovezhuzhu@gmail.com
 */
public abstract class SimpleLoadListener implements LoadListener {

	@Override
	public void onProgressUpdate(Object url, long total, long downloaded) {

	}

	@Override
	public void onLoaded(ImageView imageView, Bitmap bitmap) {
		
	}

	@Override
	public void onCanceld(ImageView imageView, Object data) {

	}
	
	@Override
	public void onError(Object data, Object errorMsg) {
		
	}

}
