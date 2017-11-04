package com.jenny.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.jenny.ui.R;

import java.util.logging.Logger;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 *Created by ${Jenny} on 2017/10/15.
 *E-mail: fishloveqin@gmail.com
 * 自定义支付密码输入框
 * 支持明文和暗文显示
 * 密码位数可配置
 */

public class PayPwdInputView extends AppCompatEditText {

    private Context            mContext;
    /**
     * 第一个圆开始绘制的圆心坐标
     */
    private float              startX;
    private float              startY;

    private float              cX;

    /**
     * 实心圆的半径
     */
    private int                radius             = 10;
    /**
     * view的高度
     */
    private int                height;
    private int                width;

    /**
     * 当前输入密码位数
     */
    private int                textLength         = 0;
    private int                bottomLineLength;
    /**
     * 最大输入位数
     */
    private int                maxCount           = 6;
    /**
     * 圆的颜色   默认BLACK
     */
    private int                circleColor        = Color.BLACK;

    /**
     * 文本字体颜色
     */

    private int                textColor          = Color.parseColor("#323539");

    /**
     * 底部线的颜色   默认GRAY
     */
    private int                bottomLineColor    = Color.GRAY;

    /**
     * 分割线的颜色
     */
    private int                borderColor        = Color.GRAY;
    /**
     * 分割线的画笔
     */
    private Paint              borderPaint;
    /**
     * 分割线开始的坐标x
     */
    private int                divideLineWStartX;

    /**
     * 分割线的宽度  默认2
     */
    private int                divideLineWidth    = 2;
    /**
     * 竖直分割线的颜色
     */
    private int                divideLineColor    = Color.GRAY;
    private int                focusedColor       = Color.BLUE;
    private RectF              rectF              = new RectF();
    private RectF              focusedRecF        = new RectF();
    private int                psdType            = 0;
    private boolean            isPlaintext        = false;
    private final static int   psdType_weChat     = 0;
    private final static int   psdType_bottomLine = 1;

    private int                textSize           = 30;
    /**
     * 矩形边框的圆角
     */
    private int                rectAngle          = 0;
    /**
     * 竖直分割线的画笔
     */
    private Paint              divideLinePaint;
    /**
     * 圆的画笔
     */
    private Paint              circlePaint, textPaint;
    /**
     * 底部线的画笔
     */
    private Paint              bottomLinePaint;

    /**
     * 需要对比的密码  一般为上次输入的
     */
    private String             mComparePassword   = null;

    /**
     * 当前输入的位置索引
     */
    private int                position           = 0;

    private onPasswordListener mListener;

