package com.example.demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //服务器的接口地址
    private final static String URL = "http://192.168.43.188:8080/api/test";

    /**
     * 请求的类型
     */
    private interface RequestMethod {
        int GET = 1;
        int POST = 2;
    }

    private EditText activity_main_nickname_et;
    private EditText activity_main_age_et;
    private RadioButton activity_main_male_rb;
    private RadioButton activity_main_female_rb;
    private TextView activity_main_log_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        activity_main_nickname_et = findViewById(R.id.activity_main_nickname_et);
        activity_main_age_et = findViewById(R.id.activity_main_age_et);
        activity_main_male_rb = findViewById(R.id.activity_main_male_rb);
        activity_main_female_rb = findViewById(R.id.activity_main_female_rb);
        activity_main_log_tv = findViewById(R.id.activity_main_log_tv);

        //软键盘管理
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View view = getWindow().peekDecorView();
        Objects.requireNonNull(imm);

        findViewById(R.id.activity_main_get_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        request(RequestMethod.GET);
                    }
                });

        findViewById(R.id.activity_main_post_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        request(RequestMethod.POST);
                    }
                });
    }

    /**
     * 数据请求
     * @param requestType
     */
    private void request(final int requestType) {
        String nickname = activity_main_nickname_et.getText().toString();
        String age = activity_main_age_et.getText().toString();
        String gender = (activity_main_male_rb.isChecked()) ? "male" : "female";

        if (requestType == RequestMethod.GET) {
            OkGo.<String>get(URL)
                    .params("nickname", nickname)
                    .params("gender", gender)
                    .params("age", age)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String log = "GET请求：\n" + response.body();
                            activity_main_log_tv.setText(log);
                        }
                    });
        }else if (requestType == RequestMethod.POST) {
            OkGo.<String>post(URL)
                    .params("nickname", nickname)
                    .params("gender", gender)
                    .params("age", age)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String log = "POST请求：\n" + response.body();
                            activity_main_log_tv.setText(log);
                        }
                    });
        }
    }
}
