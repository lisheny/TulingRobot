package com.lishent.tulingrobot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.lishent.tulingrobot.Constant.MSG_SPEECH_START;
import static com.lishent.tulingrobot.Constant.TURING_APIKEY;
import static com.lishent.tulingrobot.Constant.TURING_SECRET;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    @InjectView(R.id.recy_message_list)
    RecyclerView recyMessageList;
    @InjectView(R.id.et_input)
    EditText etInput;
    @InjectView(R.id.btn_send)
    ImageButton btnSend;

    private List<HomeListBeen> mDatas;
    private HomeListAdapter adapter;

    private TuringManager mTuringManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        init();

        if (mDatas == null) {
            mDatas = new ArrayList<HomeListBeen>();
        }

        addData(Constant.APP,"你好，请问有什么可以帮到你？");
        adapter = new HomeListAdapter(MainActivity.this, mDatas);
        recyMessageList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyMessageList.setAdapter(adapter);
    }

    /**
     * 添加讲话内容到列表
     * @param teller
     * @param content
     */
    private void addData(int teller, String content) {
        HomeListBeen homeListBeen = new HomeListBeen();
        homeListBeen.setTeller(teller);
        homeListBeen.setContent(content);
        mDatas.add(homeListBeen);
        if (null != adapter) {
            adapter.notifyItemInserted(mDatas.size() - 1);
            recyMessageList.scrollToPosition(mDatas.size() - 1);
            etInput.setText("");
            etInput.clearFocus();
        }
    }

    /**
     * 初始化turingSDK
     */
    private void init() {
        mTuringManager = new TuringManager(this,TURING_APIKEY, TURING_SECRET);
        mTuringManager.setHttpRequestListener(myHttpConnectionListener);

    }

    /**
     * 网络请求回调
     */
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

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        mTuringManager.requestTuring(etInput.getText().toString().trim());
        addData(Constant.USER,etInput.getText().toString().trim() );
        hideSoftInput(MainActivity.this);
    }


    /**
     * 动态隐藏软键盘
     * @param activity activity
     */
    public static void hideSoftInput(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
