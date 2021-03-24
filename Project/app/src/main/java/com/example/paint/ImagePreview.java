package com.example.paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class ImagePreview extends AppCompatActivity
{
    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        setTitle("Image Preview");

        Intent intent = getIntent();
        String path = intent.getExtras().getString("preview");
       // Bitmap bitmap = (Bitmap) intent.getParcelableExtra("Bitmap");
        //imageView.setImageBitmap(bitmap);

        preview = (ImageView)findViewById(R.id.imagePreview);
        preview.setImageDrawable(Drawable.createFromPath(path));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.editButton)
        {
            Intent intent = new Intent(this, CanvasPage.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.exportButton)
        {

        }
        if(item.getItemId() == R.id.deleteButton)
        {

        }
        return true;
    }
}