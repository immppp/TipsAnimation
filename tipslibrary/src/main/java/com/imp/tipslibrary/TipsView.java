package com.imp.tipslibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ClassName: TipsView
 * @Author: imp
 * @Description: 实现一个提示的动态动画效果
 * @Date: 2020/4/22 10:55 AM
 * @Version: 1.0
 */

public class TipsView extends View {

    private static final String TAG = "TipsView";
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
    // 动画重复执行的次数（配置） -> 1表示不重复，0表示一直重复
    private int repeatTimeAtrrs;
    // 重绘标记
    private boolean onDrawFlag = false;
    // 绘制画笔
    private Paint circlePaint;
    // 外圈圆的厚度
    private int strokeWidth;
    // 监听
    private TipsViewClickListener tipsViewClickListener;
    // 绘制的路线（外圈圆+内圈图标）
    private Path circlePath;
    // 绘制的路线
    private Path mPath;
    // 当前路线的进度
    private float mCurrentValue;
    private PathMeasure pathMeasure;
    // 第二段动画是否完成，例如对号，三角形都为两个动画
    private boolean finishFirstCircle = false;
    // 第三段动画是否完成，例如暂停，感叹号都为三个动画
    private boolean finishSecondCircle = false;
    // 动画时长总进度，三段动画为3，两段动画为2
    private int animationTimes = 2;
    // 动画
    private ValueAnimator animator;

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
        repeatTimeAtrrs = typedArray.getInt(R.styleable.TipsView_repeatTimes, 1);
        repeatTimes = repeatTimeAtrrs;
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
            return;
        }

        if (TipsEnum.WRONG.getCode().equals(tipsType)) {
            // 绘制错误键
            setWrongPath();
        }
    }

    // 绘制内部对勾图形
    private void setRightPath() {
        circlePath.moveTo(mRadius / 2f, mRadius);
        circlePath.lineTo(mRadius - mRadius / 5f, mRadius + 2 * mRadius / 5f);
        circlePath.lineTo(mRadius + 2 * mRadius / 5f, mRadius - mRadius / 3f);
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
        circlePath.moveTo(2 * mRadius / 5f + strokeWidth, 2 * mRadius / 5f + strokeWidth);
        circlePath.lineTo(2 * mRadius / 5f + strokeWidth, 2 * mRadius - (2 * mRadius / 5f + strokeWidth));
        circlePath.lineTo(2 * mRadius - (2 * mRadius / 5f - strokeWidth), mRadius);
        circlePath.lineTo(2 * mRadius / 5f + strokeWidth, 2 * mRadius / 5f + strokeWidth);
        circlePath.lineTo(2 * mRadius / 5f + strokeWidth, 2 * mRadius - (2 * mRadius / 5f + strokeWidth));
//        circlePath.close();
    }

    // 绘制三角形
    private void setTriAnglePath() {
        circlePath.moveTo(mRadius, mRadius / 2f);
        circlePath.lineTo(mRadius / 2f + strokeWidth, mRadius + mRadius / 2f - strokeWidth);
        circlePath.lineTo(mRadius + mRadius / 2f - strokeWidth, mRadius + mRadius / 2f - strokeWidth);
        circlePath.lineTo(mRadius, mRadius / 2f);
        circlePath.lineTo(mRadius / 2f + strokeWidth, mRadius + mRadius / 2f - strokeWidth);
    }

    // 绘制错误
    private void setWrongPath() {
        // 第一根竖线
        circlePath.moveTo(3 * mRadius / 5f, 3 * mRadius / 5f);
        circlePath.lineTo(2 * mRadius / 5f + mRadius, mRadius + 2 * mRadius / 5f);

        // 第二根竖线
        circlePath.moveTo(2 * mRadius / 5f + mRadius, 3 * mRadius / 5f);
        circlePath.lineTo(3 * mRadius / 5f, mRadius + 2 * mRadius / 5f);
    }

    // 开启绘制动画
    private void startAnimationPath() {
        if (pathMeasure != null) {
            pathMeasure.setPath(circlePath, false);
        } else {
            pathMeasure = new PathMeasure(circlePath, false);
        }

        // 动画效果,当之前存在动画时，则之前的动画效果
        if (animator != null) {
            animator.cancel();
        }
        if (TipsEnum.STOP.getCode().equals(tipsType) || TipsEnum.LAMENT.getCode().equals(tipsType)
                || TipsEnum.WRONG.getCode().equals(tipsType)) {
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

    /**
     * onDraw方法，主要思路为：绘制主要分为两大类：无限循环绘制和多次绘制后停止操作；
     * 无限绘制就是一直绘制path，绘制完成后则清空画布，再次绘制
     * 多次绘制为将绘制次数缓存，当是最后一次绘制时，则重置画笔颜色，设置为默认配置色彩
     * 外部设置图标时会调用重绘
     *
     * @param canvas 画笔
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 外部调用，需要重新设置图形时调用该部分，主要清空画布的功能
        if (onDrawFlag) {
            finishFirstCircle = false;
            finishSecondCircle = false;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPath.reset();
            onDrawFlag = false;
            circlePath.reset();
            setCirclePath();
            setTypePath();
            startAnimationPath();
        }

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

        if (repeatTimes == 0) {     // 表示不停止动画，需要一直循环动画
            if (mCurrentValue < animationTimes - 0.1) {     // 一次循环中
                canvas.drawPath(mPath, circlePaint);
            } else {    // 一次循环完成，清空画布
                finishFirstCircle = false;
                finishSecondCircle = false;
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mPath.reset();
                startAnimationPath();
            }
        } else {    // 表示需要停止动画
            if (mCurrentValue < animationTimes - 0.001 || repeatTimes == 1) {   // 一次动画结束 ，设置完成
                if (mCurrentValue == animationTimes) {
                    circlePaint.setColor(endColor);
                }
                canvas.drawPath(mPath, circlePaint);
            } else {    // 表示动画需要重新绘制一遍
                if (repeatTimes > 1) {  // 表示次数大于1，需要重绘
                    repeatTimes--;
                    finishFirstCircle = false;
                    finishSecondCircle = false;
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mPath.reset();
                    startAnimationPath();
                }
            }
        }

    }

    /**
     * 重写onMeasure方法，主要设置适应控件大小
     *
     * @param widthMeasureSpec  宽度的配置要求，包含大小和与父类的匹配标准
     * @param heightMeasureSpec 高度的配置要求，包含大小和与父类的匹配标准
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 测量出信息
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

    @Override
    public boolean performClick() {
        if (tipsViewClickListener != null) {
            tipsViewClickListener.onClickListener(repeatTimeAtrrs, mRadius, tipsType);
        }
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "tipsView ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "tipsView ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    performClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    // 设置图标的方法
    public void setAnimationIcon(TipsEnum tipsEnum) {
        if (tipsEnum != null) {
            tipsType = tipsEnum.getCode();
            // 设置循环的次数
            repeatTimes = repeatTimeAtrrs;
            onDrawFlag = true;
            // 重新绘制
            invalidate();
        }
    }

    // 添加事件监听
    public void addTipsViewClickListener(TipsViewClickListener tipsViewClickListener) {
        this.tipsViewClickListener = tipsViewClickListener;
    }

}
