package ligang.huse.cn.qqslidemenu.Test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ligang.huse.cn.qqslidemenu.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//隐藏标题栏
        setContentView(R.layout.activity_test);
    }
}
