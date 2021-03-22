package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import org.w3c.dom.Text;

public class CanvasPage extends AppCompatActivity {

    private CanvasView canvas;
    private Paint paintTool, textTool, shapeTool;
    private String text, font;
    private ConstraintLayout paintDropDown, shapeDropDown, textDropDown;
    private Spinner fontDropDown;
    private boolean bold, italic, underline, strikeThrough;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        getSupportActionBar().hide();

        canvas = findViewById(R.id.canvasView);
        canvas.setSelectedTool("paint");
        canvas.setPaintTool("Paint Brush");
        canvas.setShapeTool("Line");

        paintTool = new Paint();
        textTool = new Paint();
        shapeTool = new Paint();

        paintTool.setAntiAlias(true);
        paintTool.setColor(Color.BLACK);
        paintTool.setStyle(Paint.Style.STROKE);
        paintTool.setStrokeWidth(7);
        paintTool.setStrokeCap(Paint.Cap.ROUND);

        shapeTool.setColor(Color.BLACK);
        shapeTool.setStrokeWidth(7);
        shapeTool.setStyle(Paint.Style.STROKE);

        textTool.setColor(Color.BLACK);
        textTool.setTextSize(11);
        text = "";

        bold = false;
        italic = false;
        underline = false;
        strikeThrough = false;

        canvas.updatePaint(paintTool);
        canvas.updateText(textTool, text);
        canvas.updateShape(shapeTool);


        paintDropDown = findViewById(R.id.paintToolsDropDown);
        shapeDropDown = findViewById(R.id.shapeToolsDropDown);
        textDropDown = findViewById(R.id.textToolsDropDown);

        SeekBar paintSizeSlider = findViewById(R.id.paintSizeSlider);
        setUpSliders(paintSizeSlider);
        SeekBar transparencySlider = findViewById(R.id.paintTransparencySlider);
        setUpSliders(transparencySlider);
        SeekBar textSizeSlider = findViewById(R.id.textSizeSlider);
        setUpSliders(textSizeSlider);
        SeekBar shapeThicknessSlider = findViewById(R.id.shapeSizeSlider);
        setUpSliders(shapeThicknessSlider);

        ColorSeekBar paintColourPicker = findViewById(R.id.paintColourPicker);
        colourSliders(paintColourPicker);
        ColorSeekBar textColourPicker = findViewById(R.id.textColourPicker);
        colourSliders(textColourPicker);
        ColorSeekBar shapeColourPicker = findViewById(R.id.shapeColourPicker);
        colourSliders(shapeColourPicker);

