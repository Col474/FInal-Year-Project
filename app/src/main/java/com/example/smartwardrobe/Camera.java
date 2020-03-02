package com.example.smartwardrobe;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Camera extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public ImageView click_image_id;
    public TextView imageDetails;
    public Button add_item_id;
    public Button add_to_wardrobe;
    public Bitmap imageBitmap = null;
    public String clothingCategory;
    public String color;
    public RadioGroup predictions;
    public RadioButton firstPredict;
    public RadioButton secondPredict;
    public RadioButton thirdPredict;
    public Spinner otherPredict;
    public EditText labelInput;
    public ArrayList<String> categories;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Ask for Camera Permissions
        super.onCreate(savedInstanceState);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else{
            openCamera(); //If permission granted -open camera
        }

        //set up activity elements
        setContentView(R.layout.activity_camera);
        click_image_id = findViewById(R.id.imageView);
        add_item_id = findViewById((R.id.addNewItem));
        add_item_id.setVisibility(View.GONE);
        add_to_wardrobe = findViewById((R.id.addWardrobe));
        add_to_wardrobe.setVisibility((View.GONE));
        predictions = findViewById((R.id.predictions));
        firstPredict = findViewById(R.id.firstPrediction);
        secondPredict = findViewById(R.id.secondPrediction);
        thirdPredict = findViewById(R.id.thirdPrediction);
        labelInput = findViewById(R.id.addLabel);


        //set up spinner for other prediction options
        otherPredict = findViewById(R.id.predictOther);
        categories = (ArrayList<String>) getIntent().getSerializableExtra("categories"); //get categories array

        predictions.setVisibility(View.GONE);
        imageDetails = findViewById(R.id.imageClothingDetails);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //Request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                openCamera();
            }
        }
    }
    //open camera app using intent
    public void openCamera() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

    public void onAddItem(View view){
       openCamera();
       imageDetails.setText("");
    }

    public void onSaveItem(View view) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String label = labelInput.getText().toString();

        if (label.matches("")) {
            String text = "Plese enter item label";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        } else if (predictions.getCheckedRadioButtonId() == -1) {
            String text = "Please select item category";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        } else if (MainActivity.clothingCollection.clothingItemDao().getItemByName(label) == 1) {
            String text = "Item with that label allready exits. Please create new label";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
         else{
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File mypath=new File(directory,label+".jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
             String imageLocation = directory.getAbsolutePath();
             int selectedButton = predictions.getCheckedRadioButtonId();
             RadioButton selectedCategory = findViewById(selectedButton);
             String category = (String)selectedCategory.getText();

             ClothingItem newItem = new ClothingItem(label,imageLocation,category,color);
             MainActivity.clothingCollection.clothingItemDao().insert(newItem);

             String text = "Item saved to wardrobe";
             Toast toast = Toast.makeText(context,text,duration);
             toast.show();
         }

    }

    //Output Model prediction results to radio buttons
    public void showResults(List<Classifier.Recognition>results){
        if (results != null && results.size() >= 3) {
            Classifier.Recognition recognition = results.get(0);
            if (recognition != null) {
                if (recognition.getTitle() != null) firstPredict.setText(recognition.getTitle());
                clothingCategory = recognition.getTitle();
            }

            Classifier.Recognition recognition1 = results.get(1);
            if (recognition1 != null) {
                if (recognition1.getTitle() != null) secondPredict.setText(recognition1.getTitle());
            }

            Classifier.Recognition recognition2 = results.get(2);
            if (recognition2 != null) {
                if (recognition2.getTitle() != null) thirdPredict.setText(recognition2.getTitle());
            }
        }
    }

    //Classify bitmap using the model
    public void classify(Bitmap input) throws IOException {
        Classifier classifier;
        classifier = Classifier.create(this, Classifier.Device.CPU,1);
         Bitmap convertedBitmap = input.copy(Bitmap.Config.ARGB_8888,true);
        final List<Classifier.Recognition> results = classifier.recognizeImage(convertedBitmap, 0);
        Log.d("Info","Recognition Complete");
        showResults(results);
        Log.d("info","printed Results");
        classifier.close();
    }

    //kill ativity if user backs out
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //process after camera takes image
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //get bitmap
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            //get colour of centre pixel of bitmap (clothing)
            int pixel = imageBitmap.getPixel(imageBitmap.getHeight()/2,imageBitmap.getWidth()/2);
            int redValue = Color.red(pixel);
            int blueValue = Color.blue(pixel);
            int greenValue = Color.green(pixel);
            color = GetClothingColor.getColorNameFromRgb(redValue,greenValue,blueValue);
            Log.d("info","Starting Recognition");
            Log.d("info", String.valueOf(imageBitmap));
            try {
                classify(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("info","Classifying");

            //make activity elements visable
            imageDetails.append("Colour ="+color);
            click_image_id.setImageBitmap(imageBitmap);
            add_item_id.setVisibility(View.VISIBLE);
            predictions.setVisibility(View.VISIBLE);
            add_to_wardrobe.setVisibility((View.VISIBLE));
            }
        }
}
