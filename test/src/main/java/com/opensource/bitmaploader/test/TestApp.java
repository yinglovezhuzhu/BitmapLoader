/*
 *
 *  * Copyright (C) 2014 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  * auther:yinglovezhuzhu@gmail.com
 *  * date:14-5-15 下午3:00
 *  * version:v1.0
 *
 */

package com.opensource.bitmaploader.test;

import android.app.Application;

import com.opensource.bitmaploader.ImageWorker;


/**
 * Created by xiaoying on 14-5-15.
 */
public class TestApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ImageWorker.setDebug(BuildConfig.DEBUG);
    }

}