        fontDropDown = (Spinner) findViewById(R.id.fontDropDown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fonts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        fontDropDown.setAdapter(adapter);
        fontDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                font = parent.getItemAtPosition(position).toString();
                setTypeface();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final EditText enterText = (EditText)findViewById(R.id.enterText);
        enterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text = (s.toString());
                canvas.updateText(textTool, text);
            }
        });
    }

    public void hideUI(View view)
    {
        ConstraintLayout topBar = (ConstraintLayout)findViewById(R.id.drawingToolsBar);
        ConstraintLayout bottomBar = (ConstraintLayout)findViewById(R.id.systemToolsBar);
        topBar.setVisibility(View.INVISIBLE);
        bottomBar.setVisibility(View.INVISIBLE);
    }

    public void processButtons(View myView)
    {
        TextView selectedPaintTool = (TextView)findViewById(R.id.selectedPaintTool);
        TextView selectedShapeTool = (TextView)findViewById(R.id.selectedShapeTool);
        switch(myView.getId())
        {
            case R.id.paintTool:
                shapeDropDown.setVisibility(View.INVISIBLE);
                textDropDown.setVisibility(View.INVISIBLE);
                if(canvas.getSelectedTool().equals("paint"))
                {
                    if(paintDropDown.getVisibility() == View.INVISIBLE){paintDropDown.setVisibility(View.VISIBLE);}
                    else{paintDropDown.setVisibility(View.INVISIBLE);}
                }
                else
                {
                    canvas.setSelectedTool("paint");
                }
                break;

            case R.id.shapeTool:
                paintDropDown.setVisibility(View.INVISIBLE);
                textDropDown.setVisibility(View.INVISIBLE);
                if(canvas.getSelectedTool().equals("shape"))
                {
                    if(shapeDropDown.getVisibility() == View.INVISIBLE){shapeDropDown.setVisibility(View.VISIBLE);}
                    else{shapeDropDown.setVisibility(View.INVISIBLE);}
                }
                else
                {
                    canvas.setSelectedTool("shape");
                }
                break;

            case R.id.textTool:
                shapeDropDown.setVisibility(View.INVISIBLE);
                paintDropDown.setVisibility(View.INVISIBLE);
                if(canvas.getSelectedTool().equals("text"))
                {

                    if(textDropDown.getVisibility() == View.INVISIBLE){textDropDown.setVisibility(View.VISIBLE);}
                    else{textDropDown.setVisibility(View.INVISIBLE);}
                }
                else
                {
                    canvas.setSelectedTool("text");
                }
                break;

            case R.id.paintBrush:
                selectedPaintTool.setText("Paint Brush");
                canvas.setPaintTool("Paint Brush");
                canvas.updatePaint(paintTool);
                break;

            case R.id.sprayCan:
                selectedPaintTool.setText("Spray Can");
                canvas.setPaintTool("Spray Can");
                canvas.updatePaint(paintTool);
                break;

            case R.id.fillBucket:
                selectedPaintTool.setText("Fill Bucket");
                canvas.setPaintTool("Fill Bucket");
                canvas.updatePaint(paintTool);
                break;

            case R.id.eraser:
                selectedPaintTool.setText("Eraser");
                canvas.setPaintTool("Eraser");
                canvas.updatePaint(paintTool);
                break;

            case R.id.redPaintButton:
                paintTool.setColor(Color.RED);
                canvas.updatePaint(paintTool);
                break;

            case R.id.bluePaintButton:
                paintTool.setColor(Color.BLUE);
                canvas.updatePaint(paintTool);
                break;

            case R.id.yellowPaintButton:
                paintTool.setColor(Color.YELLOW);
                canvas.updatePaint(paintTool);
                break;

            case R.id.greenPaintButton:
                paintTool.setColor(Color.GREEN);
                canvas.updatePaint(paintTool);
                break;

            case R.id.magentaPaintButton:
                paintTool.setColor(Color.MAGENTA);
                canvas.updatePaint(paintTool);
                break;

            case R.id.blackPaintButton:
                paintTool.setColor(Color.BLACK);
                canvas.updatePaint(paintTool);
                break;

            case R.id.whitePaintButton:
                paintTool.setColor(Color.WHITE);
                canvas.updatePaint(paintTool);
                break;

            case R.id.greyPaintButton:
                paintTool.setColor(Color.GRAY);
                canvas.updatePaint(paintTool);
                break;

            case R.id.darkGreyPaintButton:
                paintTool.setColor(Color.DKGRAY);
                canvas.updatePaint(paintTool);
                break;

            case R.id.cyanPaintButton:
                paintTool.setColor(Color.CYAN);
                canvas.updatePaint(paintTool);
                break;

            case R.id.lineButton:
                selectedShapeTool.setText("Line");
                canvas.setShapeTool("Line");
                canvas.updateShape(shapeTool);
                break;

            case R.id.arrowButton:
                selectedShapeTool.setText("Arrow");
                canvas.setShapeTool("Arrow");
                canvas.updateShape(shapeTool);
                break;

            case R.id.circleButton:
                selectedShapeTool.setText("Circle");
                canvas.setShapeTool("Circle");
                canvas.updateShape(shapeTool);
                break;

            case R.id.triangleButton:
                selectedShapeTool.setText("Triangle");
                canvas.setShapeTool("Triangle");
                canvas.updateShape(shapeTool);
                break;

            case R.id.squareButton:
                selectedShapeTool.setText("Square");
                canvas.setShapeTool("Square");
                canvas.updateShape(shapeTool);
                break;

            case R.id.pentagonButton:
                selectedShapeTool.setText("Pentagon");
                canvas.setShapeTool("Pentagon");
                canvas.updateShape(shapeTool);
                break;

            case R.id.hexagonButton:
                selectedShapeTool.setText("Hexagon");
                canvas.setShapeTool("Hexagon");
                canvas.updateShape(shapeTool);
                break;

            case R.id.octogonButton:
                selectedShapeTool.setText("Octogon");
                canvas.setShapeTool("Octogon");
                canvas.updateShape(shapeTool);
                break;

            case R.id.redShapeButton:
                shapeTool.setColor(Color.RED);
                canvas.updateShape(shapeTool);
                break;

            case R.id.blueShapeButton:
                shapeTool.setColor(Color.BLUE);
                canvas.updateShape(shapeTool);
                break;

            case R.id.yellowShapeButton:
                shapeTool.setColor(Color.YELLOW);
                canvas.updateShape(shapeTool);
                break;

            case R.id.greenShapeButton:
                shapeTool.setColor(Color.GREEN);
                canvas.updateShape(shapeTool);
                break;

            case R.id.magentaShapeButton:
                shapeTool.setColor(Color.MAGENTA);
                canvas.updateShape(shapeTool);
                break;

            case R.id.blackShapeButton:
                shapeTool.setColor(Color.BLACK);
                canvas.updateShape(shapeTool);
                break;

            case R.id.whiteShapeButton:
                shapeTool.setColor(Color.WHITE);
                canvas.updateShape(shapeTool);
                break;

            case R.id.greyShapeButton:
                shapeTool.setColor(Color.GRAY);
                canvas.updateShape(shapeTool);
                break;

            case R.id.darkGreyShapeButton:
                shapeTool.setColor(Color.DKGRAY);
                canvas.updateShape(shapeTool);
                break;

            case R.id.cyanShapeButton:
                shapeTool.setColor(Color.CYAN);
                canvas.updateShape(shapeTool);
                break;

            case R.id.boldButton:
                if(bold){bold = false;}
                else{bold = true;}
                setTypeface();
                break;

            case R.id.italicButton:
                if(italic){italic = false;}
                else{italic = true;}
                setTypeface();
                break;

            case R.id.underlineButton:
                if(underline){underline = false;}
                else{underline = true;}
                setTypeface();
                break;

            case R.id.strikethroughButton:
                if(strikeThrough){strikeThrough = false;}
                else{strikeThrough = true;}
                setTypeface();
                break;

            case R.id.redTextButton:
                textTool.setColor(Color.RED);
                canvas.updateText(textTool,  text);
                break;

            case R.id.blueTextButton:
                textTool.setColor(Color.BLUE);
                canvas.updateText(textTool,  text);
                break;

            case R.id.yellowTextButton:
                textTool.setColor(Color.YELLOW);
                canvas.updateText(textTool,  text);
                break;

            case R.id.greenTextButton:
                textTool.setColor(Color.GREEN);
                canvas.updateText(textTool,  text);
                break;

            case R.id.magentaTextButton:
                textTool.setColor(Color.MAGENTA);
                canvas.updateText(textTool,  text);
                break;

            case R.id.blackTextButton:
                textTool.setColor(Color.BLACK);
                canvas.updateText(textTool,  text);
                break;

            case R.id.whiteTextButton:
                textTool.setColor(Color.WHITE);
                canvas.updateText(textTool,  text);
                break;

            case R.id.greyTextButton:
                textTool.setColor(Color.GRAY);
                canvas.updateText(textTool,  text);
                break;

            case R.id.darkGreyTextButton:
                textTool.setColor(Color.DKGRAY);
                canvas.updateText(textTool,  text);
                break;

            case R.id.cyanTextButton:
                textTool.setColor(Color.CYAN);
                canvas.updateText(textTool,  text);
                break;
        }

    }

    public void setUpSliders(final SeekBar slider)
    {
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(slider.getId() == R.id.paintSizeSlider)
                {
                    TextView sizeText = (TextView)findViewById(R.id.sizeText);
                    sizeText.setText("Size: "+ progress);
                    paintTool.setStrokeWidth(progress);
                }
                else if(slider.getId() == R.id.paintTransparencySlider)
                {
                    TextView transparenyText = (TextView)findViewById(R.id.paintTransparencyText);
                    transparenyText.setText("Transparency: "+ progress);
                    paintTool.setAlpha(progress);
                }
                else if(slider.getId() == R.id.shapeSizeSlider)
                {
                    TextView shapeSizeText = (TextView)findViewById(R.id.shapeSizeText);
                    shapeSizeText.setText("Thickness " + progress);
                    shapeTool.setStrokeWidth(progress);
                }
                else if(slider.getId() == R.id.textSizeSlider)
                {
                    TextView textSizeText = (TextView)findViewById(R.id.textSizeText);
                    textSizeText.setText("Size: " + progress);
                    textTool.setTextSize(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(slider.getId() == R.id.paintSizeSlider || slider.getId() == R.id.paintTransparencySlider)
                {
                    canvas.updatePaint(paintTool);
                }
                else if(slider.getId() == R.id.shapeSizeSlider)
                {
                    canvas.updateShape(shapeTool);
                }
                else if(slider.getId() == R.id.textSizeSlider)
                {
                    canvas.updateText(textTool, text);
                }
            }
        });
    }

    public void colourSliders(final ColorSeekBar colourSeeker)
    {
        colourSeeker.setMaxPosition(100);
        colourSeeker.setColorSeeds(R.array.material_colors);
        colourSeeker.setColorBarPosition(50);

        colourSeeker.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {

                if(colourSeeker.getId() == R.id.paintColourPicker)
                {
                    TextView colourText = findViewById(R.id.colourText);
                    colourText.setText("Colour: " + color);
                    paintTool.setColor(color);
                    canvas.updatePaint(paintTool);
                }
                else if(colourSeeker.getId() == R.id.shapeColourPicker)
                {
                    shapeTool.setColor(color);
                    canvas.updateShape(shapeTool);
                }
                else if(colourSeeker.getId() == R.id.textColourPicker)
                {
                    textTool.setColor(color);
                    canvas.updateText(textTool, text);
                }
            }
        });
    }

    private void setTypeface()
    {
        if(bold && italic) {textTool.setTypeface(Typeface.create(font, Typeface.BOLD_ITALIC)); }
        else if(bold){textTool.setTypeface(Typeface.create(font, Typeface.BOLD));}
        else if(italic){textTool.setTypeface(Typeface.create(font, Typeface.ITALIC));}
        else{textTool.setTypeface(Typeface.create(font, Typeface.NORMAL));}

        if(underline && strikeThrough){textTool.setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);}
        else if(underline){textTool.setFlags(Paint.UNDERLINE_TEXT_FLAG);}
        else if(strikeThrough){textTool.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);}
        else{textTool.setFlags(0);}
        canvas.updateText(textTool, text);
    }
}