package com.example.fellowtraveler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jjoe64.graphview.GraphView;

public class CustomGraphView extends GraphView {

    private Paint paint;
    private float tapX = -1;

    public CustomGraphView(Context context) {
        super(context);
        //init();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
    }

    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                super.onTouchEvent(event);
                tapX = event.getX();
                invalidate(); // Redraw the graph
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                 // Redraw the graph
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tapX != -1) {
            // Draw vertical line at tapX
            canvas.drawLine(tapX, 0, tapX, getHeight(), paint);
        }
    }
}