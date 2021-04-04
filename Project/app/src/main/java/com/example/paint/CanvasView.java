package com.example.paint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CanvasView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintToScreen;
    private Paint paintLine, textPaint, shapePaint;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<Bitmap> undoList;
    private String selectedPaintTool, selectedShapeTool, selectedTool, text, path;
    private int recentpathId;

    private Point start;


    public CanvasView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels;

        paintToScreen = new Paint();
        paintLine = new Paint();
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
        bitmapList = new ArrayList<>();
        undoList = new ArrayList<>();
        textPaint = new Paint();
        shapePaint = new Paint();
        start = new Point();
        path = "";

        bitmap = Bitmap.createBitmap((int)dpWidth, (int)dpHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
        bitmapList.add(bitmap.copy(bitmap.getConfig(),bitmap.isMutable()));
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, 0, 0, paintToScreen);

        if(selectedTool.equals("paint"))
        {
            if(selectedPaintTool.equals("Paint Brush") || selectedPaintTool.equals("Eraser"))
            {
                for(Integer key: pathMap.keySet())
                {
                    canvas.drawPath(pathMap.get(key),paintLine);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
            touchStopped(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
            bitmapList.add(bitmap.copy(bitmap.getConfig(),bitmap.isMutable()));
            if(bitmapList.size() > 8){bitmapList.remove(0);}
            Log.d("List Size", ""+ (bitmapList.size()-1));
            undoList.clear();
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

        if(selectedTool.equals("paint"))
        {
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
                floodFill(point, bitmap.getPixel(point.x, point.y), paintLine.getColor());
            }
        }
        else if(selectedTool.equals("text"))
        {
            bitmapCanvas.drawText(text, x, y, textPaint);
        }
        else if(selectedTool.equals("shape"))
        {
            start.x = (int) x;
            start.y = (int) y;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void touchStopped(float x, float y, int pointerID)
    {
        Point point;
        if(selectedTool.equals("paint"))
        {
            Path path = pathMap.get(pointerID);
            bitmapCanvas.drawPath(path, paintLine);
            path.reset();
        }
        if(selectedTool.equals("shape"))
        {
            point = new Point();

            point.x = (int)x;
            point.y = (int)y;

            if(selectedShapeTool.equals("Line"))
            {
                //undoList.add(bitmap);
                bitmapCanvas.drawLine(start.x, start.y, point.x, point.y, shapePaint);
            }

            else if(selectedShapeTool.equals("Arrow"))
            {
                Path path = new Path();
                Point perpendicular = new Point();
                perpendicular.x = (point.x - start.x)/10;
                perpendicular.y = (point.y - start.y)/10;

                path.moveTo(start.x, start.y);
                path.lineTo(point.x - perpendicular.x, point.y - perpendicular.y);
                path.lineTo((point.x - perpendicular.x)  - perpendicular.y, (point.y - perpendicular.y) + perpendicular.x);
                path.lineTo(point.x , point.y);
                path.lineTo((point.x - perpendicular.x) + perpendicular.y, (point.y - perpendicular.y) - perpendicular.x);
                path.lineTo(point.x - perpendicular.x, point.y - perpendicular.y);

                //undoList.add(bitmap);
                bitmapCanvas.drawPath(path, shapePaint);

            }
            else if(selectedShapeTool.equals("Circle"))
            {
                float centreX = (float) start.x;
                float centreY = (float) start.y;
                float radius = (float)Math.sqrt((point.x - start.x) * (point.x - start.x) + (point.y - start.y) * (point.y - start.y));
                //undoList.add(bitmap);
                bitmapCanvas.drawOval(start.x, start.y, point.x, point.y , shapePaint);
            }
            else if(selectedShapeTool.equals("Triangle"))
            {
                Point point2 = new Point();
                Path path = new Path();

                if(point.x < start.x) {point2.x = start.x + (Math.abs(point.x - start.x)); }
                else{point2.x = start.x - (Math.abs(point.x - start.x));}
                point2.y = point.y;

                path.moveTo(start.x, start.y);
                path.lineTo(point.x, point.y);
                path.lineTo(point2.x, point2.y);
                path.lineTo(start.x, start.y);
                //undoList.add(bitmap);
                bitmapCanvas.drawPath(path, shapePaint);
            }
            else if(selectedShapeTool.equals("Square"))
            {
                //undoList.add(bitmap);
                bitmapCanvas.drawRect(start.x, start.y, point.x, point.y, shapePaint);
            }



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

    public void saveImage()
    {
        ContextWrapper wrapper = new ContextWrapper(getContext());
        String fileName = "Paint" + System.currentTimeMillis();

        File directory = wrapper.getDir("imageDir", Context.MODE_PRIVATE);
       //if(!directory.exists()){directory.mkdirs();}
        File myPath;
        if(path.equalsIgnoreCase("") == false)
        {
            myPath = new File(path);
        }
        else
        {
            myPath = new File(directory, fileName + ".jpeg");
        }



        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                outputStream.flush();
                outputStream.close();

                Toast message = Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                message.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void loadImage(String pth)
    {
        path = pth;
        File file = new File(path);
        bitmapCanvas.drawBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), 0,0, null);
        //invalidate();
    }

    public void updatePaint(Paint paint)
    {
        if(selectedPaintTool.equals("Paint Brush")  || selectedPaintTool.equals("Fill Bucket"))
        {
            paintLine = paint;
        }
        else if(selectedPaintTool.equals("Eraser"))
        {
            paintLine = paint;
            paintLine.setColor(Color.WHITE);
            paintLine.setAlpha(255);
        }
    }

    public void setPaintTool(String tool)
    {
       selectedPaintTool = tool;
    }

    public void setShapeTool(String tool)
    {
        selectedShapeTool = tool;
    }

    public String getSelectedTool()
    {
        return selectedTool;
    }

    public void setSelectedTool(String tool)
    {
        selectedTool = tool;
    }

    public void updateText(Paint textTool, String txt)
    {
        textPaint = textTool;
        text = txt;
    }

    public void updateShape(Paint shapeTool)
    {
        shapePaint = shapeTool;
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

    public void undo()
    {
        Log.d("List Size", ""+ (bitmapList.size()-1));
        if(bitmapList.size() > 1)
        {
            undoList.add(bitmapList.remove(bitmapList.size()-1));
            bitmap = bitmapList.get(bitmapList.size()-1).copy(bitmap.getConfig(), bitmap.isMutable());
            bitmapCanvas = new Canvas(bitmap);
            invalidate();
        }
    }

    public void redo()
    {
        Log.d("List Size", ""+ (undoList.size()-1));
        if (undoList.size() > 0)
        {
            bitmapList.add(undoList.remove(undoList.size()-1));
            bitmap = bitmapList.get(bitmapList.size()-1).copy(bitmap.getConfig(), bitmap.isMutable());
            bitmapCanvas = new Canvas(bitmap);
            invalidate();
        }
    }
}