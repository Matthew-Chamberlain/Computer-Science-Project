package com.example.paint;

import androidx.annotation.Nullable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;


import java.util.HashMap;

public class CanvasView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintToScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;


    public CanvasView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        paintToScreen = new Paint();
        paintLine = new Paint();
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
        setup();
    }

    private void setup()
    {
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(7);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, 78, paintLine);
    }

}