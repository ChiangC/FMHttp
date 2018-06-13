package com.fmtech.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fmtech.app.update.UpdateManager;
import com.fmtech.fmhttp.db.BaseDaoFactory;
import com.fmtech.fmhttp.http.FMHttp;
import com.fmtech.fmhttp.http.interfaces.IDataListener;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String url = "http://v.juhe.cn/toutiao/index?type=top&key=29da5e8be9ba88b932394b7261092f71";
    private static final String TAG = "dongnao";
    TextView textView;
    UpdateManager updateManager;
    UserDao baseDao;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.content);
        checkPermission();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
           init();
        }
    }

    private void init(){
        updateManager = new UpdateManager();
        baseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);
    }

    /**
     * 1
     * 2
     *
     * @param view
     */
    public void login(View view) {

        User user = new User();
        user.setName("V00" + (i++));
        user.setPassword("123456");
        user.setName("张三" + i);
        user.setUser_id("N000" + i);
        baseDao.insert(user);
        updateManager.checkThisVersionTable(this);
//        Volley.sendRequest(null, url,NewsPager.class, new IDataListener<NewsPager>() {
//            @Override
//            public void onSuccess(NewsPager loginRespense) {
//                Log.i(TAG,loginRespense.toString());
//            }
//
//            @Override
//            public void onFail() {
//                Log.i(TAG,"获取失败");
//            }
//        });


//        DownFileManager downFileService=new DownFileManager();
//        downFileService.download("http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk");
    }


    public void insert(View view) {
        Photo photo = new Photo();
        photo.setPath("data/data/my.jpg");
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        photo.setTime(dateFormat.format(new Date()));
        PhotoDao photoDao = BaseDaoFactory.getInstance().getUserHelper(PhotoDao.class, Photo.class);
        photoDao.insert(photo);
    }

    public void write(View view) {
        /**
         * 写入版本
         */
        updateManager.saveVersionInfo(this, "V002");

    }

    public void update(View view) {
        updateManager.checkThisVersionTable(this);

        updateManager.startUpdateDb(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            init();
        }

    }
}
