package tzy.pictureview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Administrator on 2017/9/21 0021.
 */

public class ThumbnailDrawable extends BitmapDrawable {
    public ThumbnailDrawable(Resources res) {
        super(res);
    }

    public ThumbnailDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }
}
