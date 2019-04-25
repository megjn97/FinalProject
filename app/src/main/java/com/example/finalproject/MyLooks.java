package com.example.finalproject;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyLooks extends AppCompatActivity {

    LinearLayout MyLooksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_looks);

        MyLooksView = findViewById(R.id.LookView);

        Cursor looks = MainActivity.database.rawQuery("SELECT L.lookID, L.title, L.longDescription, P.photo FROM Look L, Photos P, PhotoLookPair PL WHERE" +
                " P.photoID = PL.photoID AND L.lookID = PL.lookID", null);
        while(looks.moveToNext()){

            LinearLayout lookView = new LinearLayout(this);
            lookView.setOrientation(LinearLayout.VERTICAL);
            lookView.setPadding(10, 10, 10, 10);

            TextView title = new TextView(this);
            title.setText(looks.getString(1));
            title.setTextSize(20);
            title.setTextColor(Color.DKGRAY);
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lookView.addView(title);

            byte[] b = looks.getBlob(3);
            Bitmap myBitmap = BitmapFactory.decodeByteArray(b, 0,b.length);
            ImageView img = new ImageView(this);
            img.setImageBitmap(myBitmap);
            img.setMinimumWidth(500);
            img.setMinimumHeight(500);
            img.setPadding(10,10,10,10);
            lookView.addView(img);

            TextView description = new TextView(this);
            description.setText(looks.getString(2));
            description.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lookView.addView(description);

            TextView label = new TextView(this);
            label.setText("Products: ");
            label.setTextSize(20);
            lookView.addView(label);

            String[] args = {"" + looks.getInt(0)};

            Cursor products = MainActivity.database.rawQuery("SELECT P.exDate, P.description FROM Product P, Look L, ProductLookPair PL WHERE" +
                    " L.lookID = PL.lookID AND P.productID = PL.productID AND L.lookID = ?", args);
            while (products.moveToNext()){
                LinearLayout productView = new LinearLayout(this);
            productView.setOrientation(LinearLayout.VERTICAL);
                TextView productTitle = new TextView(this);
                productTitle.setText(products.getString(1));
                productTitle.setPadding(10,10, 10, 5);
                TextView date = new TextView(this);
                date.setText("ex: " +products.getString(0));
                date.setPadding(10,0,10,0);
                productView.addView(productTitle);
                productView.addView(date);
                lookView.addView(productView);
            }
            MyLooksView.addView(lookView);
        }
    }
}
