# Milink SDK 开发指南

### Version 1.0

# 概述

# 功能

1. 设备发现：发现[小米盒子][001]和[小米电视][002]，以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机、音箱。
2. 推送图片：推送图片到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机上显示。
3. 推送视频：推送视频到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机上播放。
4. 推送音乐：推送音乐到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机、音箱上播放。

# 需要的权限

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

# 支持的接收端

1. 小米盒子
2. AppleTV
3. Windows Media Player 12 （Windows 7/8 自带，需要开启“允许远程控制我的播放器”）
4. XBMC

# API
Milink已经做成了一个[Service][005]，上层应用通过API对其进行调用。

API主要由1个类和2个接口组成：

1. MilinkClientManager
2. MilinkClientManagerDataSource
3. MilinkClientManagerDelegate

## MilinkClientManager
这是最主要的类，所有接口都是非阻塞接口。

### 设置客户端名称

    void setDeviceName(String selfName);

说明
* 小米电视上收到手机推送的视频时，会提示：正在接收来自`xxx`的视频，`xxx`就是这里设置的客户端名称。
    
参数
* selfName - 客户端名称，任意字符串，比如`小米手机`、`小米视频`等。

### 设置数据源

    void setDataSource(MilinkClientManagerDataSource dataSource);

说明
* 推送照片时，为了加速，Milink做了优化处理，Milink服务会主动查询客户端的图片数据。

参数
* dataSource - 参考 `MilinkClientManagerDataSource`

### 设置代理

    void setDelegate(MilinkClientManagerDelegate delegate);

说明
* 所有的异步操作以及事件通知，Milink服务会主动回调delegate。

参数
* dataSource - 参考 `MilinkClientManagerDelegate`

### 打开Milink服务

    void open();

说明
* 如果打开成功，MilinkClientManagerDelegate的`onOpen()`会被回调。

### 关闭Milink服务

    void close();

说明
* 如果关闭成功，MilinkClientManagerDelegate的`onClose()`会被回调。

### 连接到某个设备

    ReturnCode connect(String deviceId, int timeout);

说明
* 如果连接成功，delegate的`onConnected()`会被回调。只有连接成功后，下面的接口才能被调用。
* 如果连接失败，delegate的`onConnectFailed()`会被回调。

参数
* deviceId 设备ID
* timeout 超时时间，单位为毫秒(ms)

返回值
* 见 ReturnCode

### 断开连接

    ReturnCode disconnect();

说明
* 如果断开连接成功，delegate的`onDisconnected()`会被回调。

返回值
* 见 ReturnCode


### 开始显示图片

    ReturnCode startShow();

说明
* 显示第一张图片前，需要调用`startShow()`。

返回值
* 见 ReturnCode

### 显示图片

    ReturnCode show(String photoUri);

说明
* 显示指定的图片

参数
* photoUri 图片地址，格式为：__/sdcard/1.jpg__ 或 __file:///sdcard/1.jpg__

返回值
* 见 ReturnCode

### 缩放图片

    ReturnCode zoomPhoto(String photoUri,
            int x,
            int y,
            int screenWidth,
            int screenHeight,
            int orgPhotoWidth,
            int orgPhotoHeight,
            float scale)
说明
* 缩放已经在TV上显示的图片

参数
* photoUri 图片地址
* x 当前位于手机屏幕中央位置的点在原照片中对应点的横坐标
* y 当前位于手机屏幕中央位置的点在原照片中对应点的纵坐标
* screenWidth 手机端屏幕的宽度
* screenHeight 手机端屏幕的高度
* orgPhotoWidth 照片原始宽度
* orgPhotoHeight 照片原始高度
* scale 缩放比例，浮点数（0-1之间）

返回值
* 见 ReturnCode

### 停止显示图片

    ReturnCode stopShow();

说明
* 不需要显示图片时，需要调用`stopShow()`。

返回值
* 见 ReturnCode

### 开始幻灯片显示

    ReturnCode startSlideshow(int duration, SlideMode Type);

说明
* 所谓的幻灯片显示，指的是图片一张一张自动在电视机上显示，如播放幻灯片一样。

参数
* duration 每张图片显示的间隔时间，单位为毫秒。
* type 显示模式，目前有__循环__和__非循环__模式。

返回值
* 见 ReturnCode

### 停止幻灯片显示

    ReturnCode stopSlideshow();

返回值
* 见 ReturnCode

### 开始播放音频或视频

    ReturnCode startPlay(String url, String title, int iPosition, double dPosition, MediaType type);

参数
* url 音频或视频的地址，如：__http://www.youku.com/demo.m3u8__，__/sdcard/movies/demo.mp4__, __file:///sdcard/movies/demo.mp4__
* title 内容名称
* iPosition 开始播放的位置，单位为毫秒。
* dPosition 开始播放的位置，单位为百分比，即播放器的进度条上的进度值。
* type 媒体类型，分为音频和视频。

返回值
* 见 ReturnCode

注意
* 开始播放的位置有2个参数（iPosition和dPosition），是为了兼容不得已而为之，如果发送方不能确定这两个值，填0即可。

