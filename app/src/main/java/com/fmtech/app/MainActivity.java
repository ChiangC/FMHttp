package com.fmtech.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fmtech.fmhttp.http.FMHttp;
import com.fmtech.fmhttp.http.interfaces.IDataListener;

public class MainActivity extends AppCompatActivity {
    public  static  final String url="http://192.168.100.24:8080/UserRecord/LoginServlet";
    private static final String TAG = "FMHttp";

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.content);
    }

    public void login(View view) {
        User user = new User();
        user.setName("13343491234");
        user.setPassword("123456");
        for (int i = 0; i < 5; i++) {
            FMHttp.sendJsonRequest(user, url, LoginRespense.class, new IDataListener<LoginRespense>() {
                @Override
                public void onSuccess(LoginRespense loginRespense) {
                    Log.i(TAG, loginRespense.toString());
                }

                @Override
                public void onFail() {
                    Log.i(TAG, "获取失败");
                }
            });
        }


    }

}
