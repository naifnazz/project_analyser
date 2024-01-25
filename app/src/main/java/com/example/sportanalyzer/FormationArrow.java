package com.example.sportanalyzer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class FormationArrow {
    Paint  linePaint;
  public   int ARROW_TYPE;
     public static final  int DEFAULT=6,ZIGZAG=4,DASH=7;
    ArrayList<Arrow> arrayList;

    public FormationArrow() {
        arrayList=new ArrayList<>();
        ARROW_TYPE=DEFAULT;

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(10); // Adjust the stroke width as needed
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public class Arrow{
        int arrowType;
        int startX,startY,endX,endY;

        public Arrow(int arrowType) {
            this.arrowType = arrowType;
        }
        public void setStartingPoint(int x,int y){
            startX=x;
            startY=y;
        }
        public void setEndingPoint(int x,int y){
            endX=x;
            endY=y;
        }
    }
    Arrow arrow;

    public void drawArrow(Canvas canvas,int x,int y){

        for(Arrow arro:arrayList){
            canvas.drawLine(arro.startX,arro.startY,arro.endX,arro.endY,linePaint);
        }

        if(arrow==null){
            arrow=new Arrow(DEFAULT);
         Paint   paint = new Paint();
            paint.setColor(Color.BLUE); // Set the color of the circle
            paint.setStyle(Paint.Style.FILL); // Set to fill the circle
            paint.setAntiAlias(true);
            arrow.setStartingPoint(x,y);
            canvas.drawCircle(x,y,20,paint);
        }else{
            arrow.setEndingPoint(x,y);

            canvas.drawLine(arrow.startX,arrow.startY,arrow.endX,arrow.endY,linePaint);
            Arrow arrow1=new Arrow(arrow.arrowType);
            arrow1.setStartingPoint(arrow.startX,arrow.startY);
            arrow1.setEndingPoint(arrow.endX, arrow.endY);
            arrayList.add(arrow1);
            arrow=null;
        }



    }

}
