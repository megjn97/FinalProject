package com.example.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddLookActivity extends AppCompatActivity implements SensorEventListener {

    private LinearLayout lookProducts;
    private List<Integer> productIDs;
    private List<CheckBox> boxes;
    private Bitmap currBitmap;

    private int currLookID;
    private int currProductLookPairID;
    private int currPhotoID;
    private int currPhotoLookPairID;

    private ImageView lookLightAnimation;
    private TextView lookLightAnimationText;

    private SensorManager sm;
    private Sensor s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_look);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(this, s, 1000000);
        lookLightAnimation = findViewById(R.id.LookLightAnimation);
        lookLightAnimationText = findViewById(R.id.LookLightAnimationText);

        lookProducts =findViewById(R.id.look_products);
        productIDs = new ArrayList<Integer>();
        boxes = new ArrayList<CheckBox>();

        Cursor m = MainActivity.database.rawQuery("SELECT MAX(lookID) FROM Look", null);
        if(m.moveToNext()) {
            currLookID = m.getInt(0) + 1;
        }else{
            currLookID = 0;
        }

        m = MainActivity.database.rawQuery("SELECT MAX(productLookPairID) FROM ProductLookPair", null);
        if(m.moveToNext()) {
            currProductLookPairID = m.getInt(0) + 1;
        }else{
            currProductLookPairID = 0;
        }

        m = MainActivity.database.rawQuery("SELECT MAX(photoID) FROM Photos", null);
        if(m.moveToNext()) {
            currPhotoID = m.getInt(0) + 1;
        }else{
            currPhotoID = 0;
        }

        m = MainActivity.database.rawQuery("SELECT MAX(photoLookPairID) FROM PhotoLookPair", null);
        if(m.moveToNext()) {
            currPhotoLookPairID = m.getInt(0) + 1;
        }else{
            currPhotoLookPairID = 0;
        }

        currBitmap = null;

        Cursor c  = MainActivity.database.rawQuery("SELECT * FROM Category", null);
        while (c.moveToNext()){
            String[] args = {"" + c.getInt(0)};
            Cursor p = MainActivity.database.rawQuery("SELECT P.description, P.productID FROM Product P, Category C, ProductCategoryPair CP WHERE CP.productID = P.productID AND CP.categoryID = C.categoryID and C.categoryID = ?", args);
            TextView text = new TextView(this);
            text.setText(c.getString(1) + "s");
            lookProducts.addView(text);
            while(p.moveToNext()){
                CheckBox check = new CheckBox(this);
                check.setText(p.getString(0));
                lookProducts.addView(check);
                productIDs.add(p.getInt(1));
                boxes.add(check);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent x)
    {
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Bundle extras = x.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            currBitmap = imageBitmap;
            ImageView img = (ImageView) findViewById(R.id.LookPhoto);
            img.setImageBitmap(imageBitmap);
        }
    }
    public void addLookPhoto(View v){
        Intent w = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(w, 1);
    }

    public void saveLook(View v){
        EditText title = findViewById(R.id.LookTitle);
        EditText description = findViewById(R.id.LookDescription);
        ContentValues lookCV = new ContentValues();
        lookCV.put("lookID", currLookID);
        lookCV.put("title", title.getText().toString());
        lookCV.put("longDescription", description.getText().toString());
        MainActivity.database.insert("Look", null,lookCV);

        for(int i = 0; i < productIDs.size(); i++){
            if(boxes.get(i).isChecked()){
                String[] args = {""+productIDs.get(i), ""+currLookID};
                Cursor check = MainActivity.database.rawQuery("SELECT * FROM ProductLookPair P WHERE P.productID = ? AND P.lookID = ?", args);
                if(!check.moveToNext()) {
                    ContentValues plCV = new ContentValues();
                    plCV.put("productLookPairID", currProductLookPairID);
                    plCV.put("productID", productIDs.get(i));
                    plCV.put("lookID", currLookID);
                    MainActivity.database.insert("ProductLookPair",null, plCV);
                    currProductLookPairID++;
                }
            }
        }

        if (currBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] ba = stream.toByteArray();
            ContentValues photoCV = new ContentValues();
            photoCV.put("photoID", currPhotoID);
            photoCV.put("photo", ba);
            MainActivity.database.insert("Photos", null, photoCV);

            ContentValues photoLook = new ContentValues();
            photoLook.put("photoLookPairID", currPhotoLookPairID);
            photoLook.put("photoID", currPhotoID);
            photoLook.put("lookID", currLookID);
            MainActivity.database.insert("PhotoLookPair", null, photoLook);
        }

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float val = event.values[0];
        if(val > 10){
            lookLightAnimation.setBackgroundResource(R.drawable.light);
            lookLightAnimationText.setText("Great Lighting! You're picture perfect!");
        }else{
            lookLightAnimation.setBackgroundResource(R.drawable.dark);
            lookLightAnimationText.setText("It's a little dark. Consider finding more light to get a great photo!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
