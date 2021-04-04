package com.example.paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinkedHashMap<File, ImageView> imageList;
    public static Activity mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Previous Drawings");

        mainMenu = this;
        imageList = new LinkedHashMap<File, ImageView>();
        imageViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.blankCanvasButton)
        {
            Intent intent = new Intent(this, CanvasPage.class);
            intent.putExtra("Edit", "");
            startActivity(intent);
        }
        if(item.getItemId() == R.id.importImageButton)
        {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), 0);
        }
        if(item.getItemId() == R.id.cameraButton)
        {

        }
        return true;
    }

    private void imageViews() {
        int rows;
        int count = 0;
        TableLayout table = (TableLayout)findViewById(R.id.previousDrawings);
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File[] images = directory.listFiles();
        for (int i = 0; i < images.length; i++) {
            ImageView tempImage = new ImageView(this);
            File myPath = new File(directory, images[i].getName());
            tempImage.setImageDrawable(Drawable.createFromPath(myPath.toString()));
            imageList.put(myPath, tempImage);
        }
        if (imageList.size() % 3 > 0) {
            rows = (imageList.size() / 3) + 1;
        }
        else
        {
            rows = imageList.size()/3;
        }
        List<ImageView> thumbnails = new ArrayList<ImageView>(imageList.values());

        Collections.reverse(thumbnails);
        for(int i = 0; i < rows; i++)
        {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            table.addView(row);

            for(int j = 0;  j < 3; j++)
           {
                ImageView tempImage = new ImageView(this);
                tempImage.setImageDrawable(thumbnails.get(count).getDrawable());
                tempImage.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tempImage.setOnClickListener(onClick);

                row.addView(tempImage);
                count++;
                if(count == thumbnails.size())
                {
                    break;
                }

           }
        }
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View myView) {

            String path = "";
            ImageView image = (ImageView)myView;
            //Bitmap b1 = ((BitmapDrawable)image.getDrawable()).getBitmap();
            for(Map.Entry<File, ImageView> entry: imageList.entrySet())
            {
                File key = entry.getKey();
                ImageView value = entry.getValue();
                if(image.getDrawable().equals(value.getDrawable()))
                {
                    path = key.toString();
                }
            }

            Intent intent = new Intent(getApplicationContext(), ImagePreview.class);
            intent.putExtra("preview", path);
            startActivity(intent);
        }
    };

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

                    Intent intent = new Intent(this, CanvasPage.class);
                    intent.putExtra("Edit", selectedImageUri.getPath());
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
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