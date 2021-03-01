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
import android.view.MotionEvent;
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
        paintLine.setStyle(Paint.Style.STROKE);
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
        canvas.drawBitmap(bitmap, 0, 0, paintToScreen);

        for(Integer key: pathMap.keySet())
        {
            canvas.drawPath(pathMap.get(key),paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP)
        {
            touchBegin(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
        {
            touchStopped(event.getPointerId(actionIndex));
        }
        else
        {
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchBegin(float x, float y, int pointerID)
    {
        Path path;
        Point point;

        if(pathMap.containsKey(pointerID))
        {
            path = pathMap.get(pointerID);
            point = previousPointMap.get(pointerID);
        }
        else
        {
            path = new Path();
            pathMap.put(pointerID, path);
            point = new Point();
            previousPointMap.put(pointerID, point);
        }

        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;
    }

    private void touchStopped(int pointerID)
    {
        Path path = pathMap.get(pointerID);
        bitmapCanvas.drawPath(path, paintLine);
        path.reset();
    }



    private void touchMoved(MotionEvent event)
    {
        for(int i = 0; i < event.getPointerCount(); i++)
        {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if(pathMap.containsKey(pointerID))
            {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                float dx = Math.abs(newX - point.x);
                float dy = Math.abs(newY - point.y);

                if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
                {
                    path.quadTo(point.x, point.y, (newX + point.x)/2, (newY + point.y)/2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    public void clearMap()
    {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

}