    public PayPwdInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        getAtt(attrs);
        initPaint();

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setCursorVisible(false);
        this.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxCount) });

    }

    private void getAtt(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PayPwdInputView);
        maxCount = typedArray.getInt(R.styleable.PayPwdInputView_maxCount, maxCount);
        circleColor = typedArray.getColor(R.styleable.PayPwdInputView_pwd_circleColor, circleColor);
        bottomLineColor = typedArray.getColor(R.styleable.PayPwdInputView_bottomLineColor,
            bottomLineColor);
        radius = typedArray.getDimensionPixelOffset(R.styleable.PayPwdInputView_radius, radius);

        divideLineWidth = typedArray
            .getDimensionPixelSize(R.styleable.PayPwdInputView_divideLineWidth, divideLineWidth);
        divideLineColor = typedArray.getColor(R.styleable.PayPwdInputView_divideLineColor,
            divideLineColor);
        psdType = typedArray.getInt(R.styleable.PayPwdInputView_pwdType, psdType);
        rectAngle = typedArray.getDimensionPixelOffset(R.styleable.PayPwdInputView_rectAngle,
            rectAngle);
        focusedColor = typedArray.getColor(R.styleable.PayPwdInputView_focusedColor, focusedColor);
        isPlaintext = typedArray.getBoolean(R.styleable.PayPwdInputView_isPlaintext, false);
        textColor = typedArray.getColor(R.styleable.PayPwdInputView_pwdTextColor, textColor);

        textSize = typedArray.getDimensionPixelSize(R.styleable.PayPwdInputView_pwdTextSize,
            textSize);
        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {

        circlePaint = getPaint(5, Paint.Style.FILL, circleColor);
        textPaint = getPaint(5, Paint.Style.FILL, textColor);
        bottomLinePaint = getPaint(2, Paint.Style.FILL, bottomLineColor);

        borderPaint = getPaint(3, Paint.Style.STROKE, borderColor);

        divideLinePaint = getPaint(divideLineWidth, Paint.Style.FILL, borderColor);

    }

    /**
     * 设置画笔
     *
     * @param strokeWidth 画笔宽度
     * @param style       画笔风格
     * @param color       画笔颜色
     * @return
     */
    private Paint getPaint(int strokeWidth, Paint.Style style, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setAntiAlias(true);

        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;

        divideLineWStartX = w / maxCount;

        startX = w / maxCount / 2;
        startY = h / 2;

        bottomLineLength = w / (maxCount + 2);

        rectF.set(0, 0, width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //不删除的画会默认绘制输入的文字
        //       super.onDraw(canvas);

        switch (psdType) {
            case psdType_weChat:
                drawWeChatBorder(canvas);
                drawItemFocused(canvas, position);
                break;
            case psdType_bottomLine:
                drawBottomBorder(canvas);
                break;
        }

        if (isPlaintext) {
            drawText(canvas);
        } else {
            drawPsdCircle(canvas);
        }

    }

    /**
     * 画微信支付密码的样式
     *
     * @param canvas
     */
    private void drawWeChatBorder(Canvas canvas) {

        canvas.drawRoundRect(rectF, rectAngle, rectAngle, borderPaint);

        for (int i = 0; i < maxCount - 1; i++) {
            canvas.drawLine((i + 1) * divideLineWStartX, 0, (i + 1) * divideLineWStartX, height,
                divideLinePaint);
        }

    }

    private void drawItemFocused(Canvas canvas, int position) {
        if (position > maxCount - 1) {
            return;
        }
        focusedRecF.set(position * divideLineWStartX, 0, (position + 1) * divideLineWStartX,
            height);
        canvas.drawRoundRect(focusedRecF, rectAngle, rectAngle,
            getPaint(3, Paint.Style.STROKE, focusedColor));
    }

    /**
     * 画底部显示的分割线
     *
     * @param canvas
     */
    private void drawBottomBorder(Canvas canvas) {

        for (int i = 0; i < maxCount; i++) {
            cX = startX + i * 2 * startX;
            canvas.drawLine(cX - bottomLineLength / 2, height, cX + bottomLineLength / 2, height,
                bottomLinePaint);
        }
    }

    /**
     * 画密码实心圆
     *
     * @param canvas
     */
    private void drawPsdCircle(Canvas canvas) {
        for (int i = 0; i < textLength; i++) {
            canvas.drawCircle(startX + i * 2 * startX, startY, radius, circlePaint);
        }
    }

    /**
     * 画明文字符
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        textPaint.setTextSize(textSize);

        if (mText != null) {
            char[] chars = mText.toCharArray();
            for (int i = 0; i < textLength; i++) {

                canvas.drawText(((int) chars[i] - CHAR_NUM_BASE_VALUE) + "",
                    startX + i * 2 * startX - textSize / 3, startY + textSize / 3, textPaint);
            }
        }

    }

    private static final int CHAR_NUM_BASE_VALUE = 48;
    private String           mText;

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.position = start + lengthAfter;
        mText = text.toString();
        textLength = text.toString().length();

        if (mComparePassword != null && mListener != null) {
            if (TextUtils.equals(mComparePassword, getPasswordString()) && textLength == maxCount) {
                mListener.onEqual(getPasswordString());
            } else {
                mListener.onDifference();
            }
        }
        if (mPwdLengthListener != null) {
            mPwdLengthListener.onLength(mText.trim());
        }
        invalidate();

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        //保证光标始终在最后
        if (selStart == selEnd) {
            setSelection(getText().length());
        }
    }

    /**
     * 获取输入的密码
     *
     * @return
     */
    public String getPasswordString() {
        return getText().toString().trim();
    }

    public void setComparePassword(String comparePassword, onPasswordListener listener) {
        mComparePassword = comparePassword;
        mListener = listener;
    }

    /**
     * 密码比较监听
     */
    public interface onPasswordListener {
        void onDifference();

        void onEqual(String psd);

    }

    /**
     * 密码输入长度回调
     */
    public interface PwdLengthListener {

        void onLength(String pwd);
    }

    public void setPwdLengthListener(PwdLengthListener listener) {

        this.mPwdLengthListener = listener;
    }

    private PwdLengthListener mPwdLengthListener;
}
