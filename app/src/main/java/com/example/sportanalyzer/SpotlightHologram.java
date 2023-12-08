package com.example.sportanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class SpotlightHologram extends View {
    Paint paint;
    float x,y;

    Bitmap bitmap;
    Canvas mycanvas;
    Rect destRect;
    public SpotlightHologram(Context context,Bitmap bitmap) {
        super(context);

       this. bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn);
        int bitmapWidth = 200; // Set the desired width
        int bitmapHeight = 200; // Set the desired height

         destRect = new Rect((int) x, (int) y, (int) x + bitmapWidth, (int) y + bitmapHeight);
        paint = new Paint();
        paint.setAntiAlias(true); // Smooth edges

    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;

    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if(bitmap!=null){

             canvas.drawBitmap(bitmap,x,y,paint);
        }

        mycanvas=canvas;

      // canvas.drawCircle(x, y, 100, paint);
        super.onDraw(canvas);
    }

    public void setHologramXY(float x,float y){
      this.x=x;
      this.y=y;
       if(mycanvas!=null)
        mycanvas.drawBitmap(bitmap,x,y,paint);
        invalidate();
    }
}
