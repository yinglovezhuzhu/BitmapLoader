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
            "http://img3.fengniao.com/forum/attachpics/666/176/26635081.jpg",
            "http://preview.cloud.189.cn/image/imageAction?param=0EDA6DC161D1C513EA9AC24E4971A67F53EF9DBF960172830E6377A065A7E8968F7A1E0B2C70B0A50400981E780F86C5EADDF30216C95D63C219F477DFB7B79CFF9082CE6516AD552739E1080325191B70D7B4B7E4C7222504D4D58963C6562536E53560",
            "http://b.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c6e4186a485d6277f9e2ff8b4.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635078.jpg",
            "http://www.ii123.com/uploads/allimg/c121123/1353A261D0460-410C.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635077.jpg",
            "http://preview.quanjing.com/chineseview037/mhrf-cpmh-19218.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635082.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec638670f92d2eb9389b506b4d.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/1e30e924b899a901d843e9ab1f950a7b0208f53d.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d452195603f6d55fbb3fbd981.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93332973c3eaf4bd11373f00144.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/9345d688d43f8794032b5a2fd01b0ef41ad53ade.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/a2cc7cd98d1001e9a60bfb98ba0e7bec54e79776.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/b3fb43166d224f4ab10be01d0bf790529822d12a.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/b3b7d0a20cf431adc5ee149c4936acaf2edd9852.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/b21bb051f8198618d58cfd8548ed2e738ad4e6ec.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/d009b3de9c82d1581a77bcbb820a19d8bd3e42c7.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93321a62f74af4bd11373f001af.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/43a7d933c895d1435ed8272e71f082025aaf0715.jpg",
    };
    /**
     * Simple static adapter to use for images.
     */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.imageUrls[num];
//            return Images.imageThumbUrls[num];
        }

        @Override
        public int getSize() {
            return Images.imageThumbUrls.length;
//            return Images.imageUrls.length;
        }
    };
    /**
     * This are PicasaWeb thumbnail URLs and could potentially change. Ideally the PicasaWeb API
     * should be used to fetch the URLs.
     */
    public static String[] imageThumbUrls = new String[]{
           "http://img3.fengniao.com/forum/attachpics/666/176/26635081.jpg",
           "http://img3.fengniao.com/forum/attachpics/666/176/26635081.jpg",
            "http://preview.cloud.189.cn/image/imageAction?param=0EDA6DC161D1C513EA9AC24E4971A67F53EF9DBF960172830E6377A065A7E8968F7A1E0B2C70B0A50400981E780F86C5EADDF30216C95D63C219F477DFB7B79CFF9082CE6516AD552739E1080325191B70D7B4B7E4C7222504D4D58963C6562536E53560",
            "http://b.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c6e4186a485d6277f9e2ff8b4.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/26635078.jpg",
//            "http://www.ii123.com/uploads/allimg/c121123/1353A261D0460-410C.jpg",
//            "http://img3.fengniao.com/forum/attachpics/666/176/26635077.jpg",
//            "http://preview.quanjing.com/chineseview037/mhrf-cpmh-19218.jpg",
            "http://www.ii123.com/uploads/allimg/c121123/1353A261D0460-410C2.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/266350773.jpg",
            "http://img3.fengniao.com/forum/attachpics/666/176/266350822.jpg",

            "http://img3.fengniao.com/forum/attachpics/666/176/26635082.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec638670f92d2eb9389b506b4d.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/1e30e924b899a901d843e9ab1f950a7b0208f53d.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513d452195603f6d55fbb3fbd981.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93332973c3eaf4bd11373f00144.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/9345d688d43f8794032b5a2fd01b0ef41ad53ade.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/a2cc7cd98d1001e9a60bfb98ba0e7bec54e79776.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/b3fb43166d224f4ab10be01d0bf790529822d12a.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/b3b7d0a20cf431adc5ee149c4936acaf2edd9852.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/b21bb051f8198618d58cfd8548ed2e738ad4e6ec.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/d009b3de9c82d1581a77bcbb820a19d8bd3e42c7.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93321a62f74af4bd11373f001af.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/43a7d933c895d1435ed8272e71f082025aaf0715.jpg",
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
