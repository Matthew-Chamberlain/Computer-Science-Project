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
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ImagePreview extends AppCompatActivity
{
    private ImageView preview;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#000000\">Image Preview</font>")));

        Intent intent = getIntent();
        path = intent.getExtras().getString("preview");

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
            intent.putExtra("Edit", path);
            startActivity(intent);
            finish();
        }
        if(item.getItemId() == R.id.exportButton)
        {
            File file = new File(path);
            MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(file.getAbsolutePath()), file.getName(), "Created by Paint App");
            Toast message = Toast.makeText(this, "Image Exported", Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }
        if(item.getItemId() == R.id.deleteButton)
        {
            File file = new File(path);
            file.delete();

            MainActivity.mainMenu.finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}