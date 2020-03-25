package clem.app.mymvvm.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import clem.app.mymvvm.R;
import clem.app.mymvvm.util.DensityUtils;

public class SmoothRoundProgressBar extends View {

    private static final String TAG = "SmoothRoundProgressBar";

    public static final String IDLE = "IDLE";
    public static final String CONNECTING = "CONNECTING";
    public static final String CONNECTED = "CONNECTED";
    public static final String INIT = "INIT";

//可以设置的值
    private int strokeWidth;
//    private int strokeWidth2 = 10;
    private int startColor;
    private int endColor;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;


    private RotateAnimation animation;


    private Paint ringPaint;
    private Paint ringPaint2;
    private int[] colors;// 用于渐变
    private final RectF rectF = new RectF();
    private Paint halfRoundPaint;

    private int ringWidth;
    private int ringHeight;

    public SmoothRoundProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public SmoothRoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SmoothRoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SmoothRoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private int dip2px(int dp){
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }


    @SuppressWarnings("ResourceType")
    private void init(final Context context, final AttributeSet attrs) {




            strokeWidth = dip2px(7);

            startColor = Color.parseColor("#4DFFFFFF");
            endColor = Color.parseColor("#E6FFFFFF");
            duration = 1200;



        //获取系统定义的属性.注意此时拿到的layoutparams为空
         int[] attrsArray = new int[] {
        android.R.attr.id, // 0
        android.R.attr.background, // 1
        android.R.attr.layout_width, // 2
        android.R.attr.layout_height // 3
        };

         TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
       // int id = ta.getResourceId(0, View.NO_ID);
       // Drawable background = ta.getDrawable(1);
        int layout_width = ta.getDimensionPixelSize(2, ViewGroup.LayoutParams.MATCH_PARENT);
       // int layout_height = ta.getDimensionPixelSize(3, ViewGroup.LayoutParams.MATCH_PARENT);
        ta.recycle();

        Log.e("height","height:"+layout_width);
        if (layout_width > 0){
            strokeWidth = (int) (layout_width  * 0.1 );
        }else {
            strokeWidth = dip2px(5);
        }





        //获取xml中设置的自定义属性
            TypedArray typedArray = null;
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleProgressBar);
                strokeWidth = (int) typedArray.getDimension(R.styleable.MyCircleProgressBar_cpb_strokeWidth, strokeWidth);
                startColor = typedArray.getColor(R.styleable.MyCircleProgressBar_cpb_startColor, startColor);
                endColor = typedArray.getColor(R.styleable.MyCircleProgressBar_cpb_endColor, endColor);
                duration = typedArray.getInteger(R.styleable.MyCircleProgressBar_cpb_duration,duration);
            } finally {
                if (typedArray != null) {
                    typedArray.recycle();
                }
            }





        colors = new int[]{startColor,endColor};

        //圆环的画笔
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setStrokeWidth(strokeWidth);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeJoin(Paint.Join.ROUND);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setColor(startColor);

        ringPaint2 = new Paint();
        ringPaint2.setAntiAlias(true);
        ringPaint2.setStyle(Paint.Style.FILL);
        ringPaint2.setColor(Color.BLUE);


        //开始的点的半圆画笔
//        halfRoundPaint = new Paint();
//        halfRoundPaint.setAntiAlias(true);
//        halfRoundPaint.setStrokeWidth(strokeWidth2);
//        halfRoundPaint.setColor(Color.BLUE);
//        halfRoundPaint.setStyle(Paint.Style.STROKE);
//        halfRoundPaint.setStrokeJoin(Paint.Join.ROUND);
//        halfRoundPaint.setStrokeCap(Paint.Cap.ROUND);


    }




    /**
     * @param color ColorInt
     */
    public void setEndColor(final int color) {
        if (this.endColor != color) {
            this.endColor = color;
            colors = new int[]{startColor,endColor};
            invalidate();
        }
    }

    public int getEndColor() {
        return this.endColor;
    }

    /**
     * @param width px
     */
    public void setStrokeWidth(final int width) {
        if (this.strokeWidth != width) {
            this.strokeWidth = width;
            // 画描边的描边变化
            ringPaint.setStrokeWidth(width);

            // 会影响measure
            requestLayout();
        }
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }





    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        final int minimumWidth = getSuggestedMinimumWidth();
