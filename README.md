>机器人客服，目前我们在微信公众号、手机应用和一些网站上都会有接触到，刚好最近项目也有接入机器人对话的需求，这里用的是图灵机器人。下面直接说如何快速将图灵机器人 sdk 接入到我们的应用中来。

* **首先，到 [图灵机器人官网](http://www.tuling123.com/ "图灵机器人官网") 注册一个账号，根据自己需求在 我的机器人 里面创建一个机器人:**


![创建机器人](http://upload-images.jianshu.io/upload_images/2591553-24613f49d4fc49cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

创建好之后会得到一个 APIkey 和 secret ，在集成 sdk 时会用到，下载 Android SDK ，里面 libs 文件夹里有我们需要的  jar 包。

*  **开始集成**
1 新建一个项目，将下载的 libs 文件夹里面的 volley.jar 和 turingSDK.jar 放到工程的 libs 目录中，其他的  jar 包是语音相关的，这里我们不管；
2 给 AndroidMainfest.xml 添加权限：
```
<!-- SDK必须 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

3 请求机器人网络回调监听：
返回结果：
```
  {
      "code": 200000,
      "text": "亲，已帮你找到图片",
      "url": "http://m.image.so.com/i?q=%E5%B0%8F%E7%8B%97"
   }
```

网络回调：
```
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {
        @Override
        public void onSuccess(String result) {
            if (result != null) {
                try {
                    Log.d(TAG, "result" + result);
                    JSONObject result_obj = new JSONObject(result);
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        mHandler.obtainMessage(MSG_SPEECH_START,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onFail(int code, String error) {
            Log.d(TAG, "onFail code:" + code + "|error:" + error);
            mHandler.obtainMessage(MSG_SPEECH_START, "网络慢脑袋不灵了").sendToTarget();
        }
    };
```

Handler 将得到的字符串显示在 UI 上：
```
 private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SPEECH_START:
                    addData(Constant.APP,(String) msg.obj);
                    break;
            }
        }

        ;
    };
```

初始化 SDK：
这里的 TURING_APIKEY 和 TURING_SECRET 分别是创建机器人是的  APIkey 和 secret 。
```
    private void init() {
        mTuringManager = new TuringManager(this,TURING_APIKEY, TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);
    }
```
发出请求：
public void requestTuring(String requestInfo)
参数：requestInfo 为传递的文本
eg:
```
mTuringManager.requestTuring(etInput.getText().toString().trim());
```


* 至此，整个流程做完。 [Demo](http://www.tuling123.com/ "图灵机器人Demo")


![附图片](http://upload-images.jianshu.io/upload_images/2591553-1066501eb664486f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
