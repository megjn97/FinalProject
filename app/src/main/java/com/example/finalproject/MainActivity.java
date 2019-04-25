package com.example.finalproject;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

//Images icons in app found at:
//https://www.publicdomainpictures.net/pictures/190000/nahled/the-sun-4-14685111738iS.jpg
//http://res.freestockphotos.biz/pictures/15/15143-illustration-of-a-stormy-cloud-with-light-rain-pv.png
public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase database;
    private LinearLayout mainMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainMenu = findViewById(R.id.mainScreen);

        database = this.openOrCreateDatabase("ProjectDatabase", Context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS Photos (photoID INT PRIMARY KEY, photo BLOB)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Look (lookID INT PRIMARY KEY, title TEXT, longDescription TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Product (productID INT PRIMARY KEY, exDate DATE, description TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Category (categoryID INT PRIMARY KEY, name TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS PhotoProductPair(photoProductPairID INT PRIMARY KEY, photoID INT, productID INT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS ProductCategoryPair(productCategoryPairID INT PRIMARY KEY, productID INT, categoryID INT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS PhotoLookPair(photoLookPairID INT PRIMARY KEY, photoID INT, lookID INT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS ProductLookPair(productLookPairID INT PRIMARY KEY, productID INT, lookID INT)");

       //See MyJobService for information about Notification and JobScheduler tutorial/source
        JobScheduler j = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        //for demo, setPeriodic(900000)
        j.schedule(new JobInfo.Builder(99999, new ComponentName(this, MyJobService.class)).setPeriodic(86400000).build());


        Cursor check = database.rawQuery("SELECT * FROM Category", null);
        if(!check.moveToNext()) {
            ContentValues cv = new ContentValues();
            cv.put("categoryID", 0);
            cv.put("name", "Lip Product");
            database.insert("Category", null, cv);

            cv = new ContentValues();
            cv.put("categoryID", 1);
            cv.put("name", "Eye Product");
            database.insert("Category", null, cv);

            cv = new ContentValues();
            cv.put("categoryID", 2);
            cv.put("name", "Skin Product");
            database.insert("Category", null, cv);
        }
    }


    public static void printDataBase(){
        Cursor c = database.rawQuery("SELECT * FROM Photos",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "Photos: photoID: " + c.getInt(0) +" photo: "+ c.getBlob(1));
        }
        //database.execSQL("CREATE TABLE IF NOT EXISTS Look (lookID INT PRIMARY KEY, title TEXT, longDescription TEXT)");
        c = database.rawQuery("SELECT * FROM Look",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "Look: lookID: " + c.getInt(0) +" title: "+ c.getString(1) + " logDescription: " + c.getString(2));
        }

        //database.execSQL("CREATE TABLE IF NOT EXISTS Product (productID INT PRIMARY KEY, exDate DATE, description TEXT)");
        c = database.rawQuery("SELECT * FROM Product",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "Product: productID: " + c.getInt(0) +" exDate: "+ c.getString(1) + " description: " + c.getString(2));
        }

        // database.execSQL("CREATE TABLE IF NOT EXISTS Category (categoryID INT PRIMARY KEY, name TEXT)");
        c = database.rawQuery("SELECT * FROM Category",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "Category: categoryID: " + c.getInt(0) +" name: "+ c.getString(1));
        }

       // database.execSQL("CREATE TABLE IF NOT EXISTS PhotoProductPair(photoProductPairID INT PRIMARY KEY, photoID INT, productID INT)");
        c = database.rawQuery("SELECT * FROM PhotoProductPair",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "PhotoProductPair: photoProductPairID: " + c.getInt(0) +" photoID: "+ c.getInt(1) + " productID: " + c.getInt(2));
        }
       // database.execSQL("CREATE TABLE IF NOT EXISTS ProductCategoryPair(productCategoryPairID INT PRIMARY KEY, productID INT, categoryID INT)");
        c = database.rawQuery("SELECT * FROM ProductCategoryPair",null);
        while (c.moveToNext()){
            Log.v("MYFINALPROJECT", "ProductCategoryPair: photoProductPairID: " + c.getInt(0) +" productID: "+ c.getInt(1) + " categoryID: " + c.getInt(2));
        }

        // database.execSQL("CREATE TABLE IF NOT EXISTS PhotoLookPair(photoLookPairID INT PRIMARY KEY, photoID INT, lookID INT)");
       // database.execSQL("CREATE TABLE IF NOT EXISTS ProductLookPair(productLookPairID INT PRIMARY KEY, productID INT, lookID INT)");
    }

    public void addProduct(View v){
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }

    public void viewMyProducts(View v){
        Intent i = new Intent(this, MyProducts.class);
        startActivity(i);
    }

    public void addLook(View v){
        Intent i = new Intent(this, AddLookActivity.class);
        startActivity(i);
    }
    public void viewMyLooks(View v){
        Intent i = new Intent(this, MyLooks.class);
        startActivity(i);
    }
}