### 停止播放

    ReturnCode stopPlay();

返回值
* 见 ReturnCode

### 设置播放速率

    ReturnCode setPlaybackRate(int rate);

参数
* rate 播放速率，0：为暂停，1：正常播放。

返回值
* 见 ReturnCode

### 获取播放速率

    int getPlaybackRate();

返回值
* 播放速率，-1：获取失败，0：为暂停，1：正常播放。

### 设置播放进度

    ReturnCode setPlaybackProgress(int position);
    
参数
* position 播放进度点，单位为毫秒。

返回值
* 见 ReturnCode

### 获取播放进度

    int getPlaybackProgress();

返回值
* 播放进度，-1：获取失败，> 0：获取成功，单位为毫秒。

### 获取播放内容长度

    int getPlaybackDuration();
    
返回值
* 内容长度，-1：获取失败，> 0：获取成功，单位为毫秒。

### 设置播放音量

    ReturnCode setVolume(int volume);

说明
* 请注意返回值，某些设备不支持此接口。

参数
* volume 音量值：[0, 100]

返回值
* 见 ReturnCode

### 获取播放音量

    int getVolume();

说明
* 某些设备无法读取播放音量。

返回值
* 音量值，-1：获取失败，> 0：获取成功。

## MilinkClientManagerDataSource

### 获取前一张图片

    String getPrevPhoto(String uri, boolean isRecyle);
    
说明
* 为了加速，Milink服务需要缓存图片，此时会主动调用此接口。

参数
* uri 当前图片的地址
* isRecyle true：循环，false：非循环。所谓循环的概念是指第一张图片的前一张图片，应该是最后一张。

返回值
* 返回当前图片（uri）的前一张图片。如果是第一张且isRecyle为true，返回最后一张；如果是第一张且isRecyle为false，返回null。

### 获取下一张图片

    String getNextPhoto(String uri, boolean isRecyle);

说明
* 为了加速，Milink服务需要缓存图片，此时会主动调用此接口。

参数
* uri 当前图片的地址
* isRecyle true：循环，false：非循环。所谓循环的概念是指最后一张图片的下一张图片，应该是第一张。

返回值
* 返回当前图片（uri）的下一张图片。

## MilinkClientManagerDelegate

### 打开Milink成功

    void onOpen();

### 关闭Milink成功

    void onClose();

### 设备上线

    void onDeviceFound(String deviceId, String name, DeviceType type);

说明
* 每找到一个设备，此接口会被回调一次，应用需要将设备列表保存起来。

参数
* deviceId 设备ID
* name 设备名称
* type 设备类型，分为电视和音箱

### 设备下线

    void onDeviceLost(String deviceId);

说明
* 某个设备下线（比如关机），此接口会被回调一次，应用需要将设备列表中的此设备删除。

### 连接成功

    void onConnected();

说明
* 只有连接成功后，才可以推送图片、音频和视频等操作。

### 连接失败

    void onConnectedFailed(ErrorCode errorCode);

说明
* 失败的原因很可能是网络状况不好，连接超时了。

### 断开连接

    void onDisconnected();

说明
* 断开连接，可以由应用主动断开，也可以是电视机主动断开。

### 电视机正在加载内容

    void onLoading();

### 电视机正在播放内容

    void onPlaying();

### 电视机已经停止播放

    void onStopped();

### 电视机已经暂停播放

    void onPaused();

### 电视机改变音量值

    void onVolume(int volume);

### 请求下一首音乐

    void onNextAudio(boolean isAuto);

说明
* 收到此请求后，如果希望继续播放下一首音乐，则继续调用`startPlay`

参数
* isAuto true：表示电视机自动播放到下一首，false：表示用遥控器播放下一首。

### 请求上一首音乐

    void onPrevAudio(boolean isAuto);

说明
* 收到此请求后，如果希望继续播放上一首音乐，则继续调用`startPlay`

参数
* isAuto true：表示电视机自动播放到上一首，false：表示用遥控器播放上一首。

# 其他数据类型

1. DeviceType
2. ErrorCode
3. MediaType
4. ReturnCode
5. SlideMode

### DeviceType

设备类型，分为三种：

    未知设备类型
    Unknown
    
    电视机或电视盒  
    TV
  
    音箱  
    Speaker




[001]: http://www.xiaomi.com/  (Xiaomi)
[002]: http://www.xiaomi.com/  (Xiaomi)
[003]: http://www.xiaomi.com/  (Xiaomi)
[004]: http://www.xiaomi.com/  (Xiaomi)
[005]: http://www.xiaomi.com/  (Xiaomi)
[006]: http://www.xiaomi.com/  (Xiaomi)
[007]: http://www.xiaomi.com/  (Xiaomi)
[008]: http://www.xiaomi.com/  (Xiaomi)
[009]: http://www.xiaomi.com/  (Xiaomi)
[010]: http://www.xiaomi.com/  (Xiaomi)
[011]: http://www.xiaomi.com/  (Xiaomi)
[012]: http://www.xiaomi.com/  (Xiaomi)
