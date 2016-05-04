package ligang.huse.cn.qqslidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 创建时间 javac on 2016/5/4.
 * <p>
 * 文  件 QQSlideMenu
 * <p>
 * 描  述
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu mSlideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        mSlideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //如果slidemenu打开，应该拦截，消费掉
        if (mSlideMenu != null && SlideMenu.DrageState.open == mSlideMenu.getCurrentState()){
            //mSlideMenu.open();
            return true;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果slidemenu打开，应该拦截，消费掉
        if (mSlideMenu != null && SlideMenu.DrageState.open == mSlideMenu.getCurrentState()){
            return true;
        }
        return super.onTouchEvent(event);
    }
}
