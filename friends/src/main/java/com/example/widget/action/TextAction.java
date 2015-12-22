package com.example.widget.action;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

public class TextAction extends Action{

	public String text;
	public float fontSize; 
	
	public TextAction( String text, float size, int color, float x, float y){
		this.text = text;
		fontSize = size;
		this.color = color;
		startX = x;
		startY = y;
		
		mPaint.setColor(color);
		mPaint.setTextSize(size);
		mPaint.setTextAlign( Align.CENTER);
		mPaint.setPathEffect(null);
	}
	
	@Override
	public void draw( Canvas canvas ){
		super.draw(canvas);
		
		canvas.drawText( text, startX, startY, mPaint);
	}
	
	@Override
	public void resize(float sx, float sy, float ex, float ey) {
	}

	public void setTextSize( float size ){
		mPaint.setTextSize(size);
	}
	public float getTextSize(){
		return mPaint.getTextSize();
	}
}
