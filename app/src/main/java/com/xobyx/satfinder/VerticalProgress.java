package com.xobyx.satfinder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class VerticalProgress extends View {
    private int width;
    private int height;
    private int backgroundColor;
    private int foreStartColor;
    private int foreEndColor;
    private int progress;
    private int value;
    private Canvas canvas;
    private Paint main_paint;
    private Paint GradientPaint;
    private Paint Linespaint;

    public VerticalProgress(Context context) {
        this(context, null);
    }

    public VerticalProgress(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VerticalProgress(Context typedArray, AttributeSet attributeSet, int defStyleAttr) {
        super(typedArray, attributeSet, defStyleAttr);
        this.backgroundColor = 0xFFFFF100;
        this.foreStartColor = 0xffea5504;
        this.foreEndColor = 0x4000A0E9;
        this.progress = 100;
        int i = 0;
        this.value = 0;
        this.main_paint = new Paint(1);
        this.main_paint.setStyle(Paint.Style.FILL);
        this.main_paint.setColor(this.foreEndColor);
        this.Linespaint = new Paint(1);
        this.Linespaint.setStyle(Paint.Style.FILL);
        this.Linespaint.setStrokeWidth(4.0f);
        this.Linespaint.setColor(this.foreEndColor);
        TypedArray styledAttributes = typedArray.obtainStyledAttributes(attributeSet,R.styleable.VerticalProgress , defStyleAttr, 0);
        if(styledAttributes != null) {
            while(i < styledAttributes.getIndexCount()) {
                int index = styledAttributes.getIndex(i);
                if(index == 0) {
                    this.foreEndColor = styledAttributes.getColor(index, R.styleable.VerticalProgress_foreEndColor);
                }
                else if(index == 1) {
                    this.foreStartColor = styledAttributes.getColor(index,R.styleable.VerticalProgress_foreStartColor);
                }
                else if(index == 2) {
                    this.backgroundColor = styledAttributes.getColor(index,R.styleable.VerticalProgress_backgroundColor);
                }
                else if(index == 3) {
                    this.progress = styledAttributes.getInt(index, 100);
                }

                ++i;
            }
        }
    }

    @Override  // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        Rect v_rect = new Rect(0, this.height - this.value * this.height / 100, this.width, this.height);
        Rect m_rect = new Rect(0, 0, this.width, this.height);
        this.canvas.drawRect(m_rect, this.main_paint);
        this.canvas.drawRect(v_rect, this.GradientPaint);

        for(int i = 1; i < 10; ++i) {
            this.canvas.drawLine(0.0f, (float)(this.height / 10 * i)
                    ,((float)this.width), (float)(this.height / 10 * i)
                    ,this.Linespaint);
        }
    }

    @Override  // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.width = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getMode(widthMeasureSpec);
        this.height = View.MeasureSpec.getSize(heightMeasureSpec);
        View.MeasureSpec.getMode(heightMeasureSpec);

        this.GradientPaint = new Paint();
        this.GradientPaint.setColor(0xFF00FF00);
        LinearGradient gradient = new LinearGradient(0.0f, 0.0f, ((float)this.width), ((float)this.height), new int[]{this.backgroundColor, this.foreStartColor}, null, Shader.TileMode.CLAMP);
        this.GradientPaint.setShader(gradient);
    }

    public void setVal(int value) {
        this.value = value;
        this.invalidate();
    }
}

