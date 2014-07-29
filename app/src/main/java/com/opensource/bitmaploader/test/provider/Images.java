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

package com.opensource.bitmaploader.test.provider;

import com.opensource.bitmaploader.ImageWorker.ImageWorkerAdapter;

/**
 * Some simple test data to use for this sample app.
 */
public class Images {

    /**
     * This are PicasaWeb URLs and could potentially change. Ideally the PicasaWeb API should be
     * used to fetch the URLs.
     */
    public final static String[] imageUrls = new String[]{
            "http://img3.fengniao.com/forum/attachpics/666/176/26635081.jpg",
            "http://preview.cloud.189.cn/image/imageAction?param=0EDA6DC161D1C513EA9AC24E4971A67F53EF9DBF960172830E6377A065A7E8968F7A1E0B2C70B0A50400981E780F86C5EADDF30216C95D63C219F477DFB7B79CFF9082CE6516AD552739E1080325191B70D7B4B7E4C7222504D4D58963C6562536E53560",
            "http://desk.blueidea.com/DESK/QTBZ/dwbz/dwbz010.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635078.jpg",
            "http://www.ii123.com/uploads/allimg/c121123/1353A261D0460-410C.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635077.jpg",
            "http://preview.quanjing.com/chineseview037/mhrf-cpmh-19218.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635082.jpg",
            "http://img012.21cnimg.com/photo/2014/0725/11/ca1d8f7125a7004f_o.jpg",
    };
    /**
     * Simple static adapter to use for images.
     */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.imageUrls[num];
        }

        @Override
        public int getSize() {
            return Images.imageUrls.length;
        }
    };
    /**
     * This are PicasaWeb thumbnail URLs and could potentially change. Ideally the PicasaWeb API
     * should be used to fetch the URLs.
     */
    public static String[] imageThumbUrls = new String[]{
            "http://img3.fengniao.com/forum/attachpics/666/176/26635081.jpg",
            "http://preview.cloud.189.cn/image/imageAction?param=0EDA6DC161D1C513EA9AC24E4971A67F53EF9DBF960172830E6377A065A7E8968F7A1E0B2C70B0A50400981E780F86C5EADDF30216C95D63C219F477DFB7B79CFF9082CE6516AD552739E1080325191B70D7B4B7E4C7222504D4D58963C6562536E53560",
            "http://desk.blueidea.com/DESK/QTBZ/dwbz/dwbz010.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635078.jpg",
            "http://www.ii123.com/uploads/allimg/c121123/1353A261D0460-410C.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635077.jpg",
            "http://preview.quanjing.com/chineseview037/mhrf-cpmh-19218.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635082.jpg",
            "http://img012.21cnimg.com/photo/2014/0725/11/ca1d8f7125a7004f_o.jpg",
    };
    /**
     * Simple static adapter to use for image thumbnails.
     */
    public final static ImageWorkerAdapter imageThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.imageThumbUrls[num];
        }

        @Override
        public int getSize() {
            return Images.imageThumbUrls.length;
        }
    };
}
