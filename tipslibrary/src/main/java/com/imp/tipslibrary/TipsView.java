package com.imp.tipslibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @ClassName: TipsView
 * @Author: imp
 * @Description: 实现一个提示的动态动画效果
 * @Date: 2020/4/22 10:55 AM
 * @Version: 1.0
 */

public class TipsView extends View {

    private final String TAG = "TipsView";
    // 圆形外圈的半径大小
    private int mRadius;
    // 动画开始执行时的颜色
    private int startColor;
    // 动画结束执行时的颜色
    private int endColor;
    // 动画内圈绘制的图形 -> 根据枚举类型决定
    private String tipsType;
    // 动画重复执行的次数 -> 1表示不重复，0表示一直重复
    private int repeatTimes;
    private Paint circlePaint;
    // 外圈圆的厚度
    private int strokeWidth;
    private Path circlePath;
    private Path mPath;
    private float mCurrentValue;
    private PathMeasure pathMeasure;
    private boolean finishFirstCircle = false;
    private boolean finishSecondCircle = false;
    private int animationTimes = 2;


    public TipsView(Context context) {
        super(context);
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    public TipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 初始化自定义的属性
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TipsView);
        mRadius = (int) typedArray.getDimension(R.styleable.TipsView_circleRadius, 20);
        mRadius = DensityUtil.px2dip(context, mRadius);
        strokeWidth = (int) typedArray.getDimension(R.styleable.TipsView_circleStrokeWidth, 5);
        strokeWidth = DensityUtil.px2dip(context, strokeWidth);
        startColor = typedArray.getColor(R.styleable.TipsView_circleStartColor, Color.BLUE);
        endColor = typedArray.getColor(R.styleable.TipsView_circleEndColor, Color.RED);
        tipsType = typedArray.getString(R.styleable.TipsView_circleType);
        repeatTimes = typedArray.getInt(R.styleable.TipsView_repeatTimes, 1);
        typedArray.recycle();
        Log.e(TAG, mRadius + "  strokeWidth" + strokeWidth + "  startColor" + startColor
                + "  tiptype" + tipsType + "  repeatTimes" + repeatTimes);
    }

    // 初始化绘制参数
    private void initPaint() {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //创建画笔
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(startColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeJoin(Paint.Join.BEVEL);

        // 创建路径对象
        circlePath = new Path();

        setCirclePath();
        setTypePath();
        mPath = new Path();

        startAnimationPath();
    }

    // 设置外圈圆的路径
    private void setCirclePath() {
        // 重置路径
        circlePath.reset();
        // 绘制圆圈路径
        circlePath.addCircle(mRadius, mRadius, mRadius - strokeWidth, Path.Direction.CW);
        // 设置画笔颜色
        circlePaint.setColor(startColor);
    }

    private void setTypePath() {
        if (TipsEnum.RIGHT.getCode().equals(tipsType)) {
            // 绘制对号动画
            setRightPath();
            return;
        }

        if (TipsEnum.TRIANGLE.getCode().equals(tipsType)) {
            // 绘制三角形
            setTriAnglePath();
            return;
        }

        if (TipsEnum.LAMENT.getCode().equals(tipsType)) {
            // 绘制惊叹号
            setLamentPath();
            return;
        }

        if (TipsEnum.START.getCode().equals(tipsType)) {
            // 绘制开始键
            setStartPath();
            return;
        }

        if (TipsEnum.STOP.getCode().equals(tipsType)) {
            // 绘制暂停键
            setStopPath();
        }
    }

    // 绘制内部对勾图形
    private void setRightPath() {
        circlePath.moveTo(mRadius / 3f, mRadius);
        circlePath.lineTo(mRadius - mRadius / 5f, mRadius + mRadius / 2f);
        circlePath.lineTo(mRadius + 2 * mRadius / 3f, mRadius - mRadius / 4f);
    }

    // 绘制感叹号图形
    private void setLamentPath() {
        // 绘制竖线
        circlePath.moveTo(mRadius, mRadius / 3f);
        circlePath.lineTo(mRadius, mRadius + mRadius / 4f);

        // 绘制圆圈
        circlePath.addCircle(mRadius, mRadius + mRadius / 4f + 25, 6, Path.Direction.CCW);
    }

    // 绘制暂停图形
    private void setStopPath() {
        // 第一根竖线
        circlePath.moveTo(3 * mRadius / 4f, mRadius / 2f);
        circlePath.lineTo(3 * mRadius / 4f, mRadius + mRadius / 2f);

        // 第二根竖线
        circlePath.moveTo(mRadius / 4f + mRadius, mRadius / 2f);
        circlePath.lineTo(mRadius / 4f + mRadius, mRadius + mRadius / 2f);
    }

    // 绘制开始图形
    private void setStartPath() {
        circlePath.moveTo(2 * mRadius / 3f, 2 * mRadius / 3f);
        circlePath.lineTo(2 * mRadius / 3f, mRadius + mRadius / 3f - strokeWidth);
        circlePath.lineTo(mRadius + mRadius / 2f - strokeWidth, mRadius);
        circlePath.lineTo(2 * mRadius / 3f, 2 * mRadius / 3f);
        circlePath.lineTo(2 * mRadius / 3f, 2 * mRadius / 3f + strokeWidth);
//        circlePath.close();
    }

    // 绘制三角形
    private void setTriAnglePath() {
        circlePath.moveTo(mRadius, mRadius / 2f);
        circlePath.lineTo(mRadius / 2f + strokeWidth, mRadius + mRadius / 2f - strokeWidth);
        circlePath.lineTo(mRadius + mRadius / 2f - strokeWidth, mRadius + mRadius / 2f - strokeWidth);
        circlePath.lineTo(mRadius, mRadius / 2f);
        circlePath.lineTo(mRadius, mRadius / 2f + strokeWidth);
    }

    // 开启绘制动画
    private void startAnimationPath() {
        if (pathMeasure != null) {
            pathMeasure.setPath(circlePath, false);
        } else {
            pathMeasure = new PathMeasure(circlePath, false);
        }

        //动画效果
        ValueAnimator animator;
        if (TipsEnum.STOP.getCode().equals(tipsType) || TipsEnum.LAMENT.getCode().equals(tipsType)) {
            animationTimes = 3;
        } else {
            animationTimes = 2;
        }
        animator = ValueAnimator.ofFloat(0, animationTimes);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(2000);
        if (repeatTimes != 1 && repeatTimes > 0) {
            animator.setRepeatCount(repeatTimes);
        }
        if (repeatTimes == 0) {
            animator.setRepeatCount(ValueAnimator.INFINITE);
        }
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先绘制圆圈
        if (mCurrentValue < 1) {
            float stop = pathMeasure.getLength() * mCurrentValue;
            pathMeasure.getSegment(0, stop, mPath, true);

        } else if (mCurrentValue < 2) {
            if (!finishFirstCircle) {  //圆圈绘制完成,启动绘制下一部分，总共分为两个部分，圆圈和对号
                finishFirstCircle = true;
                pathMeasure.getSegment(0, pathMeasure.getLength(), mPath, true);
                //绘制下一个
                pathMeasure.nextContour();
            }
            pathMeasure.getSegment(0, pathMeasure.getLength() * (mCurrentValue - 1), mPath, true);
        } else {
            if (!finishSecondCircle) {
                finishSecondCircle = true;
                pathMeasure.getSegment(0, pathMeasure.getLength(), mPath, true);
                //绘制下一个
                pathMeasure.nextContour();
            }
            pathMeasure.getSegment(0, pathMeasure.getLength() * (mCurrentValue - 2), mPath, true);
        }
//        if (mCurrentValue < animationTimes-0.1) {
        canvas.drawPath(mPath, circlePaint);
//        } else {
//            if (repeatTimes == 0) {
//                finishFirstCircle = false;
//                finishSecondCircle = false;
//                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                mPath.reset();
//                startAnimationPath();
//            }
//        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量出信息
        int wMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);

        int width = 2 * mRadius;
        int height = 2 * mRadius;

        //设置位置信息
        setMeasuredDimension((wMeasureMode == MeasureSpec.AT_MOST) ? width : measureWidth,
                (hMeasureMode == MeasureSpec.AT_MOST) ? height : measureHeight);
    }

}
