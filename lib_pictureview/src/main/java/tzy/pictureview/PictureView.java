package tzy.pictureview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by tzy on 2017/5/9.
 */

public class PictureView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnDoubleTapListener {

    private Matrix mMatirx;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private OnClickListener mOnClickListener;

    private final FlingRunnable mFlingRunnable = new FlingRunnable();
    private final ScaleRunnable mScaleRunnable = new ScaleRunnable();
    static final float MAX_SCALE = 2.0f;

    public PictureView(Context context) {
        this(context, null);
    }

    public PictureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        mGestureDetector.setOnDoubleTapListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
//        setScaleType(ScaleType.MATRIX);
//        setScaleType(ScaleType.MATRIX);
        updateDrawable();
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        Log.i("@@@@@@", "@@@@@@@:onSingleTapConfirmed");
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
//        Log.i("@@@@@@", "@@@@@@@:onDoubleTap");

        mScaleRunnable.startScale(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            mFlingRunnable.stop();
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            getParent().requestDisallowInterceptTouchEvent(true);

            mFlingRunnable.startUsingVelocity((int) -velocityX, (int) -velocityY);

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            mMatirx.postTranslate(-distanceX, -distanceY);

            final RectF image = getImageRect();
            final PointF point = getCorrection(getScrollBounds(image));

            boolean shouldScroll = false;
            if (point.x != 0) {
                shouldScroll = false;
            } else {
                shouldScroll = true;
            }

            getParent().requestDisallowInterceptTouchEvent(shouldScroll);

            mMatirx.postTranslate(point.x, point.y);

            setImageMatrix(mMatirx);

            return true;
        }
    };


    private boolean mScaling;


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        final float prevScale = mScale;
//        float newScale = mScale * detector.getScaleFactor();
//        newScale = Math.max(Math.min(newScale, mMaxScale), mMinScale);
//        float scaleRate = newScale / prevScale;
//        mMatirx.postScale(scaleRate, scaleRate, detector.getFocusX(), detector.getFocusY());
//        mScale *= scaleRate;

        matirxScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());

        final RectF image = getImageRect();
        final PointF point = getCorrection(getScrollBounds(image));
        if (point.x != 0 || point.y != 0) {
            mMatirx.postTranslate(point.x, point.y);
        }
        setImageMatrix(mMatirx);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        mScaling = false;
    }


    private RectF getScrollBounds(RectF rect) {
        float xmin = getWidth() - rect.right;
        float xmax = -rect.left;
        float ymin = getHeight() - rect.bottom;
        float ymax = -rect.top;


        if (xmin > xmax)
            xmin = xmax = (xmin + xmax) / 2;
        if (ymin > ymax)
            ymin = ymax = (ymin + ymax) / 2;

        return new RectF(xmin, ymin, xmax, ymax);
    }


    private PointF getCorrection(RectF bounds) {
        return new PointF(Math.min(Math.max(0, bounds.left), bounds.right),
                Math.min(Math.max(0, bounds.top), bounds.bottom));
    }


    //    private PointF getOffect(RectF rect) {
