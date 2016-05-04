package ligang.huse.cn.qqslidemenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import ligang.huse.cn.qqslidemenu.Test.ColorUtil;

/**
 * 创建时间 javac on 2016/5/3.
 * <p/>
 * 文  件 QQSlideMenu
 * <p/>
 * 描  述 自定义QQ侧滑面板
 */
public class SlideMenu extends FrameLayout {

    private View mMainView;
    private View mMenuView;
    public ViewDragHelper mViewDragHelper;
    private float mDragRange;//拖拽的范围
    private FloatEvaluator mFloatEvaluator;
    private IntEvaluator mIntEvaluator;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            new IllegalArgumentException("只能有两个view");
        }
        mMenuView = getChildAt(0);
        mMainView = getChildAt(1);
        mFloatEvaluator = new FloatEvaluator();
        mIntEvaluator = new IntEvaluator();

    }

    @Override
    //让ViewDragHelper帮我们判断是否应该拦截
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    //将触摸事件交给ViewDragHelper，让他来帮助我们处理
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;//表示我们已经处理了该事件
    }

    @Override
    //该方法在OnMeasure执行之后执行，可以在该方法中初始化自己和子View的宽高
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth();
        mDragRange = (float) (width * 0.6);
    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        //判断是否捕获child的触摸事件
        //child：当前的子view
        //return:true，表示捕获处理,false 表示不处理
        public boolean tryCaptureView(View child, int pointerId) {
            return mMainView == child || mMenuView == child;
        }

        @Override
        //水平方向拖拽范围
        public int getViewHorizontalDragRange(View child) {
            return (int) mDragRange;
        }

        @Override
        //控制子view在水平方向的移动范围
        //left:表示ViewDragHepler认为你想让当前的child移动的left改变的值;left=getLeft+dx
        //dx:表示本次view水平移动的距离
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainView) {
                if (left < 0) {
                    left = 0;//限制main的左边
                }
                if (left > mDragRange) {
                    left = (int) mDragRange;//限制main的右边
                }
            }
            return left;
        }

        @Override
        //当changedView的位置改变的时候执行，一般用作其他子view的伴随移动
        //changedView:位置改变的子view
        //left:子view当前最新的left值
        //top:子view当前最新的top值
        //dx:子view水平移动的距离
        //dy:子view垂直移动的距离
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (changedView == mMenuView) {
                mMenuView.layout(0, 0, mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
                int newLeft = mMainView.getLeft() + dx;
                if (newLeft > mDragRange) {
                    newLeft = (int) mDragRange;
                }
                if (newLeft < 0) {
                    newLeft = 0;
                }
                mMainView.layout(newLeft, mMainView.getTop(), mMainView.getRight(), mMainView.getBottom());
            }
            //计算百分比
            float fraction = mMainView.getLeft() / mDragRange;
            //执行伴随动画
            execAnimal(fraction);
            //回调第四步:更改状态，回调onDragStateChangeListener方法
            if (fraction == 0 && currentState != DrageState.close) {
                //更改状态为关闭,并回调listener方法
                currentState = DrageState.close;
                if (Listener != null)
                    Listener.Close();
            } else if (fraction == 1f && currentState != DrageState.open) {
                //更改状态为打开，并回调listener方法
                currentState = DrageState.open;
                if (Listener != null)
                    Listener.Onopen();
            }
            //将fraction的值暴露给外部
            if (Listener != null) {
                Listener.OnDragin(fraction);
            }

        }

        @Override
        //手指抬起时该执行的方法
        //releasedChild:当前抬起的子view
        //xvel:x轴方向移动的速度 正:向右移动,负：向左移动
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mMainView.getLeft() < mDragRange / 2) {
                //向左滑动 打开mainView
                open();
            } else {
                //向右滑动 关闭mainView
                close();
            }
            Log.i("MAIN", ""+xvel);
            //根据x的移动速度来决定，是否打开和关闭mainView(往左 xvel的值越大所以关闭,往右 xvel的值越小 所以打开)
            if(xvel>300){
               close();

            }if(xvel<-300){
               open();

            }
        }
    };
    //关闭mainView
    public void close() {
        mViewDragHelper.smoothSlideViewTo(mMainView, (int) mDragRange, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    //打开mainView
    public void open() {
        mViewDragHelper.smoothSlideViewTo(mMainView, 0, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void execAnimal(float fraction) {
        //缩小mainView
        ViewHelper.setScaleX(mMainView, mFloatEvaluator.evaluate(fraction, 1f, 0.95f));
        ViewHelper.setScaleY(mMainView, mFloatEvaluator.evaluate(fraction, 1f, 0.9f));
        //移动menuView
        ViewHelper.setTranslationX(mMenuView, mIntEvaluator.evaluate(fraction, -mMenuView.getMeasuredWidth() / 2, 0));
        //放大menuView
        ViewHelper.setScaleX(mMenuView, mFloatEvaluator.evaluate(fraction, 0.5f, 1f));
        ViewHelper.setScaleY(mMenuView, mFloatEvaluator.evaluate(fraction, 0.5f, 1f));

        //改变menuView的透明度
        ViewHelper.setAlpha(mMenuView, mFloatEvaluator.evaluate(fraction, 0.3f, 1f));
        //给slidemenu的背景添加黑色背景遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    @Override
    public void computeScroll() {//回调此方法不断执行动画
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    //回调第二步定义一个借口变量
    private onDragStateChangeListener Listener;

    //回调第三步生成对外的set方法
    public void setListener(onDragStateChangeListener listener) {
        Listener = listener;
    }

    // 回调监听第一步
    public interface onDragStateChangeListener {
        void Onopen();//打开

        void Close();//关闭

        void OnDragin(float fraction);//拖拽中
    }

    //定义状态常量
    enum DrageState {
        open, close;
    }
    public DrageState getCurrentState(){
        return currentState;
    }

    private DrageState currentState = DrageState.close;//默认状态是关闭的
}
