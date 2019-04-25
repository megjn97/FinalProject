package com.example.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class AddProductActivity extends AppCompatActivity implements SensorEventListener {

    private Bitmap currBitmap;
    private EditText name;
    private CheckBox isLipProduct;
    private CheckBox isSkinProduct;
    private CheckBox isEyeProduct;
    private DatePicker expDate;

    private int currPhotoID;
    private int currProductID;
    private int currProductCategoryPairID;
    private int currProductPhotoPairID;

    private SensorManager sm;
    private Sensor s;

    private ImageView lightAnimation;
    private TextView lightAnimationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(this, s, 1000000);
        lightAnimation = findViewById(R.id.LightAnimation);
        lightAnimationText = findViewById(R.id.LightAnimationText);

        currBitmap = null;
        name = findViewById(R.id.productName);
        isLipProduct = findViewById(R.id.lipProduct);
        isSkinProduct = findViewById(R.id.skinProduct);
        isEyeProduct = findViewById(R.id.eyeProduct);
        expDate = findViewById(R.id.exDate);

        Cursor c = MainActivity.database.rawQuery("SELECT MAX(photoID) FROM Photos", null);
        if(c.moveToNext()) {
            currPhotoID = c.getInt(0) + 1;
        }else{
            currPhotoID = 0;
        }

        c = MainActivity.database.rawQuery("SELECT MAX(productID) FROM Product", null);
        if(c.moveToNext()){
        currProductID = c.getInt(0) +1;
        }else{
            currProductID = 0;
        }
        c = MainActivity.database.rawQuery("SELECT MAX(productCategoryPairID) FROM ProductCategoryPair", null);
        if(c.moveToNext()){
            currProductCategoryPairID = c.getInt(0) + 1;
        }else{
            currProductCategoryPairID = 0;
        }


        c = MainActivity.database.rawQuery("SELECT MAX(photoProductPairID) FROM PhotoProductPair", null);
        if(c.moveToNext()){
            currProductPhotoPairID = c.getInt(0) + 1;
        }else{
            currProductPhotoPairID = 0;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent x)
    {
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Bundle extras = x.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            currBitmap = imageBitmap;
            ImageView img = (ImageView) findViewById(R.id.ProductImage);
            img.setImageBitmap(imageBitmap);

        }
    }
    public void take_picture(View v){
        Intent w = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(w, 1);
    }

    public void saveProduct(View v) {
        String enteredName = name.getText().toString();
        if (enteredName == "" || enteredName == null) {
            Toast.makeText(getApplicationContext(), "You must enter a product name", Toast.LENGTH_SHORT);
            return;
        }
        boolean eIsLipProduct = isLipProduct.isChecked();
        boolean eIsEyeProduct = isEyeProduct.isChecked();
        boolean eIsSkinProduct = isSkinProduct.isChecked();

        Log.v("MYFINALPROJECT", " " + eIsEyeProduct + " " + eIsLipProduct +" " + eIsSkinProduct);

        int day = expDate.getDayOfMonth();
        int month = (expDate.getMonth()+1)%12;
        int year = expDate.getYear();

        ContentValues productCV = new ContentValues();
        productCV.put("productID", currProductID);
        productCV.put("exDate", "" + year + "-" + month + "-" + day);
        productCV.put("description", enteredName);
        MainActivity.database.insert("Product", null, productCV);

        if(eIsLipProduct) {
            ContentValues catCV = new ContentValues();
            catCV.put("productCategoryPairID", currProductCategoryPairID);
            catCV.put("productID", currProductID);
            catCV.put("categoryID", 0);
            currProductCategoryPairID++;
            MainActivity.database.insert("ProductCategoryPair", null, catCV);
        }

        if(eIsEyeProduct) {
            ContentValues catCV = new ContentValues();
            catCV.put("productCategoryPairID", currProductCategoryPairID);
            catCV.put("productID", currProductID);
            catCV.put("categoryID", 1);
            currProductCategoryPairID++;
            MainActivity.database.insert("ProductCategoryPair", null, catCV);
        }

        if(eIsSkinProduct) {
            ContentValues catCV = new ContentValues();
            catCV.put("productCategoryPairID", currProductCategoryPairID);
            catCV.put("productID", currProductID);
            catCV.put("categoryID", 2);
            currProductCategoryPairID++;
            MainActivity.database.insert("ProductCategoryPair", null, catCV);
        }

        if (currBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] ba = stream.toByteArray();
            ContentValues photoCV = new ContentValues();
            photoCV.put("photoID", currPhotoID);
            photoCV.put("photo", ba);
            MainActivity.database.insert("Photos", null, photoCV);

            ContentValues photoProduct = new ContentValues();
            photoProduct.put("photoProductPairID", currProductPhotoPairID);
            photoProduct.put("photoID", currPhotoID);
            photoProduct.put("productID", currProductID);
            MainActivity.database.insert("PhotoProductPair", null, photoProduct);
        }

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float val = event.values[0];
        if(val > 10){
            lightAnimation.setBackgroundResource(R.drawable.light);
            lightAnimationText.setText("Great Lighting! You're picture perfect!");
        }else{
            lightAnimation.setBackgroundResource(R.drawable.dark);
            lightAnimationText.setText("It's a little dark. Consider finding more light to get a great photo!");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
