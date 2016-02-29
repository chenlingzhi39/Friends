package com.example.widget.action;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectAction extends Action{
	
	private float size;
	private int color = 0;
	
	public RectAction(int color) {
		//super(color);
		this.color = color;
	}

	public RectAction(float startX, float startY, float size, int color) {
		//super(startX, startY, size, color);
		// TODO Auto-generated constructor stub
		this.color = color;
		this.startX = startX;
		this.startY = startY;
		this.endX = startX;
		this.endY = startY;
		this.size = size;
		
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(this.color);
		mPaint.setStrokeWidth(this.size);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		canvas.drawRect(startX, startY, endX, endY, mPaint);
	}

	@Override
	public void move(float x, float y) {
		// TODO Auto-generated method stub
		super.move(x, y);
		this.endX = x;
		this.endY = y;
	}

}
