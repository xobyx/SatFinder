package com.xobyx.satfinder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import java.util.Locale;

public class Compass extends FrameLayout {
    private final int Color_cross;
    private final Paint paint61_center_text;
    public int mQuality;
    private float RotationX;
    final String name;
    private int D;
    private int E;
    private int F;
    private int G;
    private int H;
    private int I;
    private int J;
    private int K;
    private float[][] floats;
    private boolean mlocale;
    private final float normal_scale_line;
    private final float long_scale_line;
    private final float strength_circle_length;
    private Canvas canvas;
    private Context context;
    private int mSize;
    private int mSizeD2I;
    private int mSizeD2IB;
    private float mSizeD2;
    private float j;
    private float k;
    private Paint paint1_circ;
    private Paint paint2_scale_line;
    private Paint paint3_compass_scale_line;
    private Paint paint4_scale_text;
    private Paint paint5_direct_text;
    private Paint paint6_center_text;
    private float r;
    private Paint paint7_azimuth;
    private Paint paint8_azimuth_text;
    private Bitmap arrow_bitmap;
    public boolean isAzimuth;
    public float mAzimuth;
    public float mElevation;
    public float mSkew;
    public int mStrength;

    public Compass(Context context) {
        this(context, null);
    }

    public Compass(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Compass(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.Compass,i,0);
        this.normal_scale_line = this.getResources().getDimension(R.dimen.compass_normal_scale_line);   // dimen:compass_normal_scale_line
        this.long_scale_line = this.getResources().getDimension(R.dimen.compass_long_scale_line);   // dimen:compass_long_scale_line
        this.strength_circle_length = this.getResources().getDimension(R.dimen.compass_strength_circle_length);   // dimen:compass_strength_circle_length
        this.isAzimuth = false;
        this.mAzimuth = 0.0f;
        this.mElevation = 0.0f;
        this.mSkew = 0.0f;
        this.mStrength = 0;
        this.mQuality = 0;
        this.RotationX = 0.0f;
        this.name = "compass";
        this.floats = new float[10][4];
        this.mlocale = false;
        Log.i("compass", "onCompass create");
        this.context = context;
        this.paint1_circ = new Paint(1);
        this.paint1_circ.setStyle(Paint.Style.STROKE);
        this.paint1_circ.setStrokeWidth(this.strength_circle_length);
        this.paint1_circ.setColor(typedArray.getColor(R.styleable.Compass_compass_circle,context.getResources().getColor(R.color.compass_circle)));   // color:compass_circle
        this.paint2_scale_line = new Paint(1);
        this.paint2_scale_line.setStyle(Paint.Style.FILL);
        this.paint2_scale_line.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_scale_line_stroke));   // dimen:compass_scale_line_stroke
        this.paint2_scale_line.setColor(typedArray.getColor(R.styleable.Compass_compass_scale_line,context.getResources().getColor(R.color.compass_scale_line)));   // color:compass_scale_line
        this.paint3_compass_scale_line = new Paint(1);
        this.paint3_compass_scale_line.setStyle(Paint.Style.FILL);
        this.paint3_compass_scale_line.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_scale_bold_stroke));   // dimen:compass_scale_bold_stroke
        this.paint3_compass_scale_line.setColor(typedArray.getColor(R.styleable.Compass_compass_scale_line,context.getResources().getColor(R.color.compass_scale_line)));   // color:compass_scale_line
        this.paint4_scale_text = new Paint(1);
        this.paint4_scale_text.setStyle(Paint.Style.FILL);
        this.paint4_scale_text.setTextSize(this.getResources().getDimension(R.dimen.compass_scale_text_size));   // dimen:compass_scale_text_size
        this.paint4_scale_text.setColor(typedArray.getColor(R.styleable.Compass_compass_scale_text,context.getResources().getColor(R.color.compass_scale_text)));   // color:compass_scale_text
        this.paint5_direct_text = new Paint(1);
        this.paint5_direct_text.setStyle(Paint.Style.FILL);
        this.paint5_direct_text.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_direct_text_stroke));   // dimen:compass_direct_text_stroke
        this.paint5_direct_text.setTextSize(this.getResources().getDimension(R.dimen.compass_direct_text_size));   // dimen:compass_direct_text_size
        this.paint5_direct_text.setColor(typedArray.getColor(R.styleable.Compass_compass_direction_text,context.getResources().getColor(R.color.compass_direction_text)));   // color:compass_direction_text
        Color_cross =    typedArray.getColor(R.styleable.Compass_compass_cross, context.getResources().getColor(R.color.compass_cross));
        this.paint61_center_text = new Paint(1);
        this.paint61_center_text.setStyle(Paint.Style.FILL);
        this.paint61_center_text.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_cross_stroke));   // dimen:compass_cross_stroke
        this.paint61_center_text.setTextSize(this.getResources().getDimension(R.dimen.compass_center_text_size));   // dimen:compass_center_text_size
        this.paint61_center_text.setColor(Color_cross);
        this.paint6_center_text = new Paint(1);
        this.paint6_center_text.setStyle(Paint.Style.FILL);
        this.paint6_center_text.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_cross_stroke));   // dimen:compass_cross_stroke
        this.paint6_center_text.setTextSize(this.getResources().getDimension(R.dimen.compass_center_text_size));   // dimen:compass_center_text_size
        this.paint6_center_text.setColor(typedArray.getColor(R.styleable.Compass_compass_cross_text,context.getResources().getColor(R.color.compass_cross)));   // color:compass_cross
        this.paint7_azimuth = new Paint(1);
        this.paint7_azimuth.setStyle(Paint.Style.FILL);
        this.paint7_azimuth.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_azimuth_line_stroke));   // dimen:compass_azimuth_line_stroke
        this.paint7_azimuth.setAlpha(this.getResources().getInteger(R.integer.compass_azimuth_alpha));   // integer:compass_azimuth_alpha
        this.paint7_azimuth.setColor(typedArray.getColor(R.styleable.Compass_compass_azimuth,context.getResources().getColor(R.color.compass_azimuth)));   // color:compass_azimuth
        this.paint8_azimuth_text = new Paint(1);
        this.paint8_azimuth_text.setStyle(Paint.Style.FILL);
        this.paint8_azimuth_text.setStrokeWidth(this.getResources().getDimension(R.dimen.compass_azimuth_text_stroke));   // dimen:compass_azimuth_text_stroke
        this.paint8_azimuth_text.setTextSize(this.getResources().getDimension(R.dimen.compass_azimuth_text_size));   // dimen:compass_azimuth_text_size
        this.paint8_azimuth_text.setAlpha(this.getResources().getInteger(R.integer.compass_azimuth_alpha));   // integer:compass_azimuth_alpha
        this.paint8_azimuth_text.setColor(typedArray.getColor(R.styleable.Compass_compass_azimuth_text,context.getResources().getColor(R.color.compass_azimuth_text)));   // color:compass_azimuth_text
        this.mlocale = Locale.getDefault().equals(new Locale("ar"));
        this.arrow_bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_compass_arrow);  // drawable:ic_compass_arrow
        Log.i("compass", "NORMAL_SCALE_LINE_LENGTH" + this.normal_scale_line);
    }

    private double[] CalcFloat(float arg8, float arg9, double arg10, double arg12) {
        double[] v0 = new double[2];
        double v3 = Math.cos(arg10) * (double) arg8 - Math.sin(arg10) * (double) arg9;
        double v1_1 = (double) arg8 * Math.sin(arg10) + (double) arg9 * Math.cos(arg10);
        double v8_1 = Math.sqrt(v3 * v3 + v1_1 * v1_1);
        v0[0] = v3 / v8_1 * arg12;
        v0[1] = v1_1 / v8_1 * arg12;
        return v0;
    }

    public int getDensity(float arg2) {
        return (int)(arg2 * this.getResources().getDisplayMetrics().density + 0.5f);
    }

    public void ReLayout() {
        Rect v0 = new Rect();
        Rect v1 = new Rect();
        Rect v2 = new Rect();
        Rect v3 = new Rect();
        String v4 = this.getResources().getString(R.string.strElevation);   // string:strElevation "Elevation"
        String v5 = this.getResources().getString(R.string.strSkew);   // string:strSkew "Skew"
        String v6 = this.getResources().getString(R.string.strAngle);   // string:strAngle "°"
        String v7 = this.mElevation + v6;
        String v6_1 = this.mSkew <= 0.0f ? this.getResources().getString(R.string.strR ) + -this.mSkew + v6 : this.getResources().getString(R.string.strR) + this.mSkew + v6;    // string:strR "R"
        this.paint6_center_text.getTextBounds(v4, 0, v4.length(), v0);
        this.paint6_center_text.getTextBounds(v5, 0, v5.length(), v1);
        this.paint6_center_text.getTextBounds(v7, 0, v7.length(), v2);
        this.paint6_center_text.getTextBounds(v6_1, 0, v6_1.length(), v3);
        if(this.mlocale) {
            this.D = this.mSizeD2I + Math.max(v0.width(), v1.width()) - this.getDensity(20.0f);
            this.E = this.mSizeD2IB - this.getDensity(5.0f);
            this.F = this.D;
            this.G = this.mSizeD2IB + v1.height() + this.getDensity(5.0f);
            this.H = this.mSizeD2I - v2.width() - this.getDensity(25.0f);
            this.J = this.H;
        }
        else {
            this.D = this.mSizeD2I - Math.max(v0.width(), v1.width()) - this.getDensity(5.0f);
            this.E = this.mSizeD2IB - this.getDensity(5.0f);
            this.F = this.D;
            this.G = this.mSizeD2IB + v0.height() + this.getDensity(5.0f);
            this.H = this.mSizeD2I + this.getDensity(5.0f);
            this.J = this.mSizeD2I + this.getDensity(5.0f);
        }

        this.I = this.E;
        this.K = this.G;
        this.k = this.j - this.long_scale_line;

        for(int i = 0; i < 10; ++i) {
            double v5_1 = (double)(((float)i) * 36.0f);
            this.floats[i][0] = (float)(((double)this.mSizeD2I) + Math.sin(Math.toRadians(v5_1)) * ((double)this.mSizeD2));
            this.floats[i][1] = (float)(((double)this.mSizeD2IB) + Math.cos(Math.toRadians(v5_1)) * ((double)this.mSizeD2));
            this.floats[i][2] = (float)(((double)this.mSizeD2I) + Math.sin(Math.toRadians(v5_1)) * ((double)this.j));
            this.floats[i][3] = (float)(((double)this.mSizeD2IB) + Math.cos(Math.toRadians(v5_1)) * ((double)this.j));
        }
    }

    void DrawAzimuthLine() {
        float d2I = (float)this.mSizeD2I;
        float d2IB = (float)this.mSizeD2IB;
        this.canvas.save();
        this.canvas.rotate(-this.RotationX, ((float)this.mSizeD2I), ((float)this.mSizeD2IB));
        float v = d2I + ((float)Math.sin(((double)this.mAzimuth) * 3.141593 / 180.0)) * this.k;
        float g = d2IB - this.k * ((float)Math.cos(((double)this.mAzimuth) * 3.141593 / 180.0));
        float m = (float)this.getDensity(15.0f);
        this.canvas.drawLine(d2I, d2IB, v, g, this.paint7_azimuth);
        double v10 = Math.atan(0.4375);
        float t = v - d2I;
        float z = g - d2IB;
        double[] calcFloat = this.CalcFloat(t, z, v10, (double)m);
        double[] calcFloat1 = this.CalcFloat(t, z, -v10, (double)m);
        int c = (int)((double)v - calcFloat[0]);
        int f = (int)((double)g - calcFloat[1]);
        int h = (int)((double)v - calcFloat1[0]);
        int k = (int)((double)g - calcFloat1[1]);
        Path path = new Path();
        path.moveTo(v, g);
        path.lineTo((float)c, (float)f);
        path.lineTo((float)h, (float)k);
        path.close();
        this.canvas.drawLine((float)c, (float)f, v, g, this.paint7_azimuth);
        this.canvas.drawLine((float)h, (float)k, v, g, this.paint7_azimuth);
        this.canvas.restore();
        this.canvas.save();
        String text = this.getResources().getString(R.string.strAzimuth);   // string:strAzimuth "Azimuth"
        Rect rect = new Rect();
        this.paint8_azimuth_text.getTextBounds(text, 0, text.length(), rect);
        float tg = ((float)this.mSizeD2I) + this.k / 2.0f - ((float)(rect.width() / 2));
        float tl = (float)(this.mSizeD2IB - 10);
        this.canvas.rotate(this.mAzimuth - this.RotationX - 90.0f, ((float)this.mSizeD2I), ((float)this.mSizeD2IB));
        this.canvas.drawText(text, tg, tl, this.paint8_azimuth_text);
        this.canvas.restore();
    }

    private void DrawAzimuthText() {
        this.canvas.drawText(this.getResources().getString(R.string.strElevation), ((float)this.D), ((float)this.E), this.paint6_center_text);   // string:strElevation "Elevation"
        this.canvas.drawText(this.getResources().getString(R.string.strSkew), ((float)this.F), ((float)this.G), this.paint6_center_text);   // string:strSkew "Skew"
        String s = this.getResources().getString(R.string.strAngle);   // string:strAngle "°"
        String elev = this.mElevation + s;
        String skew = this.mSkew <= 0.0f ? this.getResources().getString(R.string.strR) + -this.mSkew + s : this.getResources().getString(R.string.strL) + this.mSkew + s;  // string:strR "R"
        this.canvas.drawText(elev, ((float)this.H), ((float)this.I), this.paint6_center_text);
        this.canvas.drawText(skew, ((float)this.J), ((float)this.K), this.paint6_center_text);
    }

    private void DrawCompassScale() {
        this.canvas.save();
        Rect rect = new Rect();
        int[] dir = {R.string.strN, R.string.strE, R.string.strS, R.string.strW};  // string:strN "N"
        float v = this.mSizeD2 - this.j;
        float d2I = (float)this.mSizeD2I;
        int bitmapWidth = this.arrow_bitmap.getWidth();
        this.arrow_bitmap.getHeight();
        Paint paint = new Paint();
        this.canvas.drawBitmap(this.arrow_bitmap, d2I - ((float)(bitmapWidth / 2)), v - this.normal_scale_line, paint);
        this.canvas.rotate(-this.RotationX, ((float)this.mSizeD2I), ((float)this.mSizeD2IB));

        for(int i = 0; i < 360; ++i) {
            int e = i % 30;
            if(e == 0) {
                this.canvas.drawLine(d2I, v, d2I, v + this.long_scale_line, this.paint3_compass_scale_line);
            }
            else if(i % 2 == 0) {
                this.canvas.drawLine(d2I, v, d2I, v + this.normal_scale_line, this.paint2_scale_line);
            }

            if(i == 0 || i == 90 || i == 180 || i == 270) {
                String v5_1 = this.context.getResources().getString(dir[i / 90]);
                this.paint5_direct_text.getTextBounds(v5_1, 0, v5_1.length(), rect);
                float v6_2 = (float)(this.mSizeD2I - rect.width() / 2);
                float v4_2 = this.getResources().getDimension(R.dimen.compass_text_circle_length);   // dimen:compass_text_circle_length
                float v7_1 = this.mSizeD2 - this.j - v4_2 + ((float)rect.height());
                this.canvas.drawText(v5_1, v6_2, v7_1, this.paint5_direct_text);
            }
            else if(e == 0) {
                String v5 = Integer.toString(i);
                this.paint4_scale_text.getTextBounds(v5, 0, v5.length(), rect);
                float v6_1 = (float)(this.mSizeD2I - rect.width() / 2);
                float v4_1 = this.getResources().getDimension(R.dimen.compass_text_circle_length);   // dimen:compass_text_circle_length
                float v7 = this.mSizeD2 - this.j - v4_1 + ((float)rect.height());
                this.canvas.drawText(v5, v6_1, v7, this.paint4_scale_text);
            }

            this.canvas.rotate(1.0f, ((float)this.mSizeD2I), ((float)this.mSizeD2IB));
        }

        this.canvas.restore();
    }

    public float getVal() {
        return this.RotationX;
    }

    @Override  // android.view.View
    public void invalidate() {
        super.invalidate();
    }

    @Override  // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        this.canvas.drawLine(((float)this.mSizeD2I) - this.r / 2.0f, ((float)this.mSizeD2IB), ((float)this.mSizeD2I) + this.r / 2.0f, ((float)this.mSizeD2IB), this.paint61_center_text);
        this.canvas.drawLine(((float)this.mSizeD2I), ((float)this.mSizeD2IB) - this.r / 2.0f, ((float)this.mSizeD2I), ((float)this.mSizeD2IB) + this.r / 2.0f, this.paint61_center_text);
        this.DrawCompassScale();
        if(this.isAzimuth) {
            this.DrawAzimuthLine();
            this.DrawAzimuthText();
        }
    }

    @Override  // android.widget.FrameLayout
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size_w = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode_w = View.MeasureSpec.getMode(widthMeasureSpec);
        int size_h = View.MeasureSpec.getSize(heightMeasureSpec);
        int mode_h = View.MeasureSpec.getMode(heightMeasureSpec);
        this.mSize = Math.min(size_w, size_h);
        if(mode_w == 0) {
            this.mSize = size_h;
        }
        else if(mode_h == 0) {
            this.mSize = size_w;
        }

        this.mSizeD2 = (float)(this.mSize / 2);
        this.j = this.mSizeD2 - this.strength_circle_length;
        this.r = (this.j - ((float)DensityUtil.getScreenDensity(this.context, 15.0f))) * 2.0f;
        this.mSizeD2I = this.mSize / 2;
        this.mSizeD2IB = this.mSize / 2;
        this.ReLayout();
        this.setMeasuredDimension(this.mSize, this.mSize);
    }

    public void setAzimuth(float azimuth) {
        this.mAzimuth = azimuth;
        this.isAzimuth = true;
        this.invalidate();
    }

    @Override  // android.view.View
    public void setElevation(float arg1) {
        this.mElevation = arg1;
        this.invalidate();
    }

    public void setQuality(int arg1) {
        this.mQuality = arg1;
    }

    public void setSkew(float arg1) {
        this.mSkew = arg1;
    }

    public void setStrength(int arg1) {
        this.mStrength = arg1;
    }

    public void SetRotation(float v) {
        this.RotationX = v;
        this.invalidate();
    }
}

