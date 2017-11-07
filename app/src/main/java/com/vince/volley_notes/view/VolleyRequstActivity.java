package com.vince.volley_notes.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.vince.volley_notes.R;
import com.vince.volley_notes.presenter.VollyRequestPresenter;
import com.vince.volley_notes.presenter.VollyRequestPresenterImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 用Volley模拟请求类
 */
public class VolleyRequstActivity
        extends AppCompatActivity
        implements View.OnClickListener
{
    private String mUrl;
    private Map<String, String> mParams = new HashMap<>();
    private VollyRequestPresenter mVollyRequestPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley_requst);

        initView();
    }

    private void initView() {

        Button mBtnOne   = (Button) findViewById(R.id.btn_one);
        Button mBtnTwo   = (Button) findViewById(R.id.btn_two);
        Button mBtnThree = (Button) findViewById(R.id.btn_three);
        Button mBtnFour  = (Button) findViewById(R.id.btn_four);
        Button mBtnFive  = (Button) findViewById(R.id.btn_five);
        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
        mBtnFour.setOnClickListener(this);
        mBtnFive.setOnClickListener(this);

        mVollyRequestPresenter = new VollyRequestPresenterImpl(this);

        mUrl = "http://v.juhe.cn/jztk/query";
        mParams.put("subject", "1");
        mParams.put("model", "c1");
        mParams.put("key", "a83eae4095a0bc9e0d381089c872ffde");
        mParams.put("testType", "rand");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                if (null != mVollyRequestPresenter) {
                    mVollyRequestPresenter.getRequest(mUrl, mParams);
                }
                break;
            case R.id.btn_two:
                if (null != mVollyRequestPresenter) {
                    mVollyRequestPresenter.postRequest(mUrl, mParams);
                }
                break;

            case R.id.btn_three:
                if (null != mVollyRequestPresenter) {
                    mVollyRequestPresenter.getJsonRequest(mUrl, mParams);
                }
                break;

            case R.id.btn_four:
                if (null != mVollyRequestPresenter) {
                    mVollyRequestPresenter.postJsonRequest(mUrl, mParams);
                }
                break;
            case R.id.btn_five:

                if (null != mVollyRequestPresenter) {
                    mVollyRequestPresenter.cancelRequest();
                }
                break;
        }
    }
}
