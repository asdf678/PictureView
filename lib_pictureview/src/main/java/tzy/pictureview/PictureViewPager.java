package tzy.pictureview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/2/22.
 */

public class PictureViewPager extends ViewPager {
    public PictureViewPager(Context context) {
        super(context);
    }

    public PictureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean val = false;
        try {
            val = super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return val;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final boolean val = super.onTouchEvent(ev);
        return val;
    }
}
