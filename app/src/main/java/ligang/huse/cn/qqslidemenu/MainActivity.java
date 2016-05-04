package ligang.huse.cn.qqslidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView mMenu_listview;
    private ListView mMain_listview;
    private SlideMenu slidemenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//隐藏标题栏
        setContentView(R.layout.activity_main);
        initView();
        initData();



    }

    private void initData() {
        //给listView填充数据
        mMenu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });


        mMain_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                //先缩小
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                //以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });


        slidemenu.setListener(new SlideMenu.onDragStateChangeListener() {
            @Override
            public void Onopen() {
                mMenu_listview.smoothScrollToPosition(new Random().nextInt(mMenu_listview.getCount()));
            }

            @Override
            public void Close() {
                ViewPropertyAnimator.animate(iv_head)
                        .translationXBy(15)
                        .setInterpolator(new CycleInterpolator(5))
                        .setDuration(400)
                        .start();
            }

            @Override
            public void OnDragin(float fraction) {
                ViewHelper.setAlpha(iv_head, 1 - fraction);
            }
        });
       my_layout.setSlideMenu(slidemenu);

    }

    private void initView() {
        mMenu_listview = (ListView) findViewById(R.id.menu_listview);
        mMain_listview = (ListView) findViewById(R.id.main_listview);
        slidemenu = (SlideMenu) findViewById(R.id.slidemenu);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        my_layout= (MyLinearLayout) findViewById(R.id.my_layout);
    }
}