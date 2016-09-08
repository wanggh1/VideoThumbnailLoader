# Android获取本地视频文件缩略图

这个库是将[程序亦非猿-Android获取本地视频文件的截图](http://yifeiyuan.me/2016/06/27/Android%E8%8E%B7%E5%8F%96%E6%9C%AC%E5%9C%B0%E8%A7%86%E9%A2%91%E6%96%87%E4%BB%B6%E7%9A%84%E6%88%AA%E5%9B%BE/)文章整理成库的，作为自己练手项目，他的代码没帖全哦=-=

**build.gradle**

```
compile 'com.xuie:videothumbnailloader:1.0.1'
```

**usage**

```
VideoThumbnailLoader.get().display(
        medias.get(position).getPath(),
        image,
        width,
        height,
        null);
```

add init code

```
public class App extends Application {
    @Override public void onCreate() {
        VideoThumbnailConfiguration.Builder config = new VideoThumbnailConfiguration.Builder(this);
        config.setKind(MediaStore.Video.Thumbnails.MINI_KIND);
        config.setQuality(100);
        config.setSaveBitmapFileDir(new File(Environment.getExternalStorageDirectory().getPath() + "/VideothumbnailCached/"));
        VideoThumbnailLoader.get().init(config.build());
    }
}
```


License
---
```
Copyright (C)  xuie0000 Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```