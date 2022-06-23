package com.xobyx.satfinder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;

public class CompassView extends View {

    private int isLocked;
    private float signalBarSize;
    private Paint CirclePaint;
    private Paint CompassRingPaint;
    private Paint ArrowPaint;
    private Paint paint3;
    private Paint paint4;
    private Paint ArcSpliterPaint;
    private Paint ArcPaint;
    private TextPaint AnglesTextPaint;
    private TextPaint DirTextPaint;
    private TextPaint CenterTextPaint;
    private int vSize;
    private float center;
    private float rCenter;
    private float tickHeight;
    private float DescentAscent;
    private double satAngle;
    private double latitude;
    private double longitude;
    private double satLongitude;
    private float rotate;
    private double Skew;
    private double mElevation;
    private RectF rectF;
    private int quality;
    private int strength;
    private float rotateY;
    private String strAngle;

    public CompassView(Context context) {
        this(context, null, 0);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public CompassView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.satAngle = -1.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.satLongitude = 0.0;
        this.rotate = 0.0f;
        this.rotateY = 0.0f;
        this.Skew = -1.0;
        this.mElevation = -1.0;
        this.quality = 0;
        this.strength = 0;
        this.isLocked = 0;
        this.signalBarSize = 0.0f;

        this.initialize(context);

    }

    private void DrawAngleText(Canvas canvas, String arg4, float arg5) {
        canvas.drawText(arg4, -(arg5 / 2.0f), -this.rCenter + this.tickHeight + this.DescentAscent, this.AnglesTextPaint);
    }

    private void DrawDirText(Canvas canvas, String arg5, float arg6) {

        canvas.drawText(arg5, -(arg6 / 2.0f), -this.rCenter + this.tickHeight * 2.0f + this.DescentAscent, this.DirTextPaint);
    }

