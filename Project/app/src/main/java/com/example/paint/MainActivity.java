package com.example.paint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showPreviousDrawings();
         tv1 = (TextView)findViewById(R.id.pageTitle);
    }

    private void showPreviousDrawings()
    {

    }

    public void blankCanvasButton(View myView)
    {
       setContentView(R.layout.canvas_view);
;   }

    public void importImageButton(View myView)
    {

    }

    public void cameraButton(View myView)
    {

    }
}