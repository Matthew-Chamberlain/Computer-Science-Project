package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class CanvasPage extends AppCompatActivity {

    private CanvasView canvas;
    private Paint paintTool, textTool, shapeTool;
    private String text, font, path;
    private int selectedPaintColour, transparency;
    private Spinner fontDropDown;
    private boolean bold, italic, underline, strikeThrough;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        getSupportActionBar().hide();

        Intent imageIntent = getIntent();
        path = imageIntent.getExtras().getString("Edit");


        canvas = findViewById(R.id.canvasView);
        canvas.setSelectedTool("paint");
        canvas.setPaintTool("Paint Brush");
        canvas.setShapeTool("Line");
        canvas.path = path;
        //if(path.equalsIgnoreCase("") == false)
        //{
        canvas.loadImage(path);
        //}

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
        textTool.setTextSize(50);
        text = "Preview";

        bold = false;
        italic = false;
        underline = false;
        strikeThrough = false;

        canvas.updatePaint(paintTool);
        canvas.updateText(textTool, text);
        canvas.updateShape(shapeTool);

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
        fontDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                font = parent.getItemAtPosition(position).toString();
                setTypeface();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final EditText enterText = (EditText) findViewById(R.id.enterText);
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
                updateTextPreview();
            }
        });
        transparency = 255;
        updatePaintPreview();
        updateTextPreview();
    }

    public void processButtons(View myView) {
        TextView selectedPaintTool = (TextView) findViewById(R.id.selectedPaintTool);
        TextView selectedShapeTool = (TextView) findViewById(R.id.selectedShapeTool);

        ImageButton paintIcon = findViewById(R.id.paintTool);
        ImageButton shapeIcon = findViewById(R.id.shapeTool);
        ImageButton textIcon = findViewById(R.id.textTool);

        LinearLayout paintToolBackground = findViewById(R.id.paintBackground);
        LinearLayout shapeToolBackground = findViewById(R.id.shapeBackground);
        LinearLayout textToolBackground = findViewById(R.id.textBackground);
        LinearLayout extraToolsBackground = findViewById(R.id.extraToolsBackground);

        final ConstraintLayout paintDropDown, shapeDropDown, extraToolsDropDown, textDropDown, topBar, bottomBar;

        paintDropDown = findViewById(R.id.paintToolsDropDown);
        shapeDropDown = findViewById(R.id.shapeToolsDropDown);
        textDropDown = findViewById(R.id.textToolsDropDown);
        extraToolsDropDown = findViewById(R.id.extraToolsDropDown);
        topBar = findViewById(R.id.drawingToolsBar);
        bottomBar = findViewById(R.id.systemToolsBar);

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        buttonClick.setDuration(500);

        myView.startAnimation(buttonClick);


        switch (myView.getId()) {
            case R.id.paintTool:
                shapeToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                shapeIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                shapeDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shapeDropDown.setVisibility(View.GONE);
                    }
                });

                textToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                textIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                textDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textDropDown.setVisibility(View.GONE);
                    }
                });

                extraToolsBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                extraToolsDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        extraToolsDropDown.setVisibility(View.GONE);
                    }
                });

                if (canvas.getSelectedTool().equals("paint")) {
                    if (paintDropDown.getVisibility() == View.GONE) {
                        paintToolBackground.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        paintIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        paintDropDown.setAlpha(0F);
                        paintDropDown.setVisibility(View.VISIBLE);
                        canvas.setVisibility(View.GONE);
                        paintDropDown.animate().alpha(1F).setDuration(300).setListener(null);

                    } else {
                        paintDropDown.setAlpha(1F);
                        paintDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                paintDropDown.setVisibility(View.GONE);
                            }
                        });
                        paintTool.setAlpha(255);
                        paintToolBackground.setBackgroundColor(paintTool.getColor());
                        if (paintTool.getColor() == Color.BLACK) {
                            paintIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                        } else {
                            paintIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        }
                        paintTool.setAlpha(transparency);
                        canvas.setVisibility(View.VISIBLE);
                    }
                } else {
                    canvas.setSelectedTool("paint");
                    paintTool.setAlpha(255);
                    paintToolBackground.setBackgroundColor(paintTool.getColor());
                    if (paintTool.getColor() == Color.BLACK) {
                        paintIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    } else {
                        paintIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                    }
                    paintTool.setAlpha(transparency);
                    canvas.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.shapeTool:
                paintToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                paintIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                paintDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        paintDropDown.setVisibility(View.GONE);
                    }
                });

                textToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                textIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                textDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textDropDown.setVisibility(View.GONE);
                    }
                });

                extraToolsBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                extraToolsDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        extraToolsDropDown.setVisibility(View.GONE);
                    }
                });
                if (canvas.getSelectedTool().equals("shape")) {
                    if (shapeDropDown.getVisibility() == View.GONE) {
                        shapeToolBackground.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        shapeIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        shapeDropDown.setAlpha(0F);
                        shapeDropDown.setVisibility(View.VISIBLE);
                        shapeDropDown.animate().alpha(1F).setDuration(300).setListener(null);
                        canvas.setVisibility(View.GONE);
                    } else {
                        shapeDropDown.setAlpha(1F);
                        shapeDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                shapeDropDown.setVisibility(View.GONE);
                            }
                        });
                        shapeToolBackground.setBackgroundColor(shapeTool.getColor());
                        if (shapeTool.getColor() == Color.BLACK) {
                            shapeIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                        } else {
                            shapeIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        }
                        canvas.setVisibility(View.VISIBLE);

                    }
                } else {
                    canvas.setSelectedTool("shape");
                    shapeToolBackground.setBackgroundColor(shapeTool.getColor());
                    if (shapeTool.getColor() == Color.BLACK) {
                        shapeIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    } else {
                        shapeIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                    }
                    canvas.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.textTool:
                shapeToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                shapeIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                shapeDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shapeDropDown.setVisibility(View.GONE);
                    }
                });

                paintToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                paintIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                paintDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        paintDropDown.setVisibility(View.GONE);
                    }
                });

                extraToolsBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                extraToolsDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        extraToolsDropDown.setVisibility(View.GONE);
                    }
                });
                if (canvas.getSelectedTool().equals("text")) {

                    if (textDropDown.getVisibility() == View.GONE) {
                        textToolBackground.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        textIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        textDropDown.setAlpha(0F);
                        textDropDown.setVisibility(View.VISIBLE);
                        textDropDown.animate().alpha(1F).setDuration(300).setListener(null);
                        canvas.setVisibility(View.GONE);
                    } else {
                        textDropDown.setAlpha(1F);
                        textDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                textDropDown.setVisibility(View.GONE);
                            }
                        });
                        textToolBackground.setBackgroundColor(textTool.getColor());
                        if (textTool.getColor() == Color.BLACK) {
                            textIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                        } else {
                            textIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                        }
                        canvas.setVisibility(View.VISIBLE);
                    }
                } else {
                    canvas.setSelectedTool("text");
                    textToolBackground.setBackgroundColor(textTool.getColor());
                    if (textTool.getColor() == Color.BLACK) {
                        textIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    } else {
                        textIcon.setColorFilter(Color.argb(255, 0, 0, 0));
                    }
                    canvas.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.extraTools:
                //shapeToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                shapeDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shapeDropDown.setVisibility(View.GONE);
                    }
                });

                //paintToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                paintDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        paintDropDown.setVisibility(View.GONE);
                    }
                });

                //textToolBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                textDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textDropDown.setVisibility(View.GONE);
                    }
                });
                //canvas.setSelectedTool("extra");
                if (extraToolsDropDown.getVisibility() == View.GONE) {
                    extraToolsBackground.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    extraToolsDropDown.setAlpha(0F);
                    extraToolsDropDown.setVisibility(View.VISIBLE);
                    extraToolsDropDown.animate().alpha(1F).setDuration(300).setListener(null);
                } else {
                    extraToolsDropDown.setAlpha(1F);
                    extraToolsDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            extraToolsDropDown.setVisibility(View.GONE);
                        }
                    });
                    extraToolsBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
                canvas.setVisibility(View.VISIBLE);

                if (canvas.getSelectedTool().equals("paint")) {
                    if (paintTool.getColor() == Color.BLACK) {
                        paintIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    }
                    paintTool.setAlpha(255);
                    paintToolBackground.setBackgroundColor(paintTool.getColor());
                    paintTool.setAlpha(transparency);
                } else if (canvas.getSelectedTool().equals("shape")) {
                    if (shapeTool.getColor() == Color.BLACK) {
                        shapeIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    }
                    shapeToolBackground.setBackgroundColor(shapeTool.getColor());
                } else if (canvas.getSelectedTool().equals("text")) {
                    if (textTool.getColor() == Color.BLACK) {
                        textIcon.setColorFilter(Color.argb(255, 255, 255, 255));
                    }
                    textToolBackground.setBackgroundColor(textTool.getColor());
                }
                break;

            case R.id.saveButton:
                canvas.saveImage();
                MainActivity.mainMenu.finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;


            case R.id.undoButton:
                canvas.undo();
                break;

            case R.id.redoButton:
                canvas.redo();
                break;

            case R.id.hideUIButton:

                topBar.setAlpha(1F);
                topBar.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        topBar.setVisibility(View.GONE);
                    }
                });

                bottomBar.setAlpha(1F);
                bottomBar.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bottomBar.setVisibility(View.GONE);
                    }
                });

                extraToolsDropDown.setAlpha(1F);
                extraToolsDropDown.animate().alpha(0F).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        extraToolsDropDown.setVisibility(View.GONE);
                    }
                });
                extraToolsBackground.setBackgroundColor(Color.parseColor("#D5D5D5"));

                ImageButton showUIButton = findViewById(R.id.showUIButton);
                showUIButton.setAlpha(0F);
                showUIButton.setVisibility(View.VISIBLE);
                showUIButton.animate().alpha(1F).setDuration(300).setListener(null);
                break;

            case R.id.showUIButton:
                topBar.setAlpha(0F);
                topBar.setVisibility(View.VISIBLE);
                topBar.animate().alpha(1F).setDuration(300).setListener(null);

                bottomBar.setAlpha(0F);
                bottomBar.setVisibility(View.VISIBLE);
                bottomBar.animate().alpha(1F).setDuration(300).setListener(null);

            case R.id.paintBrush:
                paintIcon.setImageResource(R.drawable.ic_paint_brush);
                selectedPaintTool.setText("Paint Brush");
                if (canvas.getPaintTool().equals("Eraser")) {
                    paintTool.setColor(selectedPaintColour);
                }
                canvas.setPaintTool("Paint Brush");
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.sprayCan:
                paintIcon.setImageResource(R.drawable.ic_spray_can);
                selectedPaintTool.setText("Spray Can");
                if (canvas.getPaintTool().equals("Eraser")) {
                    paintTool.setColor(selectedPaintColour);
                }
                canvas.setPaintTool("Spray Can");
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.fillBucket:
                paintIcon.setImageResource(R.drawable.ic_color_fill);
                selectedPaintTool.setText("Fill Bucket");
                if (canvas.getPaintTool().equals("Eraser")) {
                    paintTool.setColor(selectedPaintColour);
                }
                canvas.setPaintTool("Fill Bucket");
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.eraser:
                paintIcon.setImageResource(R.drawable.ic_eraser);
                selectedPaintTool.setText("Eraser");
                selectedPaintColour = paintTool.getColor();
                canvas.setPaintTool("Eraser");
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.redPaintButton:
                paintTool.setColor(Color.RED);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.bluePaintButton:
                paintTool.setColor(Color.BLUE);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.yellowPaintButton:
                paintTool.setColor(Color.YELLOW);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.greenPaintButton:
                paintTool.setColor(Color.GREEN);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.magentaPaintButton:
                paintTool.setColor(Color.MAGENTA);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.blackPaintButton:
                paintTool.setColor(Color.BLACK);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.whitePaintButton:
                paintTool.setColor(Color.WHITE);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.greyPaintButton:
                paintTool.setColor(Color.GRAY);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.darkGreyPaintButton:
                paintTool.setColor(Color.DKGRAY);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.cyanPaintButton:
                paintTool.setColor(Color.CYAN);
                paintTool.setAlpha(transparency);
                canvas.updatePaint(paintTool);
                updatePaintPreview();
                break;

            case R.id.lineButton:
                shapeIcon.setImageResource(R.drawable.ic_line);
                selectedShapeTool.setText("Line");
                canvas.setShapeTool("Line");
                canvas.updateShape(shapeTool);
                break;

            case R.id.arrowButton:
                shapeIcon.setImageResource(R.drawable.ic_arrow);
                selectedShapeTool.setText("Arrow");
                canvas.setShapeTool("Arrow");
                canvas.updateShape(shapeTool);
                break;

            case R.id.circleButton:
                shapeIcon.setImageResource(R.drawable.ic_circle);
                selectedShapeTool.setText("Circle");
                canvas.setShapeTool("Circle");
                canvas.updateShape(shapeTool);
                break;

            case R.id.triangleButton:
                shapeIcon.setImageResource(R.drawable.ic_triangle);
                selectedShapeTool.setText("Triangle");
                canvas.setShapeTool("Triangle");
                canvas.updateShape(shapeTool);
                break;

            case R.id.squareButton:
                shapeIcon.setImageResource(R.drawable.ic_square);
                selectedShapeTool.setText("Square");
                canvas.setShapeTool("Square");
                canvas.updateShape(shapeTool);
                break;

            /*case R.id.pentagonButton:
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
            */
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
                if (bold) {
                    bold = false;
                    myView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    bold = true;
                    myView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
                setTypeface();
                break;

            case R.id.italicButton:
                if (italic) {
                    italic = false;
                    myView.setBackgroundColor(Color.TRANSPARENT);

                } else {
                    italic = true;
                    myView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
                setTypeface();
                break;

            case R.id.underlineButton:
                if (underline) {
                    underline = false;
                    myView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    underline = true;
                    myView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
                setTypeface();
                break;

            case R.id.strikethroughButton:
                if (strikeThrough) {
                    strikeThrough = false;
                    myView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    strikeThrough = true;
                    myView.setBackgroundColor(Color.parseColor("#D5D5D5"));
                }
                setTypeface();
                break;

            case R.id.redTextButton:
                textTool.setColor(Color.RED);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.blueTextButton:
                textTool.setColor(Color.BLUE);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.yellowTextButton:
                textTool.setColor(Color.YELLOW);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.greenTextButton:
                textTool.setColor(Color.GREEN);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.magentaTextButton:
                textTool.setColor(Color.MAGENTA);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.blackTextButton:
                textTool.setColor(Color.BLACK);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.whiteTextButton:
                textTool.setColor(Color.WHITE);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.greyTextButton:
                textTool.setColor(Color.GRAY);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.darkGreyTextButton:
                textTool.setColor(Color.DKGRAY);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.cyanTextButton:
                textTool.setColor(Color.CYAN);
                canvas.updateText(textTool, text);
                updateTextPreview();
                break;

            case R.id.clearCanvasButton:
                canvas.clearMap();
                break;

            case R.id.importImageButton:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), 0);
                break;

            /*case R.id.cameraButton:
                Intent takePhoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhoto, 1);
                break;*/
        }

    }

    public void setUpSliders(final SeekBar slider) {
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (slider.getId() == R.id.paintSizeSlider) {
                    TextView sizeText = (TextView) findViewById(R.id.sizeText);
                    sizeText.setText("Size: " + progress);
                    paintTool.setStrokeWidth(progress);
                    updatePaintPreview();
                } else if (slider.getId() == R.id.paintTransparencySlider) {
                    TextView transparenyText = (TextView) findViewById(R.id.paintTransparencyText);
                    transparenyText.setText("Transparency: " + progress);
                    paintTool.setAlpha(progress);
                    transparency = progress;
                    updatePaintPreview();
                } else if (slider.getId() == R.id.shapeSizeSlider) {
                    TextView shapeSizeText = (TextView) findViewById(R.id.shapeSizeText);
                    shapeSizeText.setText("Thickness " + progress);
                    shapeTool.setStrokeWidth(progress);
                } else if (slider.getId() == R.id.textSizeSlider) {
                    TextView textSizeText = (TextView) findViewById(R.id.textSizeText);
                    textSizeText.setText("Size: " + progress);
                    textTool.setTextSize(progress);
                    updateTextPreview();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (slider.getId() == R.id.paintSizeSlider || slider.getId() == R.id.paintTransparencySlider) {
                    canvas.updatePaint(paintTool);
                } else if (slider.getId() == R.id.shapeSizeSlider) {
                    canvas.updateShape(shapeTool);
                } else if (slider.getId() == R.id.textSizeSlider) {
                    canvas.updateText(textTool, text);
                }
            }
        });
    }

    public void colourSliders(final ColorSeekBar colourSeeker) {
        colourSeeker.setMaxPosition(100);
        colourSeeker.setColorSeeds(R.array.material_colors);
        colourSeeker.setColorBarPosition(50);

        colourSeeker.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {

                if (colourSeeker.getId() == R.id.paintColourPicker) {
                    paintTool.setColor(color);
                    paintTool.setAlpha(transparency);
                    canvas.updatePaint(paintTool);
                    updatePaintPreview();
                } else if (colourSeeker.getId() == R.id.shapeColourPicker) {
                    shapeTool.setColor(color);
                    canvas.updateShape(shapeTool);
                } else if (colourSeeker.getId() == R.id.textColourPicker) {
                    textTool.setColor(color);
                    canvas.updateText(textTool, text);
                    updateTextPreview();
                }
            }
        });
    }

    private void setTypeface() {
        if (bold && italic) {
            textTool.setTypeface(Typeface.create(font, Typeface.BOLD_ITALIC));
        } else if (bold) {
            textTool.setTypeface(Typeface.create(font, Typeface.BOLD));
        } else if (italic) {
            textTool.setTypeface(Typeface.create(font, Typeface.ITALIC));
        } else {
            textTool.setTypeface(Typeface.create(font, Typeface.NORMAL));
        }

        if (underline && strikeThrough) {
            textTool.setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
        } else if (underline) {
            textTool.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else if (strikeThrough) {
            textTool.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textTool.setFlags(0);
        }
        canvas.updateText(textTool, text);
        updateTextPreview();
    }

    private void updatePaintPreview() {
        ImageView preview = findViewById(R.id.paintPreview);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;

        Bitmap b = Bitmap.createBitmap((int) dpWidth, 250, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        b.eraseColor(Color.parseColor("#f5f5f5"));
        if (canvas.getPaintTool().equals("Paint Brush") || canvas.getPaintTool().equals("Eraser"))
        {
            c.drawLine(110, 125, (int) dpWidth - 110, 125, paintTool);
        }
        else if (canvas.getPaintTool().equals("Fill Bucket"))
        {
            b.eraseColor(paintTool.getColor());
        }
        else if(canvas.getPaintTool().equals("Spray Can"))
        {
            for(int i = 1; i < dpWidth; i+= 30)
            {
                for(int j = 0; j < 5; j++)
                {
                    Random random = new Random();
                    int random1 = (int) (i + random.nextGaussian()*paintTool.getStrokeWidth());
                    int random2 = (int) (125 + random.nextGaussian()*paintTool.getStrokeWidth());

                    c.drawPoint(random1, random2, paintTool);
                }
            }
        }

        preview.setImageBitmap(b);
    }

    private void updateTextPreview() {
        ImageView preview = findViewById(R.id.textPreview);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;

        Bitmap b = Bitmap.createBitmap((int) dpWidth, 210, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        b.eraseColor(Color.parseColor("#f5f5f5"));
        c.drawText(text, (dpWidth / 2) - ((int) (textTool.measureText(text) / 2)), 200, textTool);
        preview.setImageBitmap(b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 0) {
                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    canvas.loadImage(selectedImageUri.getPath());
                }
                else if(resultCode == 1)
                {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    canvas.loadPhoto(photo);
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}