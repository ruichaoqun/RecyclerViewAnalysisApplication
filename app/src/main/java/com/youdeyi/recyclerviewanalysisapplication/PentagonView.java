package com.youdeyi.recyclerviewanalysisapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Rui Chaoqun
 * @date :2020/7/8 14:15
 * description:
 */
public class PentagonView extends androidx.appcompat.widget.AppCompatTextView {
    private Paint mPaint;
    private int color = Color.CYAN;
    private Path mPath;

    public PentagonView(Context context) {
        super(context);
        init();
    }

    public PentagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setPathEffect(new CornerPathEffect(5));
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPath == null){
            mPath = new Path();
        }
        mPath.reset();
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        mPath.moveTo(0,height/2);
        mPath.lineTo(width/4,height);
        mPath.lineTo(width*3/4,height);
        mPath.lineTo(width,height/2);
        mPath.lineTo(width*3/4,0);
        mPath.lineTo(width/4,0);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
        super.onDraw(canvas);
    }
}

