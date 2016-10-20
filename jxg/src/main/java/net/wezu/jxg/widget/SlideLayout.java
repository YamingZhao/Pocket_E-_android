package net.wezu.jxg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by snox on 2016/1/25.
 */
public class SlideLayout extends ViewGroup {

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private boolean mFirstLayout = true;
    private Scroller mScroller;
    private int mCurrentScreen;

    private LayoutOvershootInterpolator mScrollInterpolator;

    private GestureDetector mGDetector = null;

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initWorkspace();
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHapticFeedbackEnabled(false);
        initWorkspace();
    }

    private void initWorkspace() {
        Context context = getContext();
        mScrollInterpolator = new LayoutOvershootInterpolator();
        mScroller = new Scroller(context, mScrollInterpolator);
        mGDetector = new GestureDetector(this.getContext(),
                new WorkspaceOnGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean g = mGDetector.onTouchEvent(ev);
        if (mCurrentScreen == 1
                && ev.getX() + getChildAt(1).getWidth() < getWidth()
                && ev.getAction() == MotionEvent.ACTION_DOWN) {
            snapToScreen(0, false);
            return true;
        }
        if (g && ev.getAction() == MotionEvent.ACTION_UP) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 先执行滑屏事件
        boolean g = mGDetector.onTouchEvent(ev);
        if (g && ev.getAction() == MotionEvent.ACTION_UP) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (mTouchState == TOUCH_STATE_SCROLLING) {
            postInvalidate();
        }
    }

    // @Override
    // protected void dispatchDraw(Canvas canvas) {
    // super.dispatchDraw(canvas);
    // for (int i = 0; i < getChildCount(); i++) {
    // drawChild(canvas, getChildAt(i), getDrawingTime());
    // }
    // }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "Workspace can only be used in EXACTLY mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "Workspace can only be used in EXACTLY mode.");
        }

        // The children are given the same width and height as the workspace
        getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
        getChildAt(1).measure(0, heightMeasureSpec);

        if (mFirstLayout) {
            setHorizontalScrollBarEnabled(false);
            scrollTo(0, 0);
            setHorizontalScrollBarEnabled(true);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth,
                        child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    public void snapToScreen(int toScreen, boolean settle) {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        if (settle) {
            mScrollInterpolator.setDistance(1);
        } else {
            mScrollInterpolator.disableSettle();
        }

        int duration = 300;
        if (toScreen == 1) {

            View focusedChild = getFocusedChild();
            if (focusedChild != null /* && focusedChild == getChildAt(1) */) {
                focusedChild.clearFocus();
            }
            mCurrentScreen = 1;
            mScroller.startScroll(0, 0, getChildAt(1).getWidth(), 0, duration);

        } else if (toScreen == 0) {

            View focusedChild = getFocusedChild();
            if (focusedChild != null /* && focusedChild == getChildAt(0) */) {
                focusedChild.clearFocus();
            }
            mCurrentScreen = 0;
            mScroller.startScroll(getChildAt(1).getWidth(), 0, -getChildAt(1)
                    .getWidth(), 0, duration);
        }
        invalidate();
    }

    private static class LayoutOvershootInterpolator implements Interpolator {
        private static final float DEFAULT_TENSION = 1.3f;
        private float mTension;

        public LayoutOvershootInterpolator() {
            mTension = DEFAULT_TENSION;
        }

        public void setDistance(int distance) {
            mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
        }

        public void disableSettle() {
            mTension = 0.f;
        }

        public float getInterpolation(float t) {
            // _o(t) = t * t * ((tension + 1) * t + tension)
            // o(t) = _o(t - 1) + 1
            t -= 1.0f;
            return t * t * ((mTension + 1) * t + mTension) + 1.0f;
        }
    }

    private class WorkspaceOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onSingleTapUp(MotionEvent e) {
            if (mCurrentScreen == 1
                    && e.getX() + getChildAt(1).getWidth() < getWidth()) {
                snapToScreen(0, true);
                return true;
            } else {
                return false;
            }
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            float delty = e2.getY() - e1.getY();
            float deltx = e2.getX() - e1.getX();
            Log.e("deltx========", deltx + "");
            Log.e("delty========", delty + "");
            Log.e("velocityX========", velocityX + "");
            if ((velocityX < -100 || deltx < -150) && Math.abs(delty) < 100
                    && mCurrentScreen == 0) {
                snapToScreen(1, true);
                return true;
            } else if ((velocityX > 100 || deltx > 150)
                    && Math.abs(delty) < 100 && mCurrentScreen == 1) {
                snapToScreen(0, true);
                return true;
            }
            return false;
        }

        public boolean onDown(MotionEvent e) {

            return true;
        }
    }

}
