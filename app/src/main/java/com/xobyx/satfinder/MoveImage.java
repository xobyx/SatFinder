package com.xobyx.satfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.view.MotionEvent.*;


public class MoveImage extends AppCompatImageView {


    private static int width = 720;
    private static int height = 0x500;
    private final TextPaint textPaint;
    private final Drawable oval;
    private final int l;
    private final int m;
    private final Rect vm = new Rect();
    boolean connect;
    private int touchX;
    private int touchY;
    private boolean isMoveTouch;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private boolean Rtl;
    private int ScreenDensity;
    private SharedPreferences sharedPreferences;
    private int Rssi;

    public MoveImage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.touchX = 0;
        this.touchY = 0;
        this.isMoveTouch = false;
        this.ScreenDensity = 0;
        oval = context.getDrawable(R.drawable.blue_oval);
        textPaint = new TextPaint(ANTI_ALIAS_FLAG);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        this.l = 0x30;
        this.m = 0x30;
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        MoveImage.height = displayMetrics.heightPixels;
        MoveImage.width = displayMetrics.widthPixels;
        this.ScreenDensity = DensityUtil.getScreenDensity(context, 150.0f);
    }

    public void reDraw() {
        this.layout(this.left, this.top, this.right, this.bottom);
        this.invalidate();
    }

    private void SaveParameters() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt("left", this.left);
        editor.putInt("right", this.right);
        editor.putInt("top", this.top);
        editor.putInt("bottom", this.bottom);
        editor.apply();
    }

    public void setRssi(int in) {
        if(in<=60) {
            oval.setTint(Color.RED);

        }
        else {
            oval.setTint(0xFF2196F3);
        }
        this.setImageDrawable(oval);
        Rssi = in;
        this.invalidate();
    }

    @Override  // android.widget.ImageView
    protected void onDraw(Canvas canvas) {
        this.layout(this.left, this.top, this.right, this.bottom);
        super.onDraw(canvas);

        if (connect) {
            String v = Rssi + " %";
            textPaint.getTextBounds(v, 0, v.length(), vm);
            canvas.drawText(v, (float) getWidth() / 2 - vm.centerX(), (float) getHeight() / 2 - vm.centerY(), textPaint);
        }
        this.invalidate();
    }

    @Override  // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override  // android.widget.ImageView
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int c;
        int i;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.Rtl) {
            i = DensityUtil.getScreenDensity(this.getContext(), ((float) this.l)) + 20;
            c = 20;
        } else {
            c = MoveImage.width - DensityUtil.getScreenDensity(this.getContext(), ((float) this.l)) - 20;
            i = MoveImage.width - 20;
        }

        int density = this.ScreenDensity;
        int screen_d = DensityUtil.getScreenDensity(this.getContext(), ((float) this.m)) + density;
        this.left = this.sharedPreferences.getInt("left", c);
        this.right = this.sharedPreferences.getInt("right", i);
        this.top = this.sharedPreferences.getInt("top", density);
        this.bottom = this.sharedPreferences.getInt("bottom", screen_d);
        if (this.right > MoveImage.width || this.bottom > MoveImage.height || this.right - DensityUtil.getScreenDensity(this.getContext(), ((float) this.l)) != this.left || this.bottom - DensityUtil.getScreenDensity(this.getContext(), ((float) this.m)) != this.top) {
            this.left = c;
            this.right = i;
            this.top = density;
            this.bottom = screen_d;
            this.SaveParameters();
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override  // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == ACTION_DOWN) {
            this.touchX = (int) motionEvent.getRawX();
            this.touchY = (int) motionEvent.getRawY();
            this.isMoveTouch = false;
        } else {
            if (action == ACTION_UP) {
                this.setPressed(false);
                if (!this.isMoveTouch) {
                    this.performClick();
                    return true;
                }

                this.SaveParameters();
                return true;
            }

            if (action == ACTION_MOVE) {
                int i = ((int) motionEvent.getRawX()) - this.touchX;
                int i1 = ((int) motionEvent.getRawY()) - this.touchY;
                if (Math.abs(i) > 20 || Math.abs(i1) > 20) {
                    this.isMoveTouch = true;
                    this.left = this.getLeft() + i;
                    this.top = this.getTop() + i1;
                    this.right = this.getRight() + i;
                    this.bottom = this.getBottom() + i1;
                    if (this.left < 0) {
                        this.left = 0;
                        this.right = this.left + this.getWidth();
                    }

                    if (this.right > MoveImage.width) {
                        this.right = MoveImage.width;
                        this.left = this.right - this.getWidth();
                    }

                    if (this.top < 0) {
                        this.top = 0;
                        this.bottom = this.top + this.getHeight();
                    }

                    if (this.bottom > MoveImage.height) {
                        this.bottom = MoveImage.height;
                        this.top = this.bottom - this.getHeight();
                    }

                    this.layout(this.left, this.top, this.right, this.bottom);
                    this.touchX = (int) motionEvent.getRawX();
                    this.touchY = (int) motionEvent.getRawY();
                    return true;


                }
            } else if (action == ACTION_CANCEL) {
                this.setPressed(false);
                return true;
            }
        }

        return true;
    }

    public void setDefaultPosition(boolean pRtl) {
        if (pRtl) {
            this.left = 20;
            this.right = DensityUtil.getScreenDensity(this.getContext(), ((float) this.l)) + 20;
        } else {
            this.left = MoveImage.width - DensityUtil.getScreenDensity(this.getContext(), ((float) this.l)) - 20;
            this.right = MoveImage.width - 20;
        }

        this.Rtl = pRtl;
        this.top = this.ScreenDensity;
        this.bottom = this.ScreenDensity + DensityUtil.getScreenDensity(this.getContext(), ((float) this.m));
        this.SaveParameters();
    }

    public void setRtl(boolean rtl) {
        this.Rtl = rtl;
    }

    public void setSharedPreference(SharedPreferences preferences) {
        this.sharedPreferences = preferences;
    }
}

