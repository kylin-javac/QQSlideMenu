package ligang.huse.cn.qqslidemenu.Test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 创建时间 javac on 2016/5/2.
 * <p/>
 * 文  件 QQSlideMenu
 * <p/>
 * 描  述 demo
 */
public class DragLayout extends FrameLayout {
    /**
     * onMeasure:测量控件
     * onLayout:画控件的具体位置
     */
    private View mRedView;
    private View mBlueView;
    private ViewDragHelper mViewDragHelper;

    public DragLayout(Context context) {
        super(context);
        init();
    }


    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {

        mViewDragHelper = ViewDragHelper.create(this, (float) 1.0, mCallback);
    }

    @Override
    //当DragLayout的xml布局的结束标签被读取完，此时就会知道自己有几个子view了
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRedView = getChildAt(0);
        mBlueView = getChildAt(1);
    }
//
//
//    @Override
//    //测量控件
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureSpec = MeasureSpec.makeMeasureSpec(mRedView.getLayoutParams().width, MeasureSpec.EXACTLY);
//        mRedView.measure(measureSpec, measureSpec);
//        mBlueView.measure(measureSpec,measureSpec);
//
//    }

    @Override
    //画出控件的具体位置
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        mRedView.layout(left, top, left + mRedView.getMeasuredWidth(), top + mRedView.getMeasuredHeight());
        mBlueView.layout(left, mRedView.getBottom(), left + mBlueView.getMeasuredWidth(), mRedView.getBottom() + mBlueView.getMeasuredHeight());
    }


    @Override
    //让ViewDragHelper帮我们判断是否应该拦截
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    //将触摸事件交给ViewDragHelper，让他来帮助我们处理
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;//返回true表示我们已经消费了该事件
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        //判断是否捕获child的触摸事件
        //child：当前的子view
        //return:true，表示捕获处理,false 表示不处理
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mRedView || child == mBlueView;
        }

        @Override
        //当view被开始和捕获的回调
        //capturedChild:当前被捕获的子view
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        //水平方向拖拽范围
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        //垂直方向拖拽范围
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        //控制子view在水平方向的移动范围
        //left:表示ViewDragHepler认为你想让当前的child移动的left改变的值;left=getLeft+dx
        //dx:表示本次view水平移动的距离
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //检查是否超过屏幕宽度
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        @Override
        //控制子view在垂直方向的移动范围
        //left:表示ViewDragHepler认为你想让当前的child移动的top改变的值;top=getTop+dy
        //dy:表示本次view垂直移动的距离
        public int clampViewPositionVertical(View child, int top, int dy) {
            //检查是否超过屏幕高度
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
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
            if (mRedView == changedView) {//当移动红色textView时绿色TextView一起移动
                mBlueView.layout(mBlueView.getLeft() + dx, mBlueView.getTop() + dy, mBlueView.getRight() + dx, mBlueView.getBottom() + dy);
            } else if (mBlueView == changedView) {//当移动绿色TextView时红色TextView一起移动
                mRedView.layout(mRedView.getLeft() + dx, mRedView.getTop() + dy, mRedView.getRight() + dx, mRedView.getBottom() + dy);
            }
            //计算view移动的百分比
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView.getMeasuredWidth());
            //执行滑动动画
            exectueAmin(fraction);
        }

        @Override
        //手指抬起时该执行的方法
        //releasedChild:当前抬起的子view
        //xvel:x轴方向移动的速度 正:向右移动,负：向左移动
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {
                //向左缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            } else {
                //向右缓慢滑动
                mViewDragHelper.smoothSlideViewTo(releasedChild, getMeasuredWidth() - releasedChild.getMeasuredWidth(), releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }

        }


    };

    @Override
    public void computeScroll() {

       if(mViewDragHelper.continueSettling(true)){
           ViewCompat.postInvalidateOnAnimation(DragLayout.this);
       }
    }

    public void exectueAmin(float fraction) {
        mBlueView.setRotationX(360*fraction);
        mRedView.setRotationX(360*fraction);
        mRedView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.BLUE));
        mBlueView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.BLUE,Color.RED));
    }
}
