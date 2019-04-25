package com.example.finalproject;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyProducts extends AppCompatActivity {

    LinearLayout productView;
    private CheckBox isLipProduct;
    private CheckBox isSkinProduct;
    private CheckBox isEyeProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);
        productView = findViewById(R.id.MyProductView);
        isEyeProduct =findViewById(R.id.fEyeProduct);
        isSkinProduct = findViewById(R.id.fSkinProduct);
        isLipProduct = findViewById(R.id.fLipProduct);

        Cursor c = MainActivity.database.rawQuery("SELECT Pr.productID, Pr.description, P.photo, Pr.exDate FROM Product Pr, Photos P, PhotoProductPair PPP WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID", null);
        loadPictures(c);
    }

    private void loadPictures(Cursor c){
        int i =0;
        while(c.moveToNext()) {
            Log.v("MYFINALPROJECT", "LOAD PICTURES: In the while loop");
            Log.v("MYFINALPROJECT", "LOAD PICTURES: In the while loop still");

            TextView name = new TextView(this);
            name.setText("" + c.getString(1));
            name.setTextSize(20);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setPadding(0,20,0,10);
            productView.addView(name);

            byte[] b = c.getBlob(2);
            Bitmap myBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            ImageView img = new ImageView(this);
            img.setImageBitmap(myBitmap);
            img.setMinimumWidth(500);
            img.setMinimumHeight(500);
            productView.addView(img);

            i++;
            String[] args = {c.getString(0)};
            Cursor cat = MainActivity.database.rawQuery("SELECT C.name FROM Category C, ProductCategoryPair PC, Product P WHERE C.categoryID = PC.categoryID AND P.productID = PC.productID and P.productID = ?", args);
            String category = "Categories:";
            while (cat.moveToNext()) {
                category += " " + cat.getString(0) + ",";
            }
            TextView cats = new TextView(this);
            cats.setText(category);
            cats.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            productView.addView(cats);

            TextView exp = new TextView(this);
            exp.setText("Expiration Date: " + c.getString(3));
            exp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            productView.addView(exp);
        }
    }
    public void filter(View v){
        boolean lip = isLipProduct.isChecked();
        boolean skin = isSkinProduct.isChecked();
        boolean eye = isEyeProduct.isChecked();
        if(lip && skin && eye){
            Cursor c = MainActivity.database.rawQuery("SELECT Pr.productID, Pr.description, P.photo, Pr.exDate FROM Product Pr, Photos P, PhotoProductPair PPP WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID", null);
            productView.removeAllViews();;
            loadPictures(c);
            return;
        }
        if(lip && skin && !eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND (PC.categoryID = 0 OR PC.categoryID = 2)", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }
        if(lip && !skin && !eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND PC.categoryID = 0", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }

        if(!lip && skin && !eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND PC.categoryID = 2", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }
        if(!lip && !skin && eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND PC.categoryID = 1", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }
        if(lip && !skin && eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND (PC.categoryID = 0 OR PC.categoryID = 1)", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }
        if(!lip && skin && eye){
            Cursor c = MainActivity.database.rawQuery("SELECT DISTINCT Pr.productID, Pr.description, P.photo, Pr.exDate " +
                    "FROM Product Pr, Photos P, PhotoProductPair PPP, ProductCategoryPair PC " +
                    "WHERE Pr.productID = PPP.productID AND P.photoID = PPP.photoID AND PC.productID = Pr.productID AND (PC.categoryID = 2 OR PC.categoryID = 1)", null);
            productView.removeAllViews();
            loadPictures(c);
            return;
        }
    }
}