//        float xMax = -rect.left;
//        float xMin = getWidth() - rect.right;
//
//        float yMax = -rect.top;
//        float yMin = getHeight() - rect.bottom;
//
//        float X = 0, Y = 0;
//        if (xMax < xMin) {
//            X = (xMax + xMin) / 2;
//        } else {
//            if (xMax < 0)
//                X = xMax;
//            if (xMin > 0)
//                X = xMin;
//        }
//
//        if (yMax < yMin) {
//            Y = (yMax + yMin) / 2;
//        } else {
//            if (yMax < 0)
//                Y = yMax;
//            if (yMin > 0)
//                Y = yMin;
//        }
//
//        return new PointF(X, Y);
//
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getDrawable() == null || getDrawable() instanceof ThumbnailDrawable) {
            return false;
        } else {
            if (!super.onTouchEvent(event)) {

                mScaleGestureDetector.onTouchEvent(event);

                if (!mScaling) {
                    boolean val = mGestureDetector.onTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    return val;
                }

//                boolean val = mGestureDetector.onTouchEvent(event);
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                return val;
            }
        }

        return true;


    }

    public float getImageScale() {
        return mScale;
    }


    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
    }

    private int mDrawableHeight;
    private int mDrawableWidth;


    private float mScale = 1.0f;


    boolean matirxNewScale(float newScale, float px, float py) {
        final float prev = mScale;
        newScale = Math.max(Math.min(newScale, mMaxScale), mMinScale);
        float scaleRate = newScale / prev;
        mMatirx.postScale(scaleRate, scaleRate, px, py);
        mScale *= scaleRate;
        return newScale <= mMinScale || newScale >= mMaxScale;
    }

    boolean matirxScale(float scaleRate, float px, float py) {
        float newScale = mScale * scaleRate;
        return matirxNewScale(newScale, px, py);


    }


    private float mMaxScale, mMinScale, mMiddleScaleDivide;
    private int mOldMeasuredWidth, mOldMeasuredHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();

        if (measuredWidth != mOldMeasuredWidth || measuredHeight != mOldMeasuredHeight) {
            mOldMeasuredWidth = measuredWidth;
            mOldMeasuredHeight = measuredHeight;
            mMatirx.reset();
        }
        if (mMatirx.isIdentity()) {
            if (mDrawableWidth != 0 && mDrawableHeight != 0) {
                float s = (float) (getMeasuredWidth() * mDrawableHeight) / (mDrawableWidth * getMeasuredHeight());
                if (s < 0.5f) {
                    mMinScale = getMeasuredWidth() / (float) mDrawableWidth;
                    mMaxScale = getMeasuredHeight() / (float) mDrawableHeight;
                } else if (s < 1.0f) {
                    mMinScale = getMeasuredWidth() / (float) mDrawableWidth;
                    mMaxScale = getMeasuredHeight() * MAX_SCALE / (float) mDrawableHeight;
                } else if (s > 2.0f) {
                    mMinScale = getMeasuredHeight() / (float) mDrawableHeight;
                    mMaxScale = getMeasuredWidth() / (float) mDrawableWidth;
                } else if (s > 1.0f) {
                    mMinScale = getMeasuredHeight() / (float) mDrawableHeight;
                    mMaxScale = getMeasuredWidth() * MAX_SCALE / (float) mDrawableWidth;
                } else {
                    mMinScale = getMeasuredHeight() / (float) mDrawableHeight;
                    mMaxScale = getMeasuredWidth() * MAX_SCALE / (float) mDrawableWidth;
                }

            }

            mMatirx.postScale(mMinScale, mMinScale);

            RectF image = new RectF(0f, 0f, mDrawableWidth, mDrawableHeight);
            mMatirx.mapRect(image);
            mMatirx.postTranslate((getMeasuredWidth() - image.width()) / 2.0f, (getMeasuredHeight() - image.height()) / 2.0f);
            mMiddleScaleDivide = (mMaxScale + mMinScale) / 2;
            mScale = mMinScale;
            setImageMatrix(mMatirx);
        }


    }

    private RectF getImageRect() {
        final RectF rect = new RectF(getDrawable().getBounds());

        mMatirx.mapRect(rect);
        return rect;
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateDrawable();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateDrawable();
    }


    private void updateDrawable() {
//        if(!mFlingRunnable.mScroller.isFinished())

        if (mFlingRunnable != null) {
            mFlingRunnable.stop();
        }
        if (mScaleRunnable != null) {
            mScaleRunnable.scale.cancel();
        }
//        mScaleRunnable.scale.end();
        mDrawableWidth = getDrawable() == null ? 0 : getDrawable().getIntrinsicWidth();
        mDrawableHeight = getDrawable() == null ? 0 : getDrawable().getIntrinsicHeight();
        mScale = 1.0f;
        mMaxScale = 1.0f;
        mMinScale = 1.0f;
        mMiddleScaleDivide = 1.0f;


        if (mMatirx == null) {
            mMatirx = getImageMatrix();
        }

        mMatirx.reset();
        setScaleType(ScaleType.MATRIX);
        requestLayout();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }


    private final class ScaleRunnable {
        private final ObjectAnimator scale;
        private final static int DURATION_TIME = 200;
        private float x;
        private float y;

        ScaleRunnable() {
            scale = ObjectAnimator.ofFloat(this, "Scale", 1.0f, 1.0f).setDuration(DURATION_TIME);
        }

        public void startScale(float px, float py) {
            x = px;
            y = py;
            if (mScale <= mMiddleScaleDivide) {
                scale.setFloatValues(mScale, mMaxScale);
            } else {
                scale.setFloatValues(mScale, mMinScale);
            }
            scale.start();
        }

        public void setScale(float newScale) {


            boolean stop = matirxNewScale(newScale, x, y);
            final RectF image = getImageRect();
            final PointF point = getCorrection(getScrollBounds(image));

            mMatirx.postTranslate(point.x, point.y);
            setImageMatrix(mMatirx);
        }

        public void endScale() {
            scale.end();
        }
    }

    private class FlingRunnable implements Runnable {

        private final Scroller mScroller;
        private int mAnimationDuration = 400;

        private int mLastFlingX;
        private int mLastFlingY;

        public FlingRunnable() {
            mScroller = new Scroller(getContext());
        }

        private void startCommon() {
            removeCallbacks(this);
        }

        public boolean startUsingVelocity(int initialVelocityX, int initialVelocityY) {
            if (initialVelocityX == 0 && initialVelocityY == 0)
                return false;


            startCommon();

            int initialX = initialVelocityX < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            int initialY = initialVelocityY < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
            mScroller.fling(initialX, initialY, initialVelocityX, initialVelocityY,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            post(this);
            return true;
        }

        public void startUsingDistance(int distance) {
            if (distance == 0) return;

            startCommon();

            mLastFlingX = 0;
            mScroller.startScroll(0, 0, -distance, 0, mAnimationDuration);
            post(this);
        }

        public void stop() {
            removeCallbacks(this);
            endFling();
        }

        private void endFling() {
            mScroller.forceFinished(true);
        }

        @Override
        public void run() {

            if (getDrawable() == null) {
                endFling();
                return;
            }


            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();
            final int y = scroller.getCurrY();
            int deltaX = mLastFlingX - x;
            int deltaY = mLastFlingY - y;
            mMatirx.postTranslate(deltaX, deltaY);


            final RectF image = getImageRect();
            final PointF point = getCorrection(getScrollBounds(image));

            if (point.x != 0 && point.y != 0) {
                mMatirx.postTranslate(point.x, point.y);
                endFling();
            } else {
                mMatirx.postTranslate(point.x, point.y);
            }
            setImageMatrix(mMatirx);

            if (more) {
                mLastFlingX = x;
                mLastFlingY = y;
                post(this);
            } else {
                endFling();
            }
        }

    }


}
