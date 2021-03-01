package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class CanvasPage extends AppCompatActivity {

    CanvasView canvas;
    String currentTool;
    LinearLayout paintDropDown, shapeDropDown, textDropDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_page);
        getSupportActionBar().hide();
        currentTool = "paint";
        paintDropDown =(LinearLayout)findViewById(R.id.paintToolsDropDown);
        shapeDropDown =(LinearLayout)findViewById(R.id.shapeToolsDropDown);
        textDropDown =(LinearLayout)findViewById(R.id.textToolDropDown);
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
        textDropDown.setVisibility(View.INVISIBLE);
        if(currentTool.equals("paint"))
        {
            if(paintDropDown.getVisibility() == View.INVISIBLE){paintDropDown.setVisibility(View.VISIBLE);}
            else{paintDropDown.setVisibility(View.INVISIBLE);}
        }
        else
        {
            currentTool = "paint";
        }
    }

    public void shapeToolButtonPressed(View view)
    {
        paintDropDown.setVisibility(View.INVISIBLE);
        textDropDown.setVisibility(View.INVISIBLE);
        if(currentTool.equals("shape"))
        {
            if(shapeDropDown.getVisibility() == View.INVISIBLE){shapeDropDown.setVisibility(View.VISIBLE);}
            else{shapeDropDown.setVisibility(View.INVISIBLE);}
        }
        else
        {
            currentTool = "shape";
        }
    }
    public void textToolButtonPressed(View view)
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
    }
}