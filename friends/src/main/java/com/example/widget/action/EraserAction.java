package com.example.widget.action;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class EraserAction extends Action {
	
	private Path path = null;
	private float size;
	private int color = 0;

	public EraserAction(float startX, float startY, float size, int color) {
		//super(startX, startY, size, color);
		// TODO Auto-generated constructor stub
		this.color = color;
		this.size = size;
		this.path = new Path();
		this.startX = startX;
		this.startY = startY;
		
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(this.size);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		
		path.moveTo(startX, startY);
		path.quadTo(startX, startY, (startX + endX) / 2, (startY + endY) / 2);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		canvas.drawPath(this.path, mPaint);
	}

	@Override
	public void move(float x, float y) {
		super.move(x, y);
		this.path.quadTo(x, y, (startX + x) / 2, (startY + y) / 2);
	}

}
