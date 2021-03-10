package com.example.paint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class CanvasView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintToScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    private String selectedPaintTool, selectedShapeTool, selectedTool;
    public int selectedPaintColour;


    public CanvasView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        paintToScreen = new Paint();
        paintLine = new Paint();
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
        selectedPaintColour = Color.BLACK;
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

        if(selectedPaintTool.equals("Paint Brush") || selectedPaintTool.equals("Eraser"))
        {
            for(Integer key: pathMap.keySet())
            {
                canvas.drawPath(pathMap.get(key),paintLine);
            }
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

        if(selectedPaintTool.equals("Fill Bucket"))
        {
            floodFill(point, bitmap.getPixel(point.x, point.y), selectedPaintColour);
        }
    }

    private void touchStopped(int pointerID)
    {
        if(selectedPaintTool.equals("Paint Brush") || selectedPaintTool.equals("Eraser"))
        {
            Path path = pathMap.get(pointerID);
            bitmapCanvas.drawPath(path, paintLine);
            path.reset();
        }
    }



    private void touchMoved(MotionEvent event)
    {
        float newX;
        float newY;
        if(selectedTool.equals("paint"))
        {
            for(int i = 0; i < event.getPointerCount(); i++)
            {
                int pointerID = event.getPointerId(i);
                int pointerIndex = event.findPointerIndex(pointerID);
                if(pathMap.containsKey(pointerID))
                {
                    newX = event.getX(pointerIndex);
                    newY = event.getY(pointerIndex);


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
    }

    public void clearMap()
    {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void updatePaint(int size, int colour, int alpha)
    {
        selectedPaintColour = colour;
        if(selectedPaintTool.equals("Paint Brush"))
        {
            paintLine.setStrokeWidth(size);
            paintLine.setColor(colour);
            paintLine.setAlpha(alpha);
        }
        else if(selectedPaintTool.equals("Eraser"))
        {
            paintLine.setStrokeWidth(size);
            paintLine.setColor(Color.WHITE);
            paintLine.setAlpha(255);
        }
        else if(selectedPaintTool.equals("Spray Can"))
        {
            paintLine.setStrokeWidth(size/5);
            paintLine.setColor(colour);
            paintLine.setAlpha(alpha);
        }
    }

    public void setPaintTool(String tool)
    {
       selectedPaintTool = tool;
    }

    public String getSelectedTool()
    {
        return selectedTool;
    }

    public void setSelectedTool(String tool)
    {
        selectedTool = tool;
    }

    private void floodFill(Point pt, int targetColour, int replacementColour)
    {
        Queue<Point> q = new LinkedList<Point>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bitmap.getPixel(n.x, n.y) != targetColour)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bitmap.getPixel(w.x, w.y) == targetColour)) {
                bitmap.setPixel(w.x, w.y, replacementColour);
                if ((w.y > 0) && (bitmap.getPixel(w.x, w.y - 1) == targetColour))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < bitmap.getHeight() - 1)
                        && (bitmap.getPixel(w.x, w.y + 1) == targetColour))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bitmap.getWidth() - 1)
                    && (bitmap.getPixel(e.x, e.y) == targetColour)) {
                bitmap.setPixel(e.x, e.y, replacementColour);

                if ((e.y > 0) && (bitmap.getPixel(e.x, e.y - 1) == targetColour))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bitmap.getHeight() - 1)
                        && (bitmap.getPixel(e.x, e.y + 1) == targetColour))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }
    }

    private void sprayCan()
    {

    }
}