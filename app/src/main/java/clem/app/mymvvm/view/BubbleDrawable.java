package clem.app.mymvvm.view;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import static clem.app.mymvvm.view.BubbleDrawable.ArrowDirection.BOTTOM;

public class BubbleDrawable extends Drawable {


    public enum ArrowDirection {
        LEFT, TOP, RIGHT, BOTTOM
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        mArrowDirection = arrowDirection;
    }

    public void setRelativePosition(float relativePosition) {
        mRelativePosition = Math.min(Math.max(relativePosition,0),1);
    }

    public int getBubbleLength() {
        return mBubbleLength;
    }

    public void setBubbleLength(int bubbleLength) {
        mBubbleLength = bubbleLength;
    }

    private int mBubbleLength;

    private int mBgColor;

    private float mCornerRadius;

    private ArrowDirection mArrowDirection=BOTTOM;

    /**
     * from 0 to 1
     */
    private float mRelativePosition;

    private Paint mPaint;

    private RectF mRect;

    private Path mPath;

    private PointF[] mPointFs;

    public BubbleDrawable() {
        mPaint = new Paint();
        mRect = new RectF();
        mPointFs = new PointF[3];
        mPath = new Path();
        mPointFs[0] = new PointF();
        mPointFs[1] = new PointF();
        mPointFs[2] = new PointF();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(mBgColor);
        Rect bounds = getBounds();
        mBubbleLength = Math.min(Math.max(bounds.width(),bounds.height())/5,mBubbleLength);
        int left = bounds.left;
        int top = bounds.top;
        int right = bounds.right;
        int bottom = bounds.bottom;
        float length;
        switch (mArrowDirection){
            case LEFT:
                left += mBubbleLength;
                length = Math.max(mRelativePosition * bounds.height() + bounds.left,mBubbleLength + mCornerRadius);
                length = Math.min(length,bounds.height() - mBubbleLength - mCornerRadius);
                mPointFs[0].set(bounds.left,length + bounds.top);
                mPointFs[1].set(mPointFs[0].x + mBubbleLength,mPointFs[0].y - mBubbleLength);
                mPointFs[2].set(mPointFs[0].x + mBubbleLength,mPointFs[0].y + mBubbleLength);
                break;
            case TOP:
                top += mBubbleLength;
                length = Math.max(mRelativePosition * bounds.width() + bounds.top,mBubbleLength + mCornerRadius);
                length = Math.min(length,bounds.width() - mBubbleLength - mCornerRadius);
                mPointFs[0].set(bounds.left + length,bounds.top);
                mPointFs[1].set(mPointFs[0].x - mBubbleLength, mPointFs[0].y + mBubbleLength);
                mPointFs[2].set(mPointFs[0].x + mBubbleLength, mPointFs[0].y + mBubbleLength);
                break;
            case RIGHT:
                right -= mBubbleLength;
                length = Math.max(mRelativePosition * bounds.height() + bounds.right,mBubbleLength + mCornerRadius);
                length = Math.min(length,bounds.height() - mBubbleLength - mCornerRadius);
                mPointFs[0].set(bounds.right,length + bounds.top);
                mPointFs[1].set(mPointFs[0].x - mBubbleLength,mPointFs[0].y - mBubbleLength);
                mPointFs[2].set(mPointFs[0].x - mBubbleLength,mPointFs[0].y + mBubbleLength);
                break;
            case BOTTOM:
                bottom -= mBubbleLength;
                length = Math.max(mRelativePosition * bounds.width() + bounds.bottom,mBubbleLength + mCornerRadius);
                length = Math.min(length,bounds.width() - mBubbleLength - mCornerRadius);
                mPointFs[0].set(bounds.left + length,bounds.bottom);
                mPointFs[1].set(mPointFs[0].x - mBubbleLength, mPointFs[0].y - mBubbleLength);
                mPointFs[2].set(mPointFs[0].x + mBubbleLength, mPointFs[0].y - mBubbleLength);
                break;
        }
        mRect.set(left,top,right,bottom);

        mPath.reset();
//        mPath.moveTo(mPointFs[0].x, mPointFs[0].y);
//        mPath.lineTo(mPointFs[1].x, mPointFs[1].y);
//        mPath.lineTo(mPointFs[2].x, mPointFs[2].y);
        length = mRect.right;
        mPath.moveTo(length/2, mPointFs[0].y);
        mPath.lineTo(length/2 - mBubbleLength, mPointFs[1].y);
        mPath.lineTo(length/2 + mBubbleLength, mPointFs[2].y);
        mPath.close();
        mPaint.setAntiAlias(true);
        canvas.drawRoundRect(mRect,mCornerRadius,mCornerRadius,mPaint);
        canvas.drawPath(mPath,mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
