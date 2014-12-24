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

package com.opensource.bitmaploader.test.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.opensource.bitmaploader.ImageWorker;
import com.opensource.bitmaploader.Utils;
import com.opensource.bitmaploader.test.R;

/**
 * This fragment will populate the children of the ViewPager from {@link com.opensource.bitmaploader.test.ui.ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "resId";
    private int mImageNum;
    private ImageView mImageView;
    private ImageWorker mPicWorker;
    private ImageWorker mThumbWorker;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {
    }

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageNum The image number within the parent adapter to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static ImageDetailFragment newInstance(int imageNum) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);

        return f;
    }

    /**
     * Populate image number from extra, use the convenience factory method
     * {@link com.opensource.bitmaploader.test.ui.ImageDetailFragment#newInstance(int)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            mPicWorker = ((ImageDetailActivity) getActivity()).getPicWorker();
            mThumbWorker = ((ImageDetailActivity) getActivity()).getThumbWorker();
            if(mImageNum % 2 == 0) {
                mPicWorker.loadImage(mImageNum, mImageView, 0, new ImageWorker.LoadListener() {

                    @Override
                    public void onStart(ImageView imageView, Object data) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSet(ImageView imageView, Bitmap bitmap) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgressUpdate(Object url, long total, long downloaded) {
                    }

                    @Override
                    public void onLoaded(ImageView imageView, Bitmap bitmap) {
                        if (bitmap == null) {
                            Log.e("AAA", "Error occured when load image");
                            return;
                        }
                        Log.i("AAA", "Bitmap loaded=====^_^， size(" + bitmap.getWidth() + "," + bitmap.getHeight() + ")");
                    }

                    @Override
                    public void onError(Object data, Object errorMsg) {
                        Log.e("AAA", "Error" + errorMsg.toString());
                    }

                    @Override
                    public void onCanceld(ImageView imageView, Object data) {
                        // TODO Auto-generated method stub

                    }
                });
            } else {
                mThumbWorker.loadImage(mImageNum, mImageView, 0, new ImageWorker.LoadListener() {

                    @Override
                    public void onStart(ImageView imageView, Object data) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSet(ImageView imageView, Bitmap bitmap) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgressUpdate(Object url, long total, long downloaded) {
                    }

                    @Override
                    public void onLoaded(ImageView imageView, Bitmap bitmap) {
                        if (bitmap == null) {
                            Log.e("BBBB", "Error occured when load image");
                            return;
                        }
                        Log.i("BBBB", "Bitmap loaded=====^_^， size(" + bitmap.getWidth() + "," + bitmap.getHeight() + ")");
                    }

                    @Override
                    public void onError(Object data, Object errorMsg) {
                        Log.e("BBBB", "Error" + errorMsg.toString());
                    }

                    @Override
                    public void onCanceld(ImageView imageView, Object data) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasActionBar()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    /**
     * Cancels the asynchronous work taking place on the ImageView, called by the adapter backing
     * the ViewPager when the child is destroyed.
     */
    public void cancelWork() {
        ImageWorker.cancelWork(mImageView);
        mImageView.setImageDrawable(null);
        mImageView = null;
    }
}
