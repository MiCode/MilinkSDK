# UniPlay

这是一个使用MilinkSDK编写的Demo应用。

## 安装

首先确保手机中已经安装[MiLinkService-signed-release.apk][005]，之后再安装[UniPlay.apk][006]，命令如下：

    adb install $your directory$/UniPlay.apk

## 实现的功能

1. 设备发现：发现[小米盒子][001]和[小米电视][002]，以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机、音箱。
2. 推送图片：推送图片到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机上显示。
3. 推送视频：推送视频到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机上播放。
4. 推送音乐：推送音乐到[小米盒子][001]、[小米电视][002]、以及支持[AirPlay][003]或[DLNA][004]的电视盒、电视机、音箱上播放。

## 支持的接收端

1. 小米盒子
2. AppleTV
3. Windows Media Player 12 （Windows 7/8 自带，需要开启“允许远程控制我的播放器”）
4. XBMC

## 使用MilinkSDK开发注意事项

1. 工程Build Path要包含[milink.jar][007]。
2. 使用MilinkClientManager对象要保证单一性，推荐采用singleton模式。
3. 所有API均采用非阻塞模式，需使用回调机制判断API调用的结果。
4. 在推送视频和音乐时，要注意维护好本地播放状态。


[001]: http://www.xiaomi.com/  (Xiaomi)
[002]: http://www.xiaomi.com/  (Xiaomi)
[003]: http://www.xiaomi.com/  (Xiaomi)
[004]: http://www.xiaomi.com/  (Xiaomi)
[005]: https://github.com/jxfengzi/MilinkSDK/blob/master/SDK/MiLinkService-signed-release.apk  (MiLinkService-signed-release.apk)
[006]: https://github.com/jxfengzi/MilinkSDK/blob/master/UniPlay/v1/UniPlay.apk  (UniPlay.apk)
[007]: https://github.com/jxfengzi/MilinkSDK/blob/master/SDK/milink.jar  (milink.jar)