//        final int minimumHeight = getSuggestedMinimumHeight();
//        Log.e("YView", "---minimumWidth = " + minimumWidth + "");
//        Log.e("YView", "---minimumHeight = " + minimumHeight + "");
//        int width = measureWidth(minimumWidth, widthMeasureSpec);
//        int height = measureHeight(minimumHeight, heightMeasureSpec);
//        setMeasuredDimension(width, height);

        //拿到要画的右边的半圆的矩形
        this.rectF.left = getMeasuredWidth()  - strokeWidth;
        this.rectF.top = getMeasuredHeight()/2 - strokeWidth/2;
        this.rectF.right = getMeasuredWidth()   ;
        this.rectF.bottom = getMeasuredHeight()/2 + strokeWidth/2;

        ringWidth = getMeasuredWidth();
        ringHeight = getMeasuredHeight();

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());

    }


//    private int measureWidth(int defaultWidth, int measureSpec) {
//
//        int specMode = MeasureSpec.getMode(measureSpec);
//        int specSize = MeasureSpec.getSize(measureSpec);
//        Log.e("YViewWidth", "---speSize = " + specSize + "");
//
//
//        switch (specMode) {
//            case MeasureSpec.AT_MOST:
//                defaultWidth = specSize + getPaddingLeft() + getPaddingRight();
//                Log.e("YViewWidth", "---speMode = AT_MOST");
//                break;
//            case MeasureSpec.EXACTLY:
//                Log.e("YViewWidth", "---speMode = EXACTLY");
//                defaultWidth = specSize;
//                break;
//            case MeasureSpec.UNSPECIFIED:
//                Log.e("YViewWidth", "---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
//        }
//        return defaultWidth;
//    }
//
//
//    private int measureHeight(int defaultHeight, int measureSpec) {
//
//        int specMode = MeasureSpec.getMode(measureSpec);
//        int specSize = MeasureSpec.getSize(measureSpec);
//        Log.e("YViewHeight", "---speSize = " + specSize + "");
//
//        switch (specMode) {
//            case MeasureSpec.AT_MOST:
//                defaultHeight = specSize + getPaddingTop() + getPaddingBottom();
//                Log.e("YViewHeight", "---speMode = AT_MOST");
//                break;
//            case MeasureSpec.EXACTLY:
//                defaultHeight = specSize;
//                Log.e("YViewHeight", "---speSize = EXACTLY");
//                break;
//            case MeasureSpec.UNSPECIFIED:
//                defaultHeight = Math.max(defaultHeight, specSize);
//                Log.e("YViewHeight", "---speSize = UNSPECIFIED");
//                break;
//        }
//        return defaultHeight;
//
//
//    }

    // 目前由于SweepGradient赋值只在构造函数，无法pre allocate & reuse instead
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int restore = canvas.save();

        final int cx = ringWidth / 2;
        final int cy = ringHeight / 2;
        final int radius = ringWidth / 2;

//        Log.d(TAG, "onDraw: " + cy + " " + cx);
        //内圈
//        canvas.drawCircle(cx, cy, ringHeight / 2, ringPaint2);
//        canvas.drawCircle(cx, cy, radius + strokeWidth2, halfRoundPaint);

        //渐变
        final SweepGradient sweepGradient = new SweepGradient(cx, cy, colors, null);
        ringPaint.setShader(sweepGradient);
        canvas.drawCircle(cx, cy, radius-strokeWidth- DensityUtils.dip2px(getContext(), 10), ringPaint);
       // canvas.restore();

        //画半弧和半圆
      /*  canvas.save();
        paint.setColor(Color.BLUE);
       // canvas.rotate((int) Math.floor(360.0f * 1) - 1+180, cx, cy);//旋转到最低点
        // canvas.drawArc(rectF, -90f, 180f, true, endPaint);
        canvas.drawArc(rectF, -90f, 180f, true, paint);
        canvas.drawCircle(rectF.centerX(),rectF.centerY(),8f,paint);*/
//        canvas.save();
//        canvas.drawOval(rectF, halfRoundPaint);


      //  canvas.restore();


       // canvas.restoreToCount(restore);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        animation = new RotateAnimation(0,359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(RotateAnimation.INFINITE);//无限循环
        LinearInterpolator lin = new LinearInterpolator();//默认状态是随sdk不同而不同，5.0以上会一快一慢，这里用线性插值器限定其匀速转动
        animation.setInterpolator(lin);
        this.startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }
}