    private void initialize(Context context) {
        Paint v8 = new Paint(5);
        this.CirclePaint = v8;
        TypedValue j = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground, j, true);
        v8.setColor(j.data);
        this.CirclePaint.setStyle(Paint.Style.FILL);
        Paint v8_1 = new Paint(1);
        this.CompassRingPaint = v8_1;
        v8_1.setColor(0xFF90CAF9);
        this.CompassRingPaint.setStrokeWidth(3.0f);
        Paint v8_2 = new Paint(1);
        this.ArrowPaint = v8_2;
        v8_2.setColor(0xFFFF0000);
        this.ArrowPaint.setStrokeWidth(10.0f);
        TextPaint v8_3 = new TextPaint(1);
        this.AnglesTextPaint = v8_3;
        v8_3.setColor(-1);
        this.AnglesTextPaint.setTextSize(30.0f);
        TextPaint v8_4 = new TextPaint(1);
        this.DirTextPaint = v8_4;
        v8_4.setColor(-1);
        this.DirTextPaint.setTextSize(60.0f);
        Paint v8_5 = new Paint(1);
        this.paint3 = v8_5;
        v8_5.setStrokeWidth(20.0f);
        this.paint3.setStyle(Paint.Style.STROKE);
        this.paint3.setColor(0xFF888888);
        TextPaint v8_6 = new TextPaint(1);
        this.CenterTextPaint = v8_6;
        v8_6.setColor(-1);
        this.CenterTextPaint.setTextSize(50.0f);
        Paint v8_7 = new Paint(1);
        this.paint4 = v8_7;
        v8_7.setColor(0xFFFF9800); //lines+
        this.paint4.setStrokeWidth(4.0f);
        Paint v8_8 = new Paint(1);
        this.ArcSpliterPaint = v8_8;
        v8_8.setColor(j.data);//spliters
        this.ArcSpliterPaint.setStrokeWidth(10.0f);
        Paint v8_9 = new Paint(5);
        this.ArcPaint = v8_9;
        v8_9.setStrokeWidth(40.0f);
        this.ArcPaint.setStyle(Paint.Style.STROKE);
        this.ArcPaint.setColor(0xFF888888);
        this.strAngle = this.getContext().getResources().getString(R.string.strAngle);
    }

    public void setLocation(double p_longitude, double p_latitude) {
        this.longitude = p_longitude;
        this.latitude = p_latitude;
        Log.e("CompassView", "setLocation satLongitude " + this.satLongitude + " longitude " + this.longitude + " latitude " + this.latitude);
        double v9 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(this.satLongitude - this.longitude)) / Math.sin(Math.toRadians(this.latitude))));
        double v0 = this.latitude <= 0.0 ? 0.0 - v9 : 180.0 - v9;
        this.satAngle = v0;
        if (v0 < 0.0) {
            v0 += 360.0;
        }

        this.satAngle = v0;
        this.Skew = Math.toDegrees(Math.atan(Math.sin(Math.toRadians(this.satLongitude - this.longitude)) / Math.tan(Math.toRadians(this.latitude))));
        this.mElevation = Math.toDegrees(Math.atan((Math.cos(Math.toRadians(this.satLongitude - this.longitude)) * Math.cos(Math.toRadians(this.latitude)) - 0.1512) / Math.sqrt(1.0 - Math.pow(Math.cos(Math.toRadians(this.satLongitude - this.longitude)) * Math.cos(Math.toRadians(this.latitude)), 2.0))));
        Log.w("CompassView", "P " + this.Skew + "E " + this.mElevation + " satAngle " + this.satAngle);
        this.invalidate();
    }

    public void setQuality(int quality) {
        this.quality = quality;
        isLocked = this.quality <= 0 ? 0 : 1;

        this.invalidate();
    }

    public void setStrength(int strength) {
        this.strength = strength;
        this.invalidate();
    }

    public void setValues(int Quality, int Strength, int Locked) {
        this.quality = Quality;
        this.strength = Strength;
        this.isLocked = Locked;
        this.invalidate();
    }

    public boolean getLock() {
        return this.isLocked == 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float line_len;
        double v;
        String dir;
        super.onDraw(canvas);
        canvas.drawCircle(this.center, this.center, this.center, this.CirclePaint);
        canvas.drawLine(((float) (this.vSize / 4)), ((float) (this.vSize / 2)), ((float) (this.vSize / 4 * 3)), ((float) (this.vSize / 2)), this.paint4);
        canvas.drawLine(((float) (this.vSize / 2)), ((float) (this.vSize / 4)), ((float) (this.vSize / 2)), ((float) (this.vSize / 4 * 3)), this.paint4);
        this.ArcPaint.setColor(Color.rgb(0xF0, 0xFF, 0xFF));
        canvas.drawArc(this.rectF, 90.0f, 180.0f, false, this.ArcPaint);
        canvas.drawArc(this.rectF, 90.0f, -180.0f, false, this.ArcPaint);
        int d_angle = 0;

        this.ArcPaint.setColor(this.isLocked == 1 ? 0xFF00FF00 : Color.rgb(0xFF, 97, 0));
        canvas.drawArc(this.rectF, 90.0f, ((float) (this.quality * 180 / 100)), false, this.ArcPaint);

        ArcPaint.setColor(this.isLocked == 1 ? 0xFFFFFF00 : Color.rgb(0x80, 42, 42));
        canvas.drawArc(this.rectF, 90.0f, ((float) (this.strength * -180 / 100)), false, this.ArcPaint);

        for (int i = 0; i < 360; i += 180) {//36
            canvas.save();
            canvas.translate(this.center, this.center);
            canvas.rotate(((float) i));
            canvas.drawLine(0.0f, -this.center, 0.0f, this.signalBarSize + -this.center, this.ArcSpliterPaint);
            canvas.restore();
        }

        if (this.satAngle > 0.0) {
            this.CenterTextPaint.setTextSize(this.tickHeight * 1.5f);
            String strElev = this.getResources().getString(R.string.strElevation);  // string:strElevation "Elevation"
            float a = this.CenterTextPaint.measureText(strElev) + this.CenterTextPaint.measureText("A");

            canvas.drawText(strElev, a >= ((float) (this.vSize / 4)) ?
                            ((float) (this.vSize / 4)) - (a - ((float) (this.vSize / 4))) :
                            ((float) (this.vSize / 4)) - ((float) (this.vSize / 8)),
                    this.center - this.tickHeight * 1.5f / 2.0f,
                    this.CenterTextPaint);
            this.CenterTextPaint.setTextSize(this.tickHeight * 2f);
            
            String strY = String.format("%d%s", (int) rotateY, strAngle);
            float ab = this.CenterTextPaint.measureText(strY);

            canvas.drawText(strY,

                    (float) (3f * vSize / 8f) - (float) (ab / 2f)
                    , this.center - this.tickHeight * 1.5f / 2.0f - 50f,
                    this.CenterTextPaint);
            this.CenterTextPaint.setTextSize(this.tickHeight * 1.5f);
            String strSkew = this.getResources().getString(R.string.strSkew);  // string:strPolagizing "Skew"
            float a1 = this.CenterTextPaint.measureText(strSkew) + this.CenterTextPaint.measureText("A");
            canvas.drawText(strSkew, a1 >= ((float) (this.vSize / 4)) ? ((float) (this.vSize / 4)) - (a1 - ((float) (this.vSize / 4))) : ((float) (this.vSize / 4)), this.center + this.tickHeight * 1.5f * 1.3f, this.CenterTextPaint);
            this.CenterTextPaint.setTextSize(this.tickHeight * 2.0f);
            canvas.drawText(String.format("%.2f", ((double) this.mElevation)), this.rCenter + 70.0f, this.center - 30.0f, this.CenterTextPaint);
            if (this.Skew > 0.0) {
                dir = this.getResources().getString(R.string.strL);  // string:strLeft "L"
                v = this.Skew;
            } else {
                dir = this.getResources().getString(R.string.strR);  // string:strRight "R"
                v = -this.Skew;
            }

            canvas.drawText(String.format("%s%.2f", dir, ((double) v)), this.rCenter + 70.0f, this.center + 70.0f, this.CenterTextPaint);
        }

        canvas.rotate(this.rotate, ((float) (this.getWidth() / 2)), ((float) (this.getHeight() / 2)));
        int[] dir_text_ids = {R.string.strN, R.string.strE, R.string.strS, R.string.strW};
        while (d_angle < 360) {
            canvas.save();
            canvas.translate(this.center, this.center);
            canvas.rotate(((float) d_angle));
            if (d_angle % 90 == 0) {
                Rect rect = new Rect();

                String DirText = this.getResources().getString(dir_text_ids[d_angle / 90]);

                this.DirTextPaint.getTextBounds(DirText, 0, DirText.length(), rect);
                this.DrawDirText(canvas, DirText, this.DirTextPaint.measureText(DirText));
            } else {
                if (d_angle % 5 == 0) {
                    line_len = this.tickHeight * 1.5f;
                } else {
                    line_len = this.tickHeight;
                }

                canvas.drawLine(0.0f, -this.rCenter, 0.0f, line_len + -this.rCenter, this.CompassRingPaint);
            }

            if (d_angle % 15 == 0 && d_angle % 90 != 0) {
                String angle = Integer.toString(d_angle);
                this.DrawAngleText(canvas, angle, this.AnglesTextPaint.measureText(angle));
            }

            canvas.restore();
            ++d_angle;
        }

        if (this.satAngle == -1.0) {
            return;
        }

        canvas.save();
        canvas.translate(this.center, this.center);
        canvas.rotate(((float) this.satAngle));
        canvas.drawLine(0.0f, -this.rCenter, 0.0f, 0.0f, this.ArrowPaint); //main line
        canvas.drawLine(3.0f, -this.rCenter, -20.0f, -this.rCenter + 30.0f, this.ArrowPaint);
        canvas.drawLine(-3.0f, -this.rCenter, 20.0f, -this.rCenter + 30.0f, this.ArrowPaint);
        ArrowPaint.setTextSize(25f);
        float m = ArrowPaint.measureText(getContext().getString(R.string.strAzimuth));

        canvas.rotate(-90);
        canvas.drawText(getContext().getString(R.string.strAzimuth), this.rCenter / 2f - m / 2f, -15, ArrowPaint);
        canvas.restore();
    }

    @Override  // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            this.vSize = Math.min(w, h);
            this.center = (float) (Math.min(w, h) / 2);
            float mDb = 43.0f * (float) (Math.min(w, h) / 2) / 48.0f;
            this.rCenter = mDb;
            this.signalBarSize = (float) (Math.min(w, h) / 2) * 2.0f / 24.0f;
            this.tickHeight = mDb * 0.083333f;
            Log.e("CompassView", "tickHeight " + this.tickHeight);
            this.AnglesTextPaint.setTextSize(this.tickHeight);
            this.DirTextPaint.setTextSize(this.tickHeight * 2.0f);
            this.DescentAscent = this.AnglesTextPaint.descent() - this.AnglesTextPaint.ascent();
            Log.e("CompassView", "vSize " + this.vSize + " signalBarSize " + this.signalBarSize);
            float v4 = (float) Math.round(this.signalBarSize / 2.0f);
            this.rectF = new RectF(v4, v4, ((float) (this.vSize - Math.round(this.signalBarSize / 2.0f))), ((float) (this.vSize - Math.round(this.signalBarSize / 2.0f))));
            this.ArcPaint.setStrokeWidth(this.signalBarSize);
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.width = this.vSize;
            params.height = this.vSize;
            this.setLayoutParams(params);
        }


    }

    @Override  // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getMode(widthMeasureSpec);
        int size1 = View.MeasureSpec.getSize(heightMeasureSpec);
        View.MeasureSpec.getMode(heightMeasureSpec);
        onSizeChanged(size, size1, size, size1);

    }

    public void setNavPos(float rotX, float rotY) {
        this.rotate = rotX * -1.0f;
        this.rotateY = rotY * -1.0f;

        this.invalidate();
    }

   /* public void setSatPosition(double satLongitude) {
        this.satLongitude = satLongitude;
        Log.e("CompassView", "satLongitude " + this.satLongitude);
        if(this.longitude == 0.0 && this.latitude == 0.0) {
            return;
        }

        double v2 = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(satLongitude - this.longitude)) / Math.sin(Math.toRadians(this.latitude))));
        double sat_angle = this.latitude <= 0.0 ? 0.0 - v2 : 180.0 - v2;
        this.satAngle = sat_angle;
        if(sat_angle < 0.0) {
            sat_angle += 360.0;
        }

        this.satAngle = sat_angle;
        this.Skew = Math.toDegrees(Math.atan(Math.sin(Math.toRadians(satLongitude - this.longitude)) / Math.tan(Math.toRadians(this.latitude))));
        this.Elevation = Math.toDegrees(Math.atan((Math.cos(Math.toRadians(satLongitude - this.longitude)) * Math.cos(Math.toRadians(this.latitude)) - 0.1512) / Math.sqrt(1.0 - Math.pow(Math.cos(Math.toRadians(satLongitude - this.longitude)) * Math.cos(Math.toRadians(this.latitude)), 2.0))));
        Log.w("CompassView", "P " + this.Skew + "E " + this.Elevation + " satAngle " + this.satAngle);
        this.invalidate();
    }*/

    public void setSatAngle(double satAngle) {
        this.satAngle = satAngle;
        this.invalidate();
    }

    public void setSkew(double skew) {
        this.Skew = skew;
        invalidate();
    }

    public void setmElevation(double mElevation) {
        this.mElevation = mElevation;
        invalidate();
    }

    public void setmElevationRot(float rotY) {

    }
}

