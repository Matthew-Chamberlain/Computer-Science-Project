package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

public class CanvasPage extends AppCompatActivity {

    CanvasView canvas;
    int currentColour, currentSize, currentAlpha;
    ConstraintLayout paintDropDown, shapeDropDown;// textDropDown;
    SeekBar sizeSlider, transparencySlider;
    ColorSeekBar colourPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        getSupportActionBar().hide();

        canvas = findViewById(R.id.canvasView);
        canvas.setSelectedTool("paint");
        canvas.setPaintTool("Paint Brush");


        currentSize = 7;
        currentAlpha = 255;
        currentColour = Color.BLACK;

        paintDropDown =(ConstraintLayout) findViewById(R.id.paintToolsDropDown);
        shapeDropDown =(ConstraintLayout) findViewById(R.id.shapeToolsDropDown);
        //textDropDown =(LinearLayout)findViewById(R.id.textToolDropDown);

        sizeSlider = findViewById(R.id.paintSizeSlider);
        sizeSlider();

        transparencySlider = findViewById(R.id.paintTransparencySlider);
        transparencySlider();

        colourPicker = findViewById(R.id.colourPicker);
        colourSlider();

    }

    public void hideUI(View view)
    {
        ConstraintLayout topBar = (ConstraintLayout)findViewById(R.id.drawingToolsBar);
        ConstraintLayout bottomBar = (ConstraintLayout)findViewById(R.id.systemToolsBar);
        topBar.setVisibility(View.INVISIBLE);
        bottomBar.setVisibility(View.INVISIBLE);
    }

    public void paintToolButtonPressed(View view)
    {
        shapeDropDown.setVisibility(View.INVISIBLE);
        //textDropDown.setVisibility(View.INVISIBLE);
        if(canvas.getSelectedTool().equals("paint"))
        {
            if(paintDropDown.getVisibility() == View.INVISIBLE){paintDropDown.setVisibility(View.VISIBLE);}
            else{paintDropDown.setVisibility(View.INVISIBLE);}
        }
        else
        {
            canvas.setSelectedTool("paint");
        }
    }

    public void shapeToolButtonPressed(View view)
    {
        paintDropDown.setVisibility(View.INVISIBLE);
        //textDropDown.setVisibility(View.INVISIBLE);
        if(canvas.getSelectedTool().equals("shape"))
        {
            if(shapeDropDown.getVisibility() == View.INVISIBLE){shapeDropDown.setVisibility(View.VISIBLE);}
            else{shapeDropDown.setVisibility(View.INVISIBLE);}
        }
        else
        {
            canvas.setSelectedTool("shape");
        }
    }
    /*public void textToolButtonPressed(View view)
    {
        shapeDropDown.setVisibility(View.INVISIBLE);
        paintDropDown.setVisibility(View.INVISIBLE);
        if(currentTool.equals("text"))
        {

            if(textDropDown.getVisibility() == View.INVISIBLE){textDropDown.setVisibility(View.VISIBLE);}
            else{textDropDown.setVisibility(View.INVISIBLE);}
        }
        else
        {
            currentTool = "text";
        }
    }*/

    public void changeColour(View myView)
    {
        switch(myView.getId())
        {
            case R.id.redButton:
                currentColour = Color.RED;
                break;

            case R.id.blueButton:
                currentColour = Color.BLUE;
                break;

            case R.id.yellowButton:
                currentColour = Color.YELLOW;
                break;

            case R.id.greenButton:
                currentColour = Color.GREEN;
                break;

            case R.id.magentaButton:
                currentColour = Color.MAGENTA;
                break;

            case R.id.blackButton:
                currentColour = Color.BLACK;
                break;

            case R.id.whiteButton:
                currentColour = Color.WHITE;
                break;

            case R.id.greyButton:
                currentColour = Color.GRAY;
                break;

            case R.id.darkGreyButton:
                currentColour = Color.DKGRAY;
                break;

            case R.id.cyanButton:
                currentColour = Color.CYAN;
                break;
        }
        canvas.updatePaint(currentSize, currentColour, currentAlpha);
    }

    public void sizeSlider()
    {
        sizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView sizeText = (TextView)findViewById(R.id.sizeText);
                sizeText.setText("Size: "+ progress);
                currentSize = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                canvas.updatePaint(currentSize, currentColour, currentAlpha);
            }
        });
    }

    public void transparencySlider()
    {
        transparencySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView transparenyText = (TextView)findViewById(R.id.paintTransparencyText);
                transparenyText.setText("Transparency: "+ progress);
                currentAlpha = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                canvas.updatePaint(currentSize, currentColour, currentAlpha);
            }
        });
    }

    public void colourSlider()
    {
        colourPicker.setMaxPosition(100);
        colourPicker.setColorSeeds(R.array.material_colors);
        colourPicker.setColorBarPosition(50);

        colourPicker.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                TextView colourText = findViewById(R.id.colourText);
                colourText.setText("Colour: " + color);
                currentColour = color;
                canvas.updatePaint(currentSize, currentColour, currentAlpha);
            }
        });
    }

    public void setPaintTool(View myView)
    {
        TextView selectedPaintTool = (TextView)findViewById(R.id.selectedPaintTool);

        if(myView.getId() == R.id.paintBrush)
        {
            selectedPaintTool.setText("Paint Brush");
            canvas.setPaintTool("Paint Brush");
            canvas.updatePaint(currentSize, currentColour, currentAlpha);
        }
        else if(myView.getId() == R.id.fillBucket)
        {
            selectedPaintTool.setText("Fill Bucket");
            canvas.setPaintTool("Fill Bucket");
            canvas.updatePaint(currentSize, currentColour, currentAlpha);
        }
        else if(myView.getId() == R.id.sprayCan)
        {
            selectedPaintTool.setText("Spray Can");
            canvas.setPaintTool("Spray Can");
            canvas.updatePaint(currentSize, currentColour, currentAlpha);
        }
        else if(myView.getId() == R.id.eraser)
        {
            selectedPaintTool.setText("Eraser");
            canvas.setPaintTool("Eraser");
            canvas.updatePaint(currentSize, currentColour, currentAlpha);
        }
    }